package com.njagakneai.velapdf.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.graphics.createBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itextpdf.kernel.exceptions.BadPasswordException
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory
import com.itextpdf.kernel.utils.XmlProcessorCreator
import com.njagakneai.velapdf.domain.model.ExtractedText
import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.domain.nlp.TextExtractionCleaner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.TransformerFactory

/**
 * Repositori ekstraksi teks luring hibrida untuk dokumen gambar & PDF:
 * 1. Untuk berkas PDF: membaca teks native via iText7 (PdfTextExtractor) dengan akurasi karakter tinggi
 *    serta fallback otomatis ke bitmap PdfRenderer + Google ML Kit OCR jika halaman berupa hasil scan gambar.
 * 2. Untuk foto kamera / galeri / halaman scan: Google ML Kit OCR + pengurutan blok sadar-kolom
 *    & pembersihan kata terpenggal baris.
 */
@Singleton
class MlKitRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextFromUri(pageNumber: Int, uri: Uri): ExtractedText =
        withContext(Dispatchers.IO) {
            try {
                val image = InputImage.fromFilePath(context, uri)
                val result = recognizer.process(image).await()
                val cleanedText = formatVisionText(result)

                ExtractedText(
                    pageNumber = pageNumber,
                    rawText = cleanedText,
                    isSuccess = true
                )
            } catch (e: Exception) {
                ExtractedText(
                    pageNumber = pageNumber,
                    rawText = "",
                    isSuccess = false,
                    errorMessage = e.message ?: "Gagal mengekstrak teks"
                )
            }
        }

    suspend fun extractPagesFromImageUris(
        uris: List<Uri>,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): Result<List<OcrPageText>> = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Tidak ada gambar yang dipilih untuk OCR."))
        }
        val pages = mutableListOf<OcrPageText>()
        try {
            uris.forEachIndexed { index, uri ->
                val pageNum = index + 1
                onProgress(pageNum, uris.size)
                val image = InputImage.fromFilePath(context, uri)
                val visionText = recognizer.process(image).await()
                val cleanedText = formatVisionText(visionText)
                pages.add(
                    OcrPageText(
                        pageNumber = pageNum,
                        rawText = cleanedText,
                        confidence = 0.95f,
                        lineCount = cleanedText.lines().count { it.isNotBlank() }
                    )
                )
            }
            Result.success(pages)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal membaca gambar pindai: ${e.localizedMessage ?: "error"}", e))
        }
    }

    suspend fun getPdfPageCount(pdfUri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            tempFile = copyPdfToTempFile(pdfUri)
            pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)
            Result.success(renderer.pageCount)
        } catch (e: SecurityException) {
            Result.failure(SecurityException("PDF terlindungi kata sandi (terenkripsi) sehingga tidak didukung.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Tidak dapat membuka berkas PDF: ${e.localizedMessage ?: "berkas rusak"}", e))
        } finally {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            try { tempFile?.delete() } catch (_: Exception) {}
        }
    }

    suspend fun extractFromPdfUri(
        pdfUri: Uri,
        startPage: Int = 1,
        endPage: Int? = null,
        maxPagesLimit: Int = DEFAULT_MAX_PAGES_PER_SESSION,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): Result<List<OcrPageText>> = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        var pdfDoc: PdfDocument? = null
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            tempFile = copyPdfToTempFile(pdfUri)

            // Tahap 1: Coba buka via iText7 untuk mengekstrak teks digital langsung (~100% akurat).
            var reader: PdfReader? = null
            pdfDoc = try {
                reader = PdfReader(tempFile)
                PdfDocument(reader)
            } catch (e: BadPasswordException) {
                try { reader?.close() } catch (_: Exception) {}
                return@withContext Result.failure(
                    SecurityException("PDF terkunci kata sandi/DRM sehingga tidak dapat diekstrak.", e)
                )
            } catch (e: Throwable) {
                try { reader?.close() } catch (_: Exception) {}
                Log.w("MlKitRepository", "iText7 failed to open PDF: ${e.javaClass.simpleName}: ${e.message}", e)
                null
            }

            val totalPdfPages = if (pdfDoc != null) {
                pdfDoc.numberOfPages
            } else {
                pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
                renderer.pageCount
            }

            if (totalPdfPages <= 0) {
                return@withContext Result.failure(IllegalStateException("Dokumen PDF kosong (0 halaman)."))
            }

            val clampedStart = startPage.coerceIn(1, totalPdfPages)
            val requestedEnd = (endPage ?: totalPdfPages).coerceIn(clampedStart, totalPdfPages)
            val cappedEnd = minOf(requestedEnd, clampedStart + maxPagesLimit - 1)
            val totalSelected = cappedEnd - clampedStart + 1

            val results = mutableListOf<OcrPageText>()
            for ((idx, pageNumber1Based) in (clampedStart..cappedEnd).withIndex()) {
                onProgress(idx + 1, totalSelected)

                val nativeCleanText = if (pdfDoc != null) {
                    extractNativePdfPageSafely(pdfDoc, pageNumber1Based)
                } else {
                    null
                }

                if (nativeCleanText != null) {
                    results.add(
                        OcrPageText(
                            pageNumber = pageNumber1Based,
                            rawText = nativeCleanText,
                            confidence = 0.99f,
                            lineCount = nativeCleanText.lines().count { it.isNotBlank() }
                        )
                    )
                } else {
                    // Fallback aman ke PdfRenderer + ML Kit OCR untuk halaman scan gambar atau CMap rusak.
                    if (renderer == null) {
                        pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                        renderer = PdfRenderer(pfd)
                    }
                    val pageIdx0Based = (pageNumber1Based - 1).coerceIn(0, renderer.pageCount - 1)
                    val ocrText = renderPdfPageAndRunOcr(renderer, pageIdx0Based)
                    results.add(
                        OcrPageText(
                            pageNumber = pageNumber1Based,
                            rawText = ocrText,
                            confidence = 0.92f,
                            lineCount = ocrText.lines().count { it.isNotBlank() }
                        )
                    )
                }
            }
            Result.success(results)
        } catch (e: SecurityException) {
            Result.failure(SecurityException("PDF terkunci kata sandi/DRM sehingga tidak dapat diekstrak.", e))
        } catch (e: BadPasswordException) {
            Result.failure(SecurityException("PDF terkunci kata sandi/DRM sehingga tidak dapat diekstrak.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengekstrak teks PDF: ${e.localizedMessage ?: "error"}", e))
        } finally {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            try { pdfDoc?.close() } catch (_: Exception) {}
            try { tempFile?.delete() } catch (_: Exception) {}
        }
    }

    private fun extractNativePdfPageSafely(pdfDoc: PdfDocument, pageNumber1Based: Int): String? {
        return try {
            val page = pdfDoc.getPage(pageNumber1Based)
            val streamRaw = PdfTextExtractor.getTextFromPage(page, SimpleTextExtractionStrategy())
            val quickCheck = TextExtractionCleaner.chooseBestNativeExtraction(streamRaw, "")
            if (quickCheck != null) {
                quickCheck
            } else {
                val locationRaw = PdfTextExtractor.getTextFromPage(page, LocationTextExtractionStrategy())
                val best = TextExtractionCleaner.chooseBestNativeExtraction(streamRaw, locationRaw)
                if (best == null) {
                    Log.w(
                        "MlKitRepository",
                        "Page $pageNumber1Based rejected by TextExtractionCleaner (streamLen=${streamRaw.length}, locLen=${locationRaw.length})"
                    )
                }
                best
            }
        } catch (e: Throwable) {
            Log.w(
                "MlKitRepository",
                "Page $pageNumber1Based iText7 extraction failed: ${e.javaClass.simpleName}: ${e.message}",
                e
            )
            null
        }
    }

    private suspend fun renderPdfPageAndRunOcr(renderer: PdfRenderer, pageIdx0Based: Int): String {
        val page = renderer.openPage(pageIdx0Based)
        val targetWidth = (page.width * 2).coerceIn(900, MAX_RENDER_WIDTH_PX)
        val scaleFactor = targetWidth.toFloat() / page.width.coerceAtLeast(1)
        val targetHeight = (page.height * scaleFactor).toInt().coerceAtLeast(100)

        val bitmap = createBitmap(targetWidth, targetHeight)
        return try {
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val visionText = recognizer.process(inputImage).await()
            formatVisionText(visionText)
        } finally {
            page.close()
            bitmap.recycle()
        }
    }

    private fun formatVisionText(visionText: Text): String {
        val spatialBlocks = visionText.textBlocks.mapNotNull { block ->
            val box = block.boundingBox ?: return@mapNotNull null
            if (block.text.isBlank()) {
                null
            } else {
                TextExtractionCleaner.SpatialTextBlock(
                    text = block.text,
                    left = box.left,
                    top = box.top,
                    right = box.right,
                    bottom = box.bottom
                )
            }
        }
        return if (spatialBlocks.isNotEmpty()) {
            TextExtractionCleaner.orderSpatialBlocks(spatialBlocks)
        } else {
            TextExtractionCleaner.normalizeExtractedPageText(visionText.text)
        }
    }

    private fun copyPdfToTempFile(pdfUri: Uri): File {
        val tempFile = File(context.cacheDir, "ocr_render_${System.currentTimeMillis()}.pdf")
        val inputStream = context.contentResolver.openInputStream(pdfUri)
            ?: throw IllegalStateException("Gagal mengakses stream berkas PDF.")
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    /**
     * Adaptor XML aman untuk runtime Android (Expat/Harmony) agar iText7 tidak melempar
     * UnsupportedOperationException saat mengurai XMP metadata melalui DocumentBuilderFactory.setXIncludeAware.
     */
    private class AndroidSafeXmlParserFactory : DefaultSafeXmlParserFactory() {
        private val securityFeatures = listOf(
            "http://apache.org/xml/features/disallow-doctype-decl" to true,
            "http://xml.org/sax/features/external-general-entities" to false,
            "http://xml.org/sax/features/external-parameter-entities" to false,
            "http://apache.org/xml/features/nonvalidating/load-external-dtd" to false
        )

        override fun configureSafeDocumentBuilderFactory(factory: DocumentBuilderFactory) {
            securityFeatures.forEach { (feature, value) ->
                runCatching { factory.setFeature(feature, value) }
            }
            runCatching { factory.isXIncludeAware = false }
            runCatching { factory.isExpandEntityReferences = false }
        }

        override fun configureSafeSAXParserFactory(factory: SAXParserFactory) {
            securityFeatures.forEach { (feature, value) ->
                runCatching { factory.setFeature(feature, value) }
            }
            runCatching { factory.isXIncludeAware = false }
        }

        override fun configureSafeTransformerFactory(factory: TransformerFactory) {
            runCatching { factory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "") }
            runCatching { factory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "") }
        }
    }

    companion object {
        const val DEFAULT_MAX_PAGES_PER_SESSION = 35
        private const val MAX_RENDER_WIDTH_PX = 2000

        init {
            XmlProcessorCreator.setXmlParserFactory(AndroidSafeXmlParserFactory())
        }
    }
}
