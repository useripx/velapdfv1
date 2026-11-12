package com.njagakneai.velapdf.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.database.HistoryDao
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import com.njagakneai.velapdf.data.model.HistoryEntity
import com.njagakneai.velapdf.data.repository.DocumentAnalysisRepository
import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.model.DocumentSummary
import com.njagakneai.velapdf.domain.model.OcrPageText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal fun filterAnalysisHistory(
    history: List<AnalysisHistoryEntity>,
    query: String
): List<AnalysisHistoryEntity> {
    val needle = query.trim().lowercase()
    if (needle.isEmpty()) return history
    return history.filter { entity ->
        entity.documentTitle.lowercase().contains(needle) ||
            entity.documentType.lowercase().contains(needle) ||
            entity.dominantTopic.lowercase().contains(needle) ||
            entity.pageRangeLabel.lowercase().contains(needle) ||
            entity.conclusionText.lowercase().contains(needle) ||
            entity.summaryText.lowercase().contains(needle)
    }
}

sealed class AnalysisTaskState {
    data object Idle : AnalysisTaskState()
    data class ExtractingOcr(
        val currentPage: Int,
        val totalPages: Int,
        val statusMessage: String
    ) : AnalysisTaskState()
    data object RunningInference : AnalysisTaskState()
    data class SuccessSummary(val summary: DocumentSummary) : AnalysisTaskState()
    data object ReadyForOcrPreview : AnalysisTaskState()
    data class Error(val message: String) : AnalysisTaskState()
}

sealed class PdfExportState {
    data object Idle : PdfExportState()
    data object Exporting : PdfExportState()
    data class Exported(val pdfUri: Uri, val shareImmediately: Boolean) : PdfExportState()
    data class Error(val message: String) : PdfExportState()
}

data class PdfInspectionInfo(
    val uri: Uri,
    val displayName: String,
    val totalPages: Int
)

