package com.njagakneai.velapdf.domain.summary

import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.model.DocumentSummary
import com.njagakneai.velapdf.domain.model.ExtractedFact
import com.njagakneai.velapdf.domain.model.FactCategory
import com.njagakneai.velapdf.domain.model.InferenceResult
import com.njagakneai.velapdf.domain.model.NlpSentence
import com.njagakneai.velapdf.domain.model.ProcessedDocumentText
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Mesin Peringkas & Penyusun Kesimpulan Berbasis Fakta SPO dan Inferensi Aturan (Bahasa Indonesia).
 */
@Singleton
class FactBasedSummarizer @Inject constructor() {

    fun generateDocumentSummary(
        metadata: DocumentMetadata,
        processedDoc: ProcessedDocumentText,
        extractedFacts: List<ExtractedFact>,
        inferenceResult: InferenceResult
    ): DocumentSummary {
        val docNumber = extractedFacts.find { it.predicate == "memilikiNomor" }?.objectValue
        val docDate = extractedFacts.find { it.predicate == "diterbitkanPadaTanggal" }?.objectValue
            ?: "Tanggal Tidak Tertera"
        val issuer = extractedFacts.find { it.predicate == "memilikiKewenangan" }?.objectValue
            ?: "Sumber / Penulis Dokumen"
        val subjectTitle = extractedFacts.find { it.predicate == "memuatPerihal" }?.objectValue
            ?: extractedFacts.find { it.predicate == "membahasTopik" }?.objectValue
            ?: metadata.fileName.substringBeforeLast(".")

        val docTypeFact = inferenceResult.inferredFacts.find { it.category == FactCategory.KLASIFIKASI_DOKUMEN }
            ?: extractedFacts.find { it.category == FactCategory.KLASIFIKASI_DOKUMEN }
        val detectedDocType = docTypeFact?.objectValue ?: "Dokumen Informatif / Umum"

        val topKeywords = processedDoc.topKeywords
        val dominantTopic = buildDominantTopicLabel(topKeywords, extractedFacts, subjectTitle)

        // 1. Seleksi kalimat intisari berbobot tertinggi (Extractive Ranking)
        val rankedSentences = processedDoc.sentences
            .filter { it.cleanedSentence.length in 22..320 }
            .sortedByDescending { sentence ->
                calculateSentenceRelevanceScore(sentence, extractedFacts, inferenceResult, topKeywords)
            }
            .distinctBy { it.cleanedSentence.lowercase() }
            .take(5)

        val keyPoints = mutableListOf<String>()
        val pageReferences = mutableMapOf<String, Int>()
        for (s in rankedSentences) {
            val pointText = s.rawSentence.trim()
            keyPoints.add(pointText)
            pageReferences[pointText] = s.pageNumber
        }
        // Fallback jika kalimat pendek semua (< 22 karakter)
        if (keyPoints.isEmpty()) {
            processedDoc.sentences.take(3).forEach { s ->
                val txt = s.rawSentence.trim()
                if (txt.isNotEmpty()) {
                    keyPoints.add(txt)
                    pageReferences[txt] = s.pageNumber
                }
            }
        }

        // 2. Penggabungan butir inferensi aturan, kewajiban, larangan, tenggat & temuan SPO
        val obligationsAndDeadlines = mutableListOf<String>()
        obligationsAndDeadlines.addAll(inferenceResult.summaryBulletPoints)

        extractedFacts.filter { it.category == FactCategory.KLAUSUL_KEWAJIBAN }
            .take(3)
            .forEach { obligationsAndDeadlines.add("Wajib: ${it.objectValue} (Hal. ${it.pageNumber})") }

        extractedFacts.filter { it.category == FactCategory.KLAUSUL_LARANGAN }
            .take(3)
            .forEach { obligationsAndDeadlines.add("Dilarang: ${it.objectValue} (Hal. ${it.pageNumber})") }

        extractedFacts.filter { it.category == FactCategory.BATAS_WAKTU_DEADLINE }
            .take(2)
            .forEach { obligationsAndDeadlines.add("Batas Waktu: ${it.objectValue} (Hal. ${it.pageNumber})") }

        // 3. Sintesis Paragraf Kesimpulan Otomatis (Bahasa Indonesia)
        val conclusionParagraph = buildIndonesianConclusion(
            detectedDocType = detectedDocType,
            dominantTopic = dominantTopic,
            docNumber = docNumber,
            issuer = issuer,
            metadata = metadata,
            extractedFacts = extractedFacts,
            keyPoints = keyPoints,
            inferenceResult = inferenceResult
        )

        // 4. Hitung rasio pemadatan
        val totalOriginalWords = processedDoc.sentences.sumOf { it.tokens.size }
        val summaryWordsCount = keyPoints.sumOf { it.split(" ").size } +
            conclusionParagraph.split(" ").size

        val compressionRatio = if (totalOriginalWords > 0) {
            ((1.0 - (summaryWordsCount.toDouble() / totalOriginalWords.toDouble())) * 100.0)
                .coerceIn(0.0, 99.9)
        } else {
            0.0
        }

        return DocumentSummary(
            metadata = metadata,
            detectedDocumentType = detectedDocType,
            dominantTopic = dominantTopic,
            topKeywords = topKeywords,
            documentIssuer = issuer,
            documentDate = docDate,
            coreSubjectTitle = subjectTitle,
            keyPoints = keyPoints.distinct(),
            conclusionParagraph = conclusionParagraph,
            obligationsAndDeadlines = obligationsAndDeadlines.distinct(),
            extractedSpoFacts = extractedFacts,
            pageReferences = pageReferences,
            inferenceResult = inferenceResult,
            totalOriginalWords = totalOriginalWords,
            summaryWordsCount = summaryWordsCount,
            compressionRatioPercentage = (compressionRatio * 10.0).roundToInt() / 10.0
        )
    }

