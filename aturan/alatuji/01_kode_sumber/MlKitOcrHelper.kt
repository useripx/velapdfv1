package com.njagakneai.velapdf.metopen.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.njagakneai.velapdf.metopen.domain.model.OcrPageText
import com.njagakneai.velapdf.metopen.domain.model.OcrRawResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * METOPEN TA 2026/2027 - PROYEK VELAPDF
 * Mahasiswa: Yogi Ario Pratama (NPM: 2313020004)
 * 
 * Kelas Ekstraksi Teks Lokal On-Device (Google ML Kit Text Recognition)
 * Beroperasi 100% luring (offline-first) tanpa transmisi jaringan (0-byte data consumption).
 */
class MlKitOcrHelper(
    private val context: Context
) {
    // Klien ML Kit Latin lokal terkuantisasi (terintegrasi langsung dalam runtime aplikasi)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Mengekstrak teks dari citra dokumen (hasil foto kamera atau dokumen scanner).
     */
    suspend fun extractFromImageUri(documentId: String, imageUri: Uri): OcrRawResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            val visionText = recognizer.process(inputImage).await()
            val duration = System.currentTimeMillis() - startTime

            val page = OcrPageText(
                pageNumber = 1,
                rawText = visionText.text,
                confidence = 0.95f,
                lineCount = visionText.textBlocks.sumOf { it.lines.size }
            )

            OcrRawResult(
                documentId = documentId,
                pages = listOf(page),
                totalCharacters = visionText.text.length,
                processingDurationMs = duration,
                isSuccess = true
            )
        } catch (e: Exception) {
            OcrRawResult(
                documentId = documentId,
                pages = emptyList(),
                totalCharacters = 0,
                processingDurationMs = System.currentTimeMillis() - startTime,
                isSuccess = false,
                errorMessage = e.localizedMessage ?: "Kegagalan ekstraksi citra dokumen via ML Kit"
            )
        }
    }

    /**
     * Merender setiap halaman berkas PDF ke bitmap beresolusi tinggi,
     * kemudian mengekstrak teks karakter per halaman secara luring.
     */
    suspend fun extractFromPdfUri(documentId: String, pdfUri: Uri): OcrRawResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val pagesResult = mutableListOf<OcrPageText>()
        var totalChars = 0

        try {
            // Salin berkas PDF ke direktori cache sementara untuk PdfRenderer
            val tempFile = File(context.cacheDir, "temp_render_${System.currentTimeMillis()}.pdf")
            context.contentResolver.openInputStream(pdfUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(pfd)
            val pageCount = pdfRenderer.pageCount

            for (i in 0 until pageCount) {
                val page = pdfRenderer.openPage(i)
                // Render dengan resolusi optimal (2x scaling) untuk memaksimalkan akurasi pengenalan teks
                val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val visionText = recognizer.process(inputImage).await()
                bitmap.recycle()

                val pageText = visionText.text
                totalChars += pageText.length
                pagesResult.add(
                    OcrPageText(
                        pageNumber = i + 1,
                        rawText = pageText,
                        confidence = 0.95f,
                        lineCount = visionText.textBlocks.sumOf { it.lines.size }
                    )
                )
            }

            pdfRenderer.close()
            pfd.close()
            tempFile.delete()

            val duration = System.currentTimeMillis() - startTime
            OcrRawResult(
                documentId = documentId,
                pages = pagesResult,
                totalCharacters = totalChars,
                processingDurationMs = duration,
                isSuccess = true
            )
        } catch (e: Exception) {
            OcrRawResult(
                documentId = documentId,
                pages = pagesResult,
                totalCharacters = totalChars,
                processingDurationMs = System.currentTimeMillis() - startTime,
                isSuccess = false,
                errorMessage = e.localizedMessage ?: "Kegagalan pemrosesan halaman berkas PDF"
            )
        }
    }
}
