package com.njagakneai.velapdf.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.ocr.MlKitRepository
import com.njagakneai.velapdf.data.repository.DocumentAnalysisRepository
import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.model.ExtractedText
import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.domain.model.ScannedPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class OcrState {
    object Idle : OcrState()
    data class Processing(val currentPage: Int, val totalPages: Int) : OcrState()
    data class Success(val results: List<ExtractedText>) : OcrState()
    data class Error(val message: String) : OcrState()
}

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val mlKitRepository: MlKitRepository,
    private val analysisRepository: DocumentAnalysisRepository
) : ViewModel() {

    private val _ocrState = MutableStateFlow<OcrState>(OcrState.Idle)
    val ocrState: StateFlow<OcrState> = _ocrState.asStateFlow()

    private val _extractedTexts = MutableStateFlow<List<ExtractedText>>(emptyList())
    val extractedTexts: StateFlow<List<ExtractedText>> = _extractedTexts.asStateFlow()

    private val _documentTitle = MutableStateFlow("Dokumen_Pindai")
    val documentTitle: StateFlow<String> = _documentTitle.asStateFlow()

    fun syncFromRepository() {
        val pendingPages = analysisRepository.latestOcrPages.value
        val pendingMeta = analysisRepository.latestMetadata.value
        if (pendingMeta != null && pendingMeta.fileName.isNotBlank()) {
            _documentTitle.value = pendingMeta.fileName
        }
        if (pendingPages.isNotEmpty()) {
            val mapped = pendingPages.map { page ->
                ExtractedText(
                    pageNumber = page.pageNumber,
                    rawText = page.rawText,
                    isSuccess = true
                )
            }
            _extractedTexts.value = mapped
            _ocrState.value = OcrState.Success(mapped)
        }
    }

    fun setDocumentTitle(title: String) {
        _documentTitle.value = title
    }

    fun processScannedPages(pages: List<ScannedPage>, title: String = "Dokumen_Pindai") {
        if (pages.isEmpty()) return
        _documentTitle.value = title

        viewModelScope.launch {
            val total = pages.size
            val results = mutableListOf<ExtractedText>()
            _ocrState.value = OcrState.Processing(0, total)

            for ((index, page) in pages.withIndex()) {
                val pageNumber = index + 1
                _ocrState.value = OcrState.Processing(pageNumber, total)
                val extractedText = mlKitRepository.extractTextFromUri(pageNumber, page.imageUri)
                results.add(extractedText)
            }

            _extractedTexts.value = results
            syncToRepository(title, pages.firstOrNull()?.imageUri, results)
            _ocrState.value = OcrState.Success(results)
        }
    }

    fun extractFromImageUris(imageUris: List<Uri>, title: String = "Dokumen_Pindai") {
        val scanned = imageUris.mapIndexed { index, uri ->
            ScannedPage(pageNumber = index + 1, imageUri = uri)
        }
        processScannedPages(scanned, title)
    }

    fun updateTextForPage(pageNumber: Int, newText: String) {
        val currentList = _extractedTexts.value.toMutableList()
        val index = currentList.indexOfFirst { it.pageNumber == pageNumber }
        if (index != -1) {
            currentList[index] = currentList[index].copy(rawText = newText)
            _extractedTexts.value = currentList
        }
        analysisRepository.updateEditedPageText(pageNumber, newText)
    }

    fun toOcrPageTexts(): List<OcrPageText> {
        return _extractedTexts.value.map { item ->
            OcrPageText(
                pageNumber = item.pageNumber,
                rawText = item.rawText,
                lineCount = item.rawText.lines().count { it.isNotBlank() }
            )
        }
    }

    private fun syncToRepository(title: String, firstUri: Uri?, results: List<ExtractedText>) {
        val pageModels = results.map { item ->
            OcrPageText(
                pageNumber = item.pageNumber,
                rawText = item.rawText,
                lineCount = item.rawText.lines().count { it.isNotBlank() }
            )
        }
        val metadata = DocumentMetadata(
            documentId = UUID.randomUUID().toString(),
            fileName = title,
            sourceUriString = firstUri?.toString().orEmpty(),
            pageRangeLabel = if (pageModels.size <= 1) "Hal. 1" else "Hal. 1 - ${pageModels.size}",
            pageCount = pageModels.size.coerceAtLeast(1),
            mimeType = "image/*"
        )
        analysisRepository.setPendingPreviewData(metadata, pageModels)
    }

    fun resetState() {
        _ocrState.value = OcrState.Idle
        _extractedTexts.value = emptyList()
    }
}