@HiltViewModel
class DocumentAnalysisViewModel @Inject constructor(
    private val repository: DocumentAnalysisRepository,
    historyDao: HistoryDao
) : ViewModel() {

    private val _taskState = MutableStateFlow<AnalysisTaskState>(AnalysisTaskState.Idle)
    val taskState: StateFlow<AnalysisTaskState> = _taskState.asStateFlow()

    val activeSummary: StateFlow<DocumentSummary?> = repository.latestSummary
    val pendingOcrPages: StateFlow<List<OcrPageText>> = repository.latestOcrPages
    val pendingMetadata: StateFlow<DocumentMetadata?> = repository.latestMetadata

    val analysisHistory: StateFlow<List<AnalysisHistoryEntity>> = repository.analysisHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    val filteredAnalysisHistory: StateFlow<List<AnalysisHistoryEntity>> =
        combine(analysisHistory, _historySearchQuery) { history, query ->
            filterAnalysisHistory(history, query)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _selectedHistoryIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedHistoryIds: StateFlow<Set<Int>> = _selectedHistoryIds.asStateFlow()

    val localPdfHistory: StateFlow<List<HistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _inspectedPdf = MutableStateFlow<PdfInspectionInfo?>(null)
    val inspectedPdf: StateFlow<PdfInspectionInfo?> = _inspectedPdf.asStateFlow()

    private val _inspectError = MutableStateFlow<String?>(null)
    val inspectError: StateFlow<String?> = _inspectError.asStateFlow()

    private val _pdfExportState = MutableStateFlow<PdfExportState>(PdfExportState.Idle)
    val pdfExportState: StateFlow<PdfExportState> = _pdfExportState.asStateFlow()

    private var lastRetryAction: (() -> Unit)? = null

    fun inspectPdf(pdfUri: Uri, displayName: String) {
        viewModelScope.launch {
            _inspectError.value = null
            _inspectedPdf.value = null
            repository.getPdfPageCount(pdfUri)
                .onSuccess { total ->
                    _inspectedPdf.value = PdfInspectionInfo(
                        uri = pdfUri,
                        displayName = displayName.ifBlank { "Dokumen_PDF.pdf" },
                        totalPages = total
                    )
                }
                .onFailure { error ->
                    _inspectError.value = error.message
                        ?: "Berkas PDF tidak dapat dibuka atau dilindungi kata sandi."
                }
        }
    }

    fun clearInspectedPdf() {
        _inspectedPdf.value = null
        _inspectError.value = null
    }

    fun analyzePdf(
        pdfUri: Uri,
        fileName: String? = null,
        startPage: Int = 1,
        endPage: Int? = null,
        previewOcrBeforeSummary: Boolean = false
    ) {
        lastRetryAction = {
            analyzePdf(pdfUri, fileName, startPage, endPage, previewOcrBeforeSummary)
        }
        viewModelScope.launch {
            _taskState.value = AnalysisTaskState.ExtractingOcr(
                currentPage = 0,
                totalPages = 1,
                statusMessage = "Membuka berkas PDF..."
            )

            val ocrResult = repository.extractPagesFromPdf(
                pdfUri = pdfUri,
                explicitFileName = fileName,
                startPage = startPage,
                endPage = endPage,
                onProgress = { current, total ->
                    _taskState.value = AnalysisTaskState.ExtractingOcr(
                        currentPage = current,
                        totalPages = total,
                        statusMessage = "Membaca teks OCR halaman $current dari $total..."
                    )
                }
            )

            ocrResult.onFailure { error ->
                _taskState.value = AnalysisTaskState.Error(
                    error.message ?: "Gagal membaca berkas PDF secara offline."
                )
                return@launch
            }

            val (metadata, pages) = ocrResult.getOrThrow()
            if (previewOcrBeforeSummary) {
                _taskState.value = AnalysisTaskState.ReadyForOcrPreview
                return@launch
            }

            runInferenceOnExtracted(metadata, pages)
        }
    }

    fun analyzeImages(
        imageUris: List<Uri>,
        documentTitle: String,
        previewOcrBeforeSummary: Boolean = false
    ) {
        if (imageUris.isEmpty()) {
            _taskState.value = AnalysisTaskState.Error("Pilih minimal satu gambar terlebih dahulu.")
            return
        }
        lastRetryAction = {
            analyzeImages(imageUris, documentTitle, previewOcrBeforeSummary)
        }
        viewModelScope.launch {
            _taskState.value = AnalysisTaskState.ExtractingOcr(
                currentPage = 0,
                totalPages = imageUris.size,
                statusMessage = "Memindai gambar..."
            )

            val ocrResult = repository.extractPagesFromImages(
                imageUris = imageUris,
                documentTitle = documentTitle,
                onProgress = { current, total ->
                    _taskState.value = AnalysisTaskState.ExtractingOcr(
                        currentPage = current,
                        totalPages = total,
                        statusMessage = "Mengenali teks gambar $current dari $total..."
                    )
                }
            )

            ocrResult.onFailure { error ->
                _taskState.value = AnalysisTaskState.Error(
                    error.message ?: "Gagal melakukan OCR pada gambar."
                )
                return@launch
            }

            val (metadata, pages) = ocrResult.getOrThrow()
            if (previewOcrBeforeSummary) {
                _taskState.value = AnalysisTaskState.ReadyForOcrPreview
                return@launch
            }

            runInferenceOnExtracted(metadata, pages)
        }
    }

    fun analyzePendingOrEditedPages(
        pages: List<OcrPageText>,
        documentTitle: String? = null
    ) {
        lastRetryAction = { analyzePendingOrEditedPages(pages, documentTitle) }
        viewModelScope.launch {
            val baseMeta = repository.latestMetadata.value ?: DocumentMetadata(
                documentId = java.util.UUID.randomUUID().toString(),
                fileName = documentTitle?.takeIf { it.isNotBlank() } ?: "Dokumen_Pindai",
                pageRangeLabel = if (pages.size <= 1) "Hal. 1" else "Hal. 1 - ${pages.size}",
                pageCount = pages.size.coerceAtLeast(1)
            )
            val resolvedMeta = if (!documentTitle.isNullOrBlank()) {
                baseMeta.copy(fileName = documentTitle, pageCount = pages.size.coerceAtLeast(1))
            } else {
                baseMeta.copy(pageCount = pages.size.coerceAtLeast(1))
            }
            runInferenceOnExtracted(resolvedMeta, pages)
        }
    }

    private suspend fun runInferenceOnExtracted(
        metadata: DocumentMetadata,
        pages: List<OcrPageText>
    ) {
        _taskState.value = AnalysisTaskState.RunningInference
        repository.runOfflineAnalysis(metadata, pages, saveToHistory = true)
            .onSuccess { summary ->
                _taskState.value = AnalysisTaskState.SuccessSummary(summary)
            }
            .onFailure { error ->
                _taskState.value = AnalysisTaskState.Error(
                    error.message ?: "Gagal menyusun ringkasan dokumen secara offline."
                )
            }
    }

    fun loadSavedAnalysis(historyId: Int) {
        viewModelScope.launch {
            _taskState.value = AnalysisTaskState.RunningInference
            repository.loadSavedSummary(historyId)
                .onSuccess { summary ->
                    _taskState.value = AnalysisTaskState.SuccessSummary(summary)
                }
                .onFailure { error ->
                    _taskState.value = AnalysisTaskState.Error(
                        error.message ?: "Riwayat ringkasan tidak ditemukan."
                    )
                }
        }
    }

    fun updateHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun toggleSelectionMode(enabled: Boolean = !_isSelectionMode.value) {
        _isSelectionMode.value = enabled
        if (!enabled) {
            _selectedHistoryIds.value = emptySet()
        }
    }

    fun enterSelectionModeWith(historyId: Int) {
        _isSelectionMode.value = true
        _selectedHistoryIds.value = _selectedHistoryIds.value + historyId
    }

    fun toggleHistorySelection(historyId: Int) {
        val current = _selectedHistoryIds.value
        _selectedHistoryIds.value = if (historyId in current) {
            current - historyId
        } else {
            current + historyId
        }
    }

    fun selectAllFilteredHistory() {
        val visibleIds = filteredAnalysisHistory.value.map { it.id }.toSet()
        if (visibleIds.isEmpty()) return
        _isSelectionMode.value = true
        val current = _selectedHistoryIds.value
        _selectedHistoryIds.value = if (visibleIds.all { it in current }) {
            current - visibleIds
        } else {
            current + visibleIds
        }
    }

    fun clearHistorySelection() {
        _selectedHistoryIds.value = emptySet()
        _isSelectionMode.value = false
    }

    fun deleteSelectedHistory() {
        val idsToDelete = _selectedHistoryIds.value.toList()
        if (idsToDelete.isEmpty()) return
        viewModelScope.launch {
            repository.deleteSavedAnalyses(idsToDelete)
            _selectedHistoryIds.value = emptySet()
            _isSelectionMode.value = false
        }
    }

    fun deleteSavedAnalysis(historyId: Int) {
        viewModelScope.launch {
            repository.deleteSavedAnalysis(historyId)
            _selectedHistoryIds.value = _selectedHistoryIds.value - historyId
        }
    }

    fun exportCurrentSummaryToPdf(shareImmediately: Boolean) {
        val summary = activeSummary.value ?: return
        viewModelScope.launch {
            _pdfExportState.value = PdfExportState.Exporting
            repository.exportSummaryToPdf(summary)
                .onSuccess { uri ->
                    _pdfExportState.value = PdfExportState.Exported(uri, shareImmediately)
                }
                .onFailure { error ->
                    _pdfExportState.value = PdfExportState.Error(
                        error.message ?: "Gagal mengekspor ringkasan ke PDF."
                    )
                }
        }
    }

    fun consumeExportState() {
        _pdfExportState.value = PdfExportState.Idle
    }

    fun consumeTaskState() {
        _taskState.value = AnalysisTaskState.Idle
    }

    fun retryLastAnalysis() {
        lastRetryAction?.invoke() ?: run {
            _taskState.value = AnalysisTaskState.Idle
        }
    }
}