    private fun buildDominantTopicLabel(
        topKeywords: List<String>,
        extractedFacts: List<ExtractedFact>,
        fallbackTitle: String
    ): String {
        val bookTopics = extractedFacts
            .filter { it.predicate == "membahasTopik" || it.predicate == "memilikiDefinisi" }
            .map { if (it.predicate == "membahasTopik") it.objectValue else it.subject }
            .distinct()
        if (bookTopics.isNotEmpty() && topKeywords.isNotEmpty()) {
            val kwStr = topKeywords.take(4).joinToString(", ")
            return "${bookTopics.first()} ($kwStr)"
        }
        return if (topKeywords.isNotEmpty()) {
            topKeywords.take(5).joinToString(", ")
        } else {
            fallbackTitle
        }
    }

    private fun buildIndonesianConclusion(
        detectedDocType: String,
        dominantTopic: String,
        docNumber: String?,
        issuer: String,
        metadata: DocumentMetadata,
        extractedFacts: List<ExtractedFact>,
        keyPoints: List<String>,
        inferenceResult: InferenceResult
    ): String {
        val sb = StringBuilder()
        val pageInfo = if (metadata.pageCount > 1) {
            "${metadata.pageCount} halaman (${metadata.pageRangeLabel})"
        } else {
            "1 halaman"
        }

        if (docNumber != null) {
            sb.append(
                "Berdasarkan analisis terhadap $pageInfo naskah, dokumen ini teridentifikasi sebagai " +
                    "$detectedDocType (Nomor: $docNumber) yang diterbitkan oleh $issuer mengenai \"$dominantTopic\". "
            )
        } else {
            sb.append(
                "Berdasarkan analisis luring terhadap $pageInfo materi, dokumen ini terklasifikasi sebagai " +
                    "$detectedDocType dengan fokus pembahasan utama pada topik $dominantTopic. "
            )
        }

        val definitionFact = extractedFacts.firstOrNull { it.predicate == "memilikiDefinisi" }
        val componentFact = extractedFacts.firstOrNull { it.predicate == "memilikiKomponen" }
        val mechanismFact = extractedFacts.firstOrNull { it.predicate == "bekerjaMelalui" }
        val functionFact = extractedFacts.firstOrNull { it.predicate == "memilikiFungsi" }
        val explicitConclusion = extractedFacts.firstOrNull { it.predicate == "memilikiKesimpulan" }
        val obligationFact = extractedFacts.firstOrNull { it.predicate == "memilikiKewajiban" }
        val deadlineFact = extractedFacts.firstOrNull { it.predicate == "berakhirPada" }
        val prohibitionFact = extractedFacts.firstOrNull { it.predicate == "memilikiLarangan" }

        definitionFact?.let {
            sb.append("Secara konseptual, ${it.subject} dijelaskan sebagai ${it.objectValue} (Hal. ${it.pageNumber}). ")
        }
        componentFact?.let {
            sb.append("Pembahasan juga merinci unsur/komponen ${it.subject} yang mencakup ${it.objectValue} (Hal. ${it.pageNumber}). ")
        }
        mechanismFact?.let {
            sb.append("Mekanisme kerjanya berlangsung melalui ${it.objectValue} (Hal. ${it.pageNumber}). ")
        } ?: functionFact?.let {
            sb.append("${it.subject} berperan untuk ${it.objectValue} (Hal. ${it.pageNumber}). ")
        }

        obligationFact?.let {
            sb.append("Pihak terkait memiliki kewajiban utama untuk ${it.objectValue}. ")
        }
        deadlineFact?.let {
            sb.append("Pelaksanaan ketentuan memiliki tenggat waktu pada ${it.objectValue}. ")
        }
        prohibitionFact?.let {
            sb.append("Terdapat larangan eksplisit terkait ${it.objectValue}. ")
        }

        if (explicitConclusion != null) {
            sb.append("Kesimpulan langsung dari naskah menegaskan bahwa ${explicitConclusion.objectValue} (Hal. ${explicitConclusion.pageNumber}).")
        } else if (keyPoints.isNotEmpty() &&
            definitionFact == null && obligationFact == null && mechanismFact == null
        ) {
            sb.append("Intisari terpenting dari bacaan ini menyoroti bahwa ${keyPoints.first()}")
        } else if (inferenceResult.actionItems.isNotEmpty()) {
            sb.append("Tindak lanjut yang disarankan: ${inferenceResult.actionItems.first()}")
        }

        return sb.toString().trim()
    }

    private fun calculateSentenceRelevanceScore(
        sentence: NlpSentence,
        facts: List<ExtractedFact>,
        inference: InferenceResult,
        topKeywords: List<String>
    ): Double {
        var score = sentence.heuristicScore

        for (kw in topKeywords.take(5)) {
            if (sentence.filteredTokens.contains(kw)) {
                score += 3.0
            }
        }
        for (fact in facts) {
            if (sentence.rawSentence.contains(fact.objectValue, ignoreCase = true)) {
                score += 8.0
            }
        }
        for (action in inference.actionItems) {
            if (sentence.rawSentence.contains(action, ignoreCase = true)) {
                score += 6.0
            }
        }
        if (sentence.detectedSection == "CONCLUSION") {
            score += 7.0
        }
        return score
    }
}
