package com.njagakneai.velapdf.data.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.njagakneai.velapdf.domain.model.ExtractedText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlKitRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Menggunakan TextRecognizerOptions.DEFAULT_OPTIONS (Bundled, Offline mode)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextFromUri(pageNumber: Int, uri: Uri): ExtractedText {
        return try {
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
}
