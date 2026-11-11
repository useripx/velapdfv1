package com.njagakneai.velapdf.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.ocr.MlKitRepository
import com.njagakneai.velapdf.domain.model.ExtractedText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OcrState {
    object Idle : OcrState()
    data class Processing(val currentPage: Int, val totalPages: Int) : OcrState()
    data class Success(val results: List<ExtractedText>) : OcrState()
    data class Error(val message: String) : OcrState()
}

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val mlKitRepository: MlKitRepository
) : ViewModel() {

    private val _ocrState = MutableStateFlow<OcrState>(OcrState.Idle)
    val ocrState: StateFlow<OcrState> = _ocrState.asStateFlow()

    private val _extractedTexts = MutableStateFlow<List<ExtractedText>>(emptyList())
    val extractedTexts: StateFlow<List<ExtractedText>> = _extractedTexts.asStateFlow()

    fun processImages(uris: List<Uri>) {
        if (uris.isEmpty()) {
            _ocrState.value = OcrState.Error("Tidak ada gambar yang dipilih")
            return
        }

        viewModelScope.launch {
            _ocrState.value = OcrState.Processing(1, uris.size)
            val results = mutableListOf<ExtractedText>()

            for ((index, uri) in uris.withIndex()) {
                val pageNumber = index + 1
                _ocrState.value = OcrState.Processing(pageNumber, uris.size)
                
                val extractedText = mlKitRepository.extractTextFromUri(pageNumber, uri)
                results.add(extractedText)
            }

            _extractedTexts.value = results
            _ocrState.value = OcrState.Success(results)
        }
    }

    fun updateText(pageNumber: Int, newText: String) {
        val currentList = _extractedTexts.value.toMutableList()
        val index = currentList.indexOfFirst { it.pageNumber == pageNumber }
        if (index != -1) {
            currentList[index] = currentList[index].copy(rawText = newText)
            _extractedTexts.value = currentList
            _ocrState.value = OcrState.Success(currentList)
        }
    }

    fun resetState() {
        _ocrState.value = OcrState.Idle
        _extractedTexts.value = emptyList()
    }
}
