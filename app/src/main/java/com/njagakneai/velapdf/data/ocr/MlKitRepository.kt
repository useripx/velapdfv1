package com.njagakneai.velapdf.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.njagakneai.velapdf.domain.model.ExtractedText
import com.njagakneai.velapdf.domain.model.OcrPageText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositori ekstraksi teks OCR Luring (Google ML Kit Text Recognition)
 * untuk sumber citra multi-halaman dan berkas PDF (menggunakan PdfRenderer di Dispatchers.IO).
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

                ExtractedText(
                    pageNumber = pageNumber,
                    rawText = result.text,
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
                pages.add(
                    OcrPageText(
                        pageNumber = pageNum,
                        rawText = visionText.text,
                        confidence = 0.95f,
                        lineCount = visionText.textBlocks.sumOf { it.lines.size }
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
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            tempFile = copyPdfToTempFile(pdfUri)
            pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)

            val totalPdfPages = renderer.pageCount
            if (totalPdfPages <= 0) {
                return@withContext Result.failure(IllegalStateException("Dokumen PDF kosong (0 halaman)."))
            }

            val clampedStart = startPage.coerceIn(1, totalPdfPages)
            val requestedEnd = (endPage ?: totalPdfPages).coerceIn(clampedStart, totalPdfPages)
            val cappedEnd = minOf(requestedEnd, clampedStart + maxPagesLimit - 1)
            val pagesToProcess = (clampedStart - 1) until cappedEnd
            val totalSelected = cappedEnd - clampedStart + 1

            val results = mutableListOf<OcrPageText>()
            pagesToProcess.forEachIndexed { idx, pageIdx ->
                onProgress(idx + 1, totalSelected)

                val page = renderer.openPage(pageIdx)
                val targetWidth = (page.width * 2).coerceIn(800, MAX_RENDER_WIDTH_PX)
                val scaleFactor = targetWidth.toFloat() / page.width.coerceAtLeast(1)
                val targetHeight = (page.height * scaleFactor).toInt().coerceAtLeast(100)

                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                try {
                    bitmap.eraseColor(Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                    val visionText = recognizer.process(inputImage).await()
                    results.add(
                        OcrPageText(
                            pageNumber = pageIdx + 1,
                            rawText = visionText.text,
                            confidence = 0.95f,
                            lineCount = visionText.textBlocks.sumOf { it.lines.size }
                        )
                    )
                } finally {
                    page.close()
                    bitmap.recycle()
                }
            }
            Result.success(results)
        } catch (e: SecurityException) {
            Result.failure(SecurityException("PDF terkunci kata sandi/DRM sehingga tidak dapat diekstrak.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengekstrak teks PDF: ${e.localizedMessage ?: "error"}", e))
        } finally {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            try { tempFile?.delete() } catch (_: Exception) {}
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

    companion object {
        const val DEFAULT_MAX_PAGES_PER_SESSION = 35
        private const val MAX_RENDER_WIDTH_PX = 1600
    }
}
