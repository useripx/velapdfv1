package com.njagakneai.velapdf.data.repository

import android.content.Context
import android.net.Uri
import com.njagakneai.velapdf.data.database.AnalysisHistoryDao
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import com.njagakneai.velapdf.data.ocr.MlKitRepository
import com.njagakneai.velapdf.domain.inference.RuleInferenceEngine
import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.model.DocumentSummary
import com.njagakneai.velapdf.domain.model.InferenceResult
import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.domain.model.ProcessedDocumentText
import com.njagakneai.velapdf.domain.nlp.NlpTextPreprocessor
import com.njagakneai.velapdf.domain.ontology.OntologyLoader
import com.njagakneai.velapdf.domain.summary.FactBasedSummarizer
import com.njagakneai.velapdf.utils.FileUriHelper
import com.njagakneai.velapdf.utils.SummaryPdfExporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orkestrator penuh pipeline Analisis Dokumen Offline:
 * OCR -> NLP Preprocessor -> Ontologi & SPO -> Forward Chaining -> Summarizer -> Room / PDF Export.
 */
@Singleton
class DocumentAnalysisRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mlKitRepository: MlKitRepository,
    private val nlpTextPreprocessor: NlpTextPreprocessor,
    private val ontologyLoader: OntologyLoader,
    private val ruleInferenceEngine: RuleInferenceEngine,
    private val factBasedSummarizer: FactBasedSummarizer,
    private val analysisHistoryDao: AnalysisHistoryDao,
    private val summaryPdfExporter: SummaryPdfExporter
) {

    private val loadMutex = Mutex()
    private var assetsInitialized = false

    private val _latestOcrPages = MutableStateFlow<List<OcrPageText>>(emptyList())
    val latestOcrPages: StateFlow<List<OcrPageText>> = _latestOcrPages.asStateFlow()

    private val _latestMetadata = MutableStateFlow<DocumentMetadata?>(null)
    val latestMetadata: StateFlow<DocumentMetadata?> = _latestMetadata.asStateFlow()

    private val _latestSummary = MutableStateFlow<DocumentSummary?>(null)
    val latestSummary: StateFlow<DocumentSummary?> = _latestSummary.asStateFlow()

    val analysisHistory: Flow<List<AnalysisHistoryEntity>> = analysisHistoryDao.getAllAnalyses()

    suspend fun ensureKnowledgeAssetsLoaded() {
        if (assetsInitialized) return
        loadMutex.withLock {
            if (assetsInitialized) return
            withContext(Dispatchers.IO) {
                nlpTextPreprocessor.loadLexiconFromAsset()
                ontologyLoader.loadOntologyFromAsset()
                ontologyLoader.loadSpoDictionaryFromAsset()
                ruleInferenceEngine.loadRulesFromAsset()
            }
            assetsInitialized = true
        }
    }

    suspend fun getPdfPageCount(pdfUri: Uri): Result<Int> {
        return mlKitRepository.getPdfPageCount(pdfUri)
    }

    suspend fun extractPagesFromImages(
        imageUris: List<Uri>,
        documentTitle: String,
        sourceUriString: String = "",
        onProgress: (Int, Int) -> Unit
    ): Result<Pair<DocumentMetadata, List<OcrPageText>>> {
        if (imageUris.isEmpty()) {
            return Result.failure(IllegalArgumentException("Tidak ada gambar yang dipilih untuk dipindai."))
        }
        val ocrResult = mlKitRepository.extractPagesFromImageUris(imageUris, onProgress)
        return ocrResult.mapCatching { pages ->
            val cleanTitle = documentTitle.ifBlank { "Pindai_Dokumen_${pages.size}_Hal" }
            val metadata = DocumentMetadata(
                documentId = UUID.randomUUID().toString(),
                fileName = cleanTitle,
                sourceUriString = sourceUriString.ifBlank { imageUris.first().toString() },
                pageRangeLabel = if (pages.size == 1) "Hal. 1" else "Hal. 1 - ${pages.size}",
                pageCount = pages.size,
                mimeType = "image/*"
            )
            _latestMetadata.value = metadata
            _latestOcrPages.value = pages
            metadata to pages
        }
    }

    suspend fun extractPagesFromPdf(
        pdfUri: Uri,
        explicitFileName: String? = null,
        startPage: Int = 1,
        endPage: Int? = null,
        maxPagesLimit: Int = 35,
        onProgress: (Int, Int) -> Unit
    ): Result<Pair<DocumentMetadata, List<OcrPageText>>> {
        val totalPagesResult = mlKitRepository.getPdfPageCount(pdfUri)
        val totalPdfPages = totalPagesResult.getOrNull() ?: 1

        val safeStart = startPage.coerceIn(1, totalPdfPages)
        val boundedEnd = (endPage ?: minOf(totalPdfPages, safeStart + maxPagesLimit - 1))
            .coerceIn(safeStart, minOf(totalPdfPages, safeStart + maxPagesLimit - 1))

        val ocrResult = mlKitRepository.extractFromPdfUri(
            pdfUri = pdfUri,
            startPage = safeStart,
            endPage = boundedEnd,
            maxPagesLimit = maxPagesLimit,
            onProgress = onProgress
        )

        return ocrResult.mapCatching { pages ->
            val resolvedName = explicitFileName?.takeIf { it.isNotBlank() }
                ?: FileUriHelper.getFileName(context, pdfUri).ifBlank { "Dokumen_PDF.pdf" }
            val rangeLabel = if (safeStart == boundedEnd) {
                "Hal. $safeStart"
            } else {
                "Hal. $safeStart - $boundedEnd (dari $totalPdfPages hal.)"
            }
            val metadata = DocumentMetadata(
                documentId = UUID.randomUUID().toString(),
                fileName = resolvedName,
                sourceUriString = pdfUri.toString(),
                pageRangeLabel = rangeLabel,
                pageCount = pages.size,
                mimeType = "application/pdf"
            )
            _latestMetadata.value = metadata
            _latestOcrPages.value = pages
            metadata to pages
        }
    }

    fun setPendingPreviewData(metadata: DocumentMetadata, pages: List<OcrPageText>) {
        _latestMetadata.value = metadata
        _latestOcrPages.value = pages
    }

    fun updateEditedPageText(pageNumber: Int, updatedText: String) {
        val current = _latestOcrPages.value
        if (current.isEmpty()) return
        _latestOcrPages.value = current.map { page ->
            if (page.pageNumber == pageNumber) {
                page.copy(
                    rawText = updatedText,
                    lineCount = updatedText.lines().count { it.isNotBlank() }
                )
            } else {
                page
            }
        }
    }

    suspend fun runOfflineAnalysis(
        metadata: DocumentMetadata,
        pages: List<OcrPageText>,
        saveToHistory: Boolean = true
    ): Result<DocumentSummary> = withContext(Dispatchers.Default) {
        try {
            val nonEmptyPages = pages.filter { it.rawText.isNotBlank() }
            if (nonEmptyPages.isEmpty()) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Teks tidak terdeteksi pada dokumen atau gambar. Pastikan pencahayaan cukup dan tulisan terlihat jelas."
                    )
                )
            }

            ensureKnowledgeAssetsLoaded()

            val lexicon = nlpTextPreprocessor.loadLexiconFromAsset()
            val taxonomy = ontologyLoader.loadOntologyFromAsset()
            val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
            val rules = ruleInferenceEngine.loadRulesFromAsset()

            val processedDoc: ProcessedDocumentText = nlpTextPreprocessor.processDocument(
                documentId = metadata.documentId,
                pages = nonEmptyPages,
                lexicon = lexicon
            )

            if (processedDoc.totalSentences == 0) {
                return@withContext Result.failure(
                    IllegalStateException("Teks yang terbaca terlalu singkat atau tidak mengandung kalimat bermakna.")
                )
            }

            val extractedFacts = ontologyLoader.extractSemanticFacts(
                documentId = metadata.documentId,
                processedDoc = processedDoc,
                taxonomy = taxonomy,
                spoDictionary = spoDict
            )

            val inferenceResult: InferenceResult = ruleInferenceEngine.executeForwardChaining(
                documentId = metadata.documentId,
                initialFacts = extractedFacts,
                rules = rules
            )

            val summary = factBasedSummarizer.generateDocumentSummary(
                metadata = metadata,
                processedDoc = processedDoc,
                extractedFacts = extractedFacts,
                inferenceResult = inferenceResult
            )

            _latestMetadata.value = metadata
            _latestOcrPages.value = pages
            _latestSummary.value = summary

            if (saveToHistory) {
                saveSummaryToHistory(summary)
            }

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSummaryToHistory(summary: DocumentSummary): Long = withContext(Dispatchers.IO) {
        val bulletText = if (summary.keyPoints.isEmpty()) {
            summary.conclusionParagraph
        } else {
            summary.keyPoints.mapIndexed { index, point ->
                val page = summary.pageReferences[point]
                val suffix = if (page != null) " (Hal. $page)" else ""
                "${index + 1}. $point$suffix"
            }.joinToString("\n")
        }

        val entity = AnalysisHistoryEntity(
            documentTitle = summary.metadata.fileName,
            documentType = summary.detectedDocumentType,
            dominantTopic = summary.dominantTopic,
            summaryText = bulletText,
            conclusionText = summary.conclusionParagraph,
            pageRangeLabel = summary.metadata.pageRangeLabel,
            factsCount = summary.extractedSpoFacts.size,
            sourceUri = summary.metadata.sourceUriString,
            timestamp = System.currentTimeMillis()
        )
        analysisHistoryDao.insertAnalysis(entity)
    }

    suspend fun loadSavedSummary(historyId: Int): Result<DocumentSummary> = withContext(Dispatchers.IO) {
        val entity = analysisHistoryDao.getAnalysisById(historyId)
            ?: return@withContext Result.failure(IllegalArgumentException("Riwayat ringkasan tidak ditemukan."))

        val restoredPoints = entity.summaryText
            .lines()
            .map { it.replace(Regex("^\\d+\\.\\s*"), "").trim() }
            .filter { it.isNotBlank() }

        val metadata = DocumentMetadata(
            documentId = "saved-${entity.id}",
            fileName = entity.documentTitle,
            sourceUriString = entity.sourceUri,
            pageRangeLabel = entity.pageRangeLabel,
            pageCount = 1,
            mimeType = "application/pdf",
            timestamp = entity.timestamp
        )

        val restoredSummary = DocumentSummary(
            metadata = metadata,
            detectedDocumentType = entity.documentType,
            dominantTopic = entity.dominantTopic,
            topKeywords = emptyList(),
            documentIssuer = "Tersimpan dari Riwayat",
            documentDate = "-",
            coreSubjectTitle = entity.dominantTopic,
            keyPoints = restoredPoints,
            conclusionParagraph = entity.conclusionText,
            obligationsAndDeadlines = emptyList(),
            extractedSpoFacts = emptyList(),
            pageReferences = emptyMap(),
            inferenceResult = InferenceResult(
                documentId = metadata.documentId,
                executedRulesCount = 0,
                firedRulesCount = 0,
                inferredFacts = emptyList(),
                summaryBulletPoints = emptyList(),
                actionItems = emptyList(),
                inferenceTraces = emptyList(),
                executionDurationMs = 0L
            ),
            totalOriginalWords = 0,
            summaryWordsCount = entity.conclusionText.split(" ").size,
            compressionRatioPercentage = 0.0
        )
        _latestSummary.value = restoredSummary
        _latestMetadata.value = metadata
        Result.success(restoredSummary)
    }

    suspend fun deleteSavedAnalysis(id: Int) = withContext(Dispatchers.IO) {
        analysisHistoryDao.deleteById(id)
    }

    suspend fun exportSummaryToPdf(summary: DocumentSummary): Result<Uri> {
        return summaryPdfExporter.exportSummaryToPdf(summary)
    }
}
