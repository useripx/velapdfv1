package com.njagakneai.velapdf.domain.model

/**
 * Abstraksi pembaca berkas JSON dari folder assets aplikasi maupun direktori lokal saat unit test.
 */
fun interface AssetTextProvider {
    fun readAsset(fileName: String): String
}

data class DocumentMetadata(
    val documentId: String,
    val fileName: String,
    val sourceUriString: String = "",
    val pageRangeLabel: String = "Semua Halaman",
    val pageCount: Int,
    val mimeType: String = "application/pdf",
    val timestamp: Long = System.currentTimeMillis()
)

data class OcrPageText(
    val pageNumber: Int,
    val rawText: String,
    val confidence: Float = 0.95f,
    val lineCount: Int = 0
)

data class OcrRawResult(
    val documentId: String,
    val pages: List<OcrPageText>,
    val totalCharacters: Int,
    val processingDurationMs: Long,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

data class NlpLexiconConfig(
    val lexiconId: String,
    val version: String,
    val stopwords: Set<String>,
    val preservedNegationAndModalityTerms: Set<String> = emptySet(),
    val protectedAbbreviations: List<String>,
    val sectionMarkers: Map<String, String>,
    val domainBoostTerms: Set<String>
)

data class NlpSentence(
    val sentenceIndex: Int,
    val pageNumber: Int,
    val rawSentence: String,
    val cleanedSentence: String,
    val tokens: List<String>,
    val filteredTokens: List<String>,
    val heuristicScore: Double = 0.0,
    val detectedSection: String = "BODY",
    val hasNegation: Boolean = false
)

data class ProcessedDocumentText(
    val documentId: String,
    val totalSentences: Int,
    val sentences: List<NlpSentence>,
    val keywordFrequencies: Map<String, Int>,
    val topKeywords: List<String> = emptyList()
)

enum class FactCategory {
    METADATA_DOKUMEN,
    KLASIFIKASI_DOKUMEN,
    KLAUSUL_KEWAJIBAN,
    KLAUSUL_LARANGAN,
    KLAUSUL_HAK,
    BATAS_WAKTU_DEADLINE,
    POIN_INTISARI
}

data class SpoPatternEntry(
    val patternId: String,
    val domainScope: String,
    val description: String,
    val subject: String,
    val subjectGroup: Int,
    val predicate: String,
    val ontologyClass: String? = null,
    val ontologyProperty: String? = null,
    val ontologyRelation: String? = null,
    val category: FactCategory,
    val regexPattern: String,
    val objectGroup: Int,
    val minObjectLength: Int,
    val maxObjectLength: Int,
    val requireDigitInObject: Boolean,
    val negationGuards: List<String> = emptyList()
)

data class SpoPatternDictionary(
    val dictionaryId: String,
    val version: String,
    val patterns: List<SpoPatternEntry>
)

data class ExtractedFact(
    val factId: String,
    val documentId: String,
    val subject: String,
    val predicate: String,
    val objectValue: String,
    val category: FactCategory,
    val sourceSentence: String,
    val pageNumber: Int,
    val confidence: Float = 1.0f,
    val sourceMethod: String = "SPO_PATTERN_REGEX",
    val ontologyClass: String? = null,
    val ontologyProperty: String? = null,
    val ontologyRelation: String? = null
)

data class OntologyProperty(
    val name: String,
    val dataType: String,
    val domainClass: String,
    val rangeClass: String,
    val isRequired: Boolean = true
)

data class OntologyPropertyBinding(
    val className: String,
    val propertyName: String,
    val spoPredicate: String,
    val extractedValue: String,
    val pageNumber: Int,
    val isRequired: Boolean
)

data class DocumentConcept(
    val classId: String,
    val className: String,
    val parentClass: String? = null,
    val description: String,
    val lexicalKeywords: List<String> = emptyList(),
    val properties: List<OntologyProperty> = emptyList()
)

data class LexicalClassificationConfig(
    val emittedSubject: String = "Dokumen",
    val emittedPredicate: String = "mengindikasikanKonsep",
    val formalClassNames: Set<String> = setOf(
        "SuratKeputusan", "SuratEdaran", "PedomanSOP", "PeraturanRegulasi", "LaporanResmi"
    ),
    val publicationClassNames: Set<String> = setOf(
        "ArtikelJurnalIlmiah", "BukuAjarTeknis", "DokumenInformatifUmum"
    ),
    val formalMinMatches: Int = 2,
    val nonFormalDominanceMargin: Int = 1
)

data class DocumentOntologyTaxonomy(
    val ontologyId: String,
    val domainName: String,
    val version: String,
    val concepts: List<DocumentConcept>,
    val relations: List<String>,
    val classificationConfig: LexicalClassificationConfig = LexicalClassificationConfig()
)

enum class OperatorType {
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    MATCHES_REGEX,
    EXISTS
}

data class RuleCondition(
    val factSubject: String,
    val factPredicate: String,
    val operator: OperatorType,
    val targetValue: String
)

data class RuleAction(
    val inferredSubject: String,
    val inferredPredicate: String,
    val inferredObject: String,
    val category: FactCategory,
    val summaryBulletPoint: String,
    val actionRequired: String? = null
)

data class ProductionRule(
    val ruleId: String,
    val ruleName: String,
    val description: String,
    val priority: Int = 1,
    val conditions: List<RuleCondition>,
    val action: RuleAction
)

data class InferenceTrace(
    val firedRuleId: String,
    val firedRuleName: String,
    val matchedFacts: List<ExtractedFact>,
    val deductionResult: String,
    val executionTimestampMs: Long
)

data class InferenceResult(
    val documentId: String,
    val executedRulesCount: Int,
    val firedRulesCount: Int,
    val inferredFacts: List<ExtractedFact>,
    val summaryBulletPoints: List<String>,
    val actionItems: List<String>,
    val inferenceTraces: List<InferenceTrace>,
    val executionDurationMs: Long
)

data class DocumentSummary(
    val metadata: DocumentMetadata,
    val detectedDocumentType: String,
    val dominantTopic: String,
    val topKeywords: List<String>,
    val documentIssuer: String,
    val documentDate: String,
    val coreSubjectTitle: String,
    val keyPoints: List<String>,
    val conclusionParagraph: String,
    val obligationsAndDeadlines: List<String>,
    val extractedSpoFacts: List<ExtractedFact>,
    val pageReferences: Map<String, Int>,
    val inferenceResult: InferenceResult,
    val totalOriginalWords: Int,
    val summaryWordsCount: Int,
    val compressionRatioPercentage: Double,
    val contentSummaryPoints: List<String> = keyPoints,
    val ruleBasedKeyPoints: List<String> = obligationsAndDeadlines,
    val ontologyPropertyBindings: List<OntologyPropertyBinding> = emptyList(),
    val missingRequiredProperties: List<String> = emptyList()
)
