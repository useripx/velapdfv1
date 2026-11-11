package com.njagakneai.velapdf.metopen.domain.model

import android.net.Uri

/**
 * METOPEN TA 2026/2027 - PROYEK VELAPDF
 * Mahasiswa: Yogi Ario Pratama (NPM: 2313020004)
 * Rumpun: Sains Data & AI (R&D Rekayasa Perangkat Lunak AI)
 * Dosen Pembimbing Proposal: Rony Heri Irawan, M.Kom.
 * 
 * MODEL DATA & ENTITAS: Representasi Metadata Dokumen, Pemrosesan NLP,
 * Fakta Semantik (SPO), Taksonomi Ontologi Dokumen Umum, Aturan Produksi,
 * dan Laporan Ringkasan Intisari (Document Summary).
 * Pemrosesan Cerdas Dokumen Resmi (Surat Keputusan, Surat Edaran, SOP, Peraturan, Laporan).
 */

// ============================================================================
// 1. MODEL DOKUMEN & METADATA DIGITAL
// ============================================================================

data class DocumentMetadata(
    val documentId: String,
    val fileName: String,
    val fileUri: Uri,
    val fileSizeBytes: Long,
    val sha256Checksum: String,
    val pageCount: Int,
    val mimeType: String = "application/pdf",
    val timestamp: Long = System.currentTimeMillis()
)

data class OcrPageText(
    val pageNumber: Int,
    val rawText: String,
    val confidence: Float,
    val lineCount: Int
)

data class OcrRawResult(
    val documentId: String,
    val pages: List<OcrPageText>,
    val totalCharacters: Int,
    val processingDurationMs: Long,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

// ============================================================================
// 2. MODEL PEMROSESAN NLP (NATURAL LANGUAGE PROCESSING)
// ============================================================================

data class NlpSentence(
    val sentenceIndex: Int,
    val pageNumber: Int,
    val rawSentence: String,
    val cleanedSentence: String,
    val tokens: List<String>,
    val filteredTokens: List<String>, // Tokens setelah Stopword Removal
    val heuristicScore: Double = 0.0, // Skor bobot relevansi kata kunci
    val detectedSection: String = "BODY" // HEADER, OPENING, BODY, CLAUSE, CLOSING
)

data class ProcessedDocumentText(
    val documentId: String,
    val totalSentences: Int,
    val sentences: List<NlpSentence>,
    val keywordFrequencies: Map<String, Int>
)

// ============================================================================
// 3. MODEL FAKTA SEMANTIK (TRIPLE SPO: SUBJECT-PREDICATE-OBJECT)
// ============================================================================

enum class FactCategory {
    METADATA_DOKUMEN,      // Nomor, tanggal, penerbit, penandatangan
    KLASIFIKASI_DOKUMEN,   // Surat Keputusan, Surat Edaran, Pedoman, Regulasi, Laporan
    KLAUSUL_KEWAJIBAN,     // Tindakan yang wajib dilakukan subjek
    KLAUSUL_LARANGAN,      // Hal yang dilarang bagi subjek
    KLAUSUL_HAK,           // Fasilitas atau hak yang diperoleh
    BATAS_WAKTU_DEADLINE,  // Tanggal kedaluwarsa atau batas waktu penyerahan
    POIN_INTISARI          // Poin-poin informasi kunci dari isi dokumen
}

data class ExtractedFact(
    val factId: String,
    val documentId: String,
    val subject: String,          // Contoh: "Rektor", "Mahasiswa", "Dokumen", "BatasPendaftaran"
    val predicate: String,        // Contoh: "menetapkanNomor", "memilikiKewajiban", "berakhirPada"
    val objectValue: String,      // Contoh: "SK/2026/04", "Registrasi Ulang", "20 Januari 2027"
    val category: FactCategory,
    val sourceSentence: String,   // Kalimat sumber asal ekstraksi (untuk pembuktian/traceability)
    val pageNumber: Int,
    val confidence: Float = 1.0f
)

// ============================================================================
// 4. MODEL ONTOLOGI DOKUMEN UMUM (JSON TAXONOMY)
// ============================================================================

data class OntologyProperty(
    val name: String,
    val dataType: String,         // STRING, INTEGER, DATE, BOOLEAN
    val domainClass: String,
    val rangeClass: String,
    val isRequired: Boolean = true
)

data class DocumentConcept(
    val classId: String,
    val className: String,
    val parentClass: String? = null,
    val description: String,
    val lexicalKeywords: List<String> = emptyList(), // Kata kunci penanda konsep
    val properties: List<OntologyProperty> = emptyList()
)

data class DocumentOntologyTaxonomy(
    val ontologyId: String,
    val domainName: String,
    val version: String,
    val concepts: List<DocumentConcept>,
    val relations: List<String>
)

// ============================================================================
// 5. MODEL ATURAN INFERENSI (PRODUCTION RULES IF-THEN)
// ============================================================================

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
    val priority: Int = 1,        // Prioritas resolusi konflik (semakin tinggi semakin utama)
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

// ============================================================================
// 6. MODEL LAPORAN RINGKASAN INTISARI DOKUMEN (DOCUMENT SUMMARY)
// ============================================================================

data class DocumentSummary(
    val metadata: DocumentMetadata,
    val detectedDocumentType: String,      // Surat Keputusan / Surat Edaran / Pedoman / Regulasi
    val documentIssuer: String,            // Lembaga/Pihak Penerbit Dokumen
    val documentDate: String,              // Tanggal Penerbitan Dokumen
    val coreSubjectTitle: String,          // Judul / Perihal Pokok Dokumen
    val keyPoints: List<String>,           // Poin-poin intisari penting (ekstraktif)
    val obligationsAndDeadlines: List<String>, // Klausul kewajiban, larangan, & batas waktu
    val pageReferences: Map<String, Int>,  // Peta nomor halaman sumber setiap poin intisari
    val inferenceResult: InferenceResult,
    val totalOriginalWords: Int,
    val summaryWordsCount: Int,
    val compressionRatioPercentage: Double // Rasio pemadatan ringkasan
)
