package com.njagakneai.velapdf.domain.summary

import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.model.DocumentSummary
import com.njagakneai.velapdf.domain.model.ExtractedFact
import com.njagakneai.velapdf.domain.model.FactCategory
import com.njagakneai.velapdf.domain.model.InferenceResult
import com.njagakneai.velapdf.domain.model.NlpSentence
import com.njagakneai.velapdf.domain.model.OntologyPropertyBinding
import com.njagakneai.velapdf.domain.model.ProcessedDocumentText
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Mesin Peringkas Dokumen Luring Bahasa Indonesia yang memisahkan dua fitur secara tegas:
 * 1) Document Summarization: merangkum representasi keseluruhan isi dokumen (awal-tengah-akhir / lintas halaman).
 * 2) Rule-Based Key-Point Extraction: mengekstrak poin penting, kewajiban, pengecualian, larangan, & batas waktu dari aturan IF-THEN.
 */
@Singleton
class FactBasedSummarizer @Inject constructor() {

    fun generateDocumentSummary(
        metadata: DocumentMetadata,
        processedDoc: ProcessedDocumentText,
        extractedFacts: List<ExtractedFact>,
        inferenceResult: InferenceResult,
        ontologyPropertyBindings: List<OntologyPropertyBinding> = emptyList(),
        missingRequiredProperties: List<String> = emptyList()
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

        val totalOriginalWords = processedDoc.sentences.sumOf { it.tokens.size }
        val targetSummaryPoints = when {
            metadata.pageCount >= 3 || totalOriginalWords > 420 -> 6
            totalOriginalWords >= 220 -> 4
            totalOriginalWords >= 90 -> 2
            processedDoc.sentences.size >= 2 -> 2
            else -> 1
        }
        val isCompactDocument = totalOriginalWords in 1..219 && metadata.pageCount <= 2

        // 1. DOCUMENT SUMMARIZATION (lintas halaman & seksi: Awal - Tengah - Akhir)
        val stratifiedSentences = selectDocumentRepresentativeSentences(
            sentences = processedDoc.sentences,
            extractedFacts = extractedFacts,
            topKeywords = topKeywords,
            maxSentences = targetSummaryPoints
        )

        val contentSummaryPoints = mutableListOf<String>()
        val pageReferences = mutableMapOf<String, Int>()
        for (s in stratifiedSentences) {
            val pointText = s.rawSentence.trim()
            contentSummaryPoints.add(pointText)
            pageReferences[pointText] = s.pageNumber
        }
        if (contentSummaryPoints.isEmpty()) {
            processedDoc.sentences.take(targetSummaryPoints).forEach { s ->
                val txt = s.rawSentence.trim()
                if (txt.isNotEmpty()) {
                    contentSummaryPoints.add(txt)
                    pageReferences[txt] = s.pageNumber
                }
            }
        }

        // 2. RULE-BASED KEY-POINT EXTRACTION (butir berbasis aturan IF-THEN & deontik/fakta spesifik)
        val ruleBasedKeyPoints = buildRuleBasedKeyPoints(
            extractedFacts = extractedFacts,
            inferenceResult = inferenceResult
        )

        // 3. Paragraf Narasi Ringkasan Keseluruhan Isi Dokumen (adaptif untuk naskah singkat agar kompresi positif)
        val summaryNarrative = buildWholeDocumentSummaryParagraph(
            detectedDocType = detectedDocType,
            dominantTopic = dominantTopic,
            docNumber = docNumber,
            issuer = issuer,
            metadata = metadata,
            representativeSentences = stratifiedSentences,
            extractedFacts = extractedFacts,
            isCompactDocument = isCompactDocument
        )

        val summaryWordsCount = contentSummaryPoints.sumOf { it.split(" ").size } +
            summaryNarrative.split(" ").size

        val compressionRatio = if (totalOriginalWords > 0) {
            ((1.0 - (summaryWordsCount.toDouble() / totalOriginalWords.toDouble())) * 100.0)
                .coerceIn(0.0, 99.9)
        } else {
            0.0
        }

        val distinctSummaryPoints = contentSummaryPoints.distinct()
        return DocumentSummary(
            metadata = metadata,
            detectedDocumentType = detectedDocType,
            dominantTopic = dominantTopic,
            topKeywords = topKeywords,
            documentIssuer = issuer,
            documentDate = docDate,
            coreSubjectTitle = subjectTitle,
            keyPoints = distinctSummaryPoints,
            conclusionParagraph = summaryNarrative,
            obligationsAndDeadlines = ruleBasedKeyPoints,
            extractedSpoFacts = extractedFacts,
            pageReferences = pageReferences,
            inferenceResult = inferenceResult,
            totalOriginalWords = totalOriginalWords,
            summaryWordsCount = summaryWordsCount,
            compressionRatioPercentage = (compressionRatio * 10.0).roundToInt() / 10.0,
            contentSummaryPoints = distinctSummaryPoints,
            ruleBasedKeyPoints = ruleBasedKeyPoints,
            ontologyPropertyBindings = ontologyPropertyBindings,
            missingRequiredProperties = missingRequiredProperties
        )
    }

    /**
     * Memilih kalimat paling representatif dengan stratifikasi posisi (Awal, Tengah, Akhir)
     * agar ringkasan benar-benar mewakili isi keseluruhan dokumen dari awal hingga akhir.
     */
    private fun selectDocumentRepresentativeSentences(
        sentences: List<NlpSentence>,
        extractedFacts: List<ExtractedFact>,
        topKeywords: List<String>,
        maxSentences: Int
    ): List<NlpSentence> {
        val candidates = sentences.filter { isCleanProseCandidate(it) }
        val pool = if (candidates.isNotEmpty()) candidates else sentences.filter { it.cleanedSentence.length >= 16 }
        if (pool.isEmpty()) return emptyList()

        val scoredMap = pool.associateWith { s ->
            calculateContentSalienceScore(s, extractedFacts, topKeywords)
        }

        val distinctPages = pool.map { it.pageNumber }.distinct().sorted()
        val openingZone: List<NlpSentence>
        val middleZone: List<NlpSentence>
        val closingZone: List<NlpSentence>

        if (distinctPages.size >= 3) {
            val third = (distinctPages.size / 3).coerceAtLeast(1)
            val openPages = distinctPages.take(third).toSet()
            val closePages = distinctPages.takeLast(third).toSet()
            val midPages = (distinctPages - openPages - closePages).ifEmpty { distinctPages.toSet() }
            openingZone = pool.filter { it.pageNumber in openPages }
            middleZone = pool.filter { it.pageNumber in midPages }
            closingZone = pool.filter { it.pageNumber in closePages }
        } else {
            val third = (pool.size / 3).coerceAtLeast(1)
            openingZone = pool.take(third)
            middleZone = pool.drop(third).dropLast(third).ifEmpty { pool }
            closingZone = pool.takeLast(third)
        }

        val chosen = mutableListOf<NlpSentence>()

        fun pickFromZone(zone: List<NlpSentence>, quota: Int, preferSectionOrder: Set<String>) {
            val ranked = zone.sortedByDescending { s ->
                val secBonus = if (s.detectedSection in preferSectionOrder) 4.0 else 0.0
                (scoredMap[s] ?: 0.0) + secBonus
            }
            var pickedHere = 0
            for (cand in ranked) {
                if (chosen.size >= maxSentences || pickedHere >= quota) break
                if (chosen.none { jaccardSimilarity(it.filteredTokens, cand.filteredTokens) >= 0.68 }) {
                    chosen.add(cand)
                    pickedHere++
                }
            }
        }

        // Ambil perwakilan dari Awal (latar/pembuka), Tengah (mekanisme/isi), dan Akhir (hasil/simpulan)
        pickFromZone(openingZone, 2, setOf("ABSTRACT", "HEADER", "DEFINITION", "OPENING"))
        pickFromZone(middleZone, 2, setOf("MECHANISM", "CLAUSE", "BODY", "DEFINITION"))
        pickFromZone(closingZone, 2, setOf("CONCLUSION", "CLAUSE", "CLOSING"))

        // Isi sisa slot (jika ada) berdasarkan peringkat relevansi global tanpa duplikasi
        if (chosen.size < maxSentences) {
            val globalRanked = pool.sortedByDescending { scoredMap[it] ?: 0.0 }
            for (cand in globalRanked) {
                if (chosen.size >= maxSentences) break
                if (chosen.none { jaccardSimilarity(it.filteredTokens, cand.filteredTokens) >= 0.68 }) {
                    chosen.add(cand)
                }
            }
        }

        // Susun ulang dalam urutan baca alami (halaman lalu indeks kalimat)
        return chosen.sortedWith(compareBy<NlpSentence> { it.pageNumber }.thenBy { it.sentenceIndex })
    }

    private fun buildRuleBasedKeyPoints(
        extractedFacts: List<ExtractedFact>,
        inferenceResult: InferenceResult
    ): List<String> {
        val points = mutableListOf<String>()
        points.addAll(inferenceResult.summaryBulletPoints)

        extractedFacts.filter { it.category == FactCategory.KLAUSUL_KEWAJIBAN }
            .take(3)
            .forEach { points.add("Wajib: ${it.objectValue} (Hal. ${it.pageNumber})") }

        extractedFacts.filter { it.predicate == "tidakMemilikiKewajiban" }
            .take(3)
            .forEach { points.add("Pengecualian (Tidak Wajib): ${it.objectValue} (Hal. ${it.pageNumber})") }

        extractedFacts.filter { it.category == FactCategory.KLAUSUL_LARANGAN }
            .take(3)
            .forEach { points.add("Dilarang: ${it.objectValue} (Hal. ${it.pageNumber})") }

        extractedFacts.filter { it.category == FactCategory.BATAS_WAKTU_DEADLINE }
            .take(2)
            .forEach { points.add("Batas Waktu: ${it.objectValue} (Hal. ${it.pageNumber})") }

        return points.distinct()
    }

    private fun buildWholeDocumentSummaryParagraph(
        detectedDocType: String,
        dominantTopic: String,
        docNumber: String?,
        issuer: String,
        metadata: DocumentMetadata,
        representativeSentences: List<NlpSentence>,
        extractedFacts: List<ExtractedFact>,
        isCompactDocument: Boolean = false
    ): String {
        val sb = StringBuilder()
        if (isCompactDocument) {
            val numSuffix = if (docNumber != null) " ($docNumber)" else ""
            sb.append("Ringkasannya, $detectedDocType$numSuffix ini membahas $dominantTopic.")
            val leadSnippet = representativeSentences.firstOrNull()?.let {
                trimSentenceForNarration(it.rawSentence, maxLen = 105)
            }
            if (!leadSnippet.isNullOrBlank()) {
                sb.append(" Inti utama: $leadSnippet.")
            }
            val exemptionFact = extractedFacts.firstOrNull { it.predicate == "tidakMemilikiKewajiban" }
            exemptionFact?.let {
                sb.append(" Pengecualian: tidak wajib ${it.objectValue}.")
            }
            return sb.toString().replace("..", ".").trim()
        }

        val pageInfo = if (metadata.pageCount > 1) {
            "${metadata.pageCount} halaman (${metadata.pageRangeLabel})"
        } else {
            "1 halaman"
        }

        if (docNumber != null) {
            sb.append(
                "Ringkasan isi naskah ($pageInfo) mengidentifikasi dokumen ini sebagai $detectedDocType " +
                    "(Nomor: $docNumber) dari $issuer dengan topik utama \"$dominantTopic\". "
            )
        } else {
            sb.append(
                "Ringkasan keseluruhan isi dokumen ($pageInfo) menelaah topik \"$dominantTopic\" " +
                    "pada kategori $detectedDocType. "
            )
        }

        // Gabungkan perwakilan awal, tengah, dan akhir dokumen secara runtut (Document Summarization)
        val picks = when {
            representativeSentences.size >= 3 -> listOf(
                representativeSentences.first(),
                representativeSentences[representativeSentences.size / 2],
                representativeSentences.last()
            ).distinctBy { it.sentenceIndex }
            else -> representativeSentences
        }

        picks.forEachIndexed { idx, sentence ->
            val trimmed = trimSentenceForNarration(sentence.rawSentence)
            val pageTag = if (metadata.pageCount > 1) " (Hal. ${sentence.pageNumber})" else ""
            when {
                idx == 0 -> sb.append("Pada bagian awal pembahasan, naskah memaparkan bahwa $trimmed$pageTag. ")
                idx == picks.lastIndex && idx > 0 ->
                    sb.append("Sebagai pokok bahasan penutup, dokumen menegaskan bahwa $trimmed$pageTag.")
                else -> sb.append("Selanjutnya pada bagian inti, dibahas bahwa $trimmed$pageTag. ")
            }
        }

        val exemptionFact = extractedFacts.firstOrNull { it.predicate == "tidakMemilikiKewajiban" }
        exemptionFact?.let {
            sb.append(" Selain itu terdapat ketentuan pengecualian bahwa pihak terkait tidak wajib ${it.objectValue}.")
        }

        return sb.toString().replace("..", ".").trim()
    }

    private fun isCleanProseCandidate(sentence: NlpSentence): Boolean {
        val raw = sentence.rawSentence.trim()
        if (sentence.cleanedSentence.length !in 28..340) return false
        val low = raw.lowercase()
        if (low.startsWith("[") && Regex("""^\[\d+]""").containsMatchIn(low)) return false
        if (low.contains("creativecommons.org") || low.contains("halaman ini sengaja dibiarkan kosong")) return false
        if (Regex("""(?i)\b[pe]-?issn\b""").containsMatchIn(raw) && sentence.tokens.size < 18) return false
        val wordCount = sentence.tokens.count { tok -> tok.any { it.isLetter() } }
        return wordCount >= 5
    }

    private fun calculateContentSalienceScore(
        sentence: NlpSentence,
        facts: List<ExtractedFact>,
        topKeywords: List<String>
    ): Double {
        var score = sentence.heuristicScore
        for (kw in topKeywords.take(6)) {
            if (sentence.filteredTokens.contains(kw)) {
                score += 3.5
            }
        }
        for (fact in facts) {
            if (fact.objectValue.length >= 6 && sentence.rawSentence.contains(fact.objectValue, ignoreCase = true)) {
                score += 4.5
            }
        }
        if (sentence.detectedSection == "CONCLUSION" || sentence.detectedSection == "ABSTRACT") {
            score += 6.0
        } else if (sentence.detectedSection == "DEFINITION" || sentence.detectedSection == "MECHANISM") {
            score += 4.0
        }
        if (sentence.hasNegation) {
            score += 1.5
        }
        return score
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

    private fun trimSentenceForNarration(raw: String, maxLen: Int = 180): String {
        val clean = raw.replace(Regex("""\s+"""), " ").trim().trimEnd('.')
        return if (clean.length > maxLen) clean.take((maxLen - 3).coerceAtLeast(20)).trimEnd() + "..." else clean
    }

    private fun jaccardSimilarity(a: List<String>, b: List<String>): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val setA = a.toSet()
        val setB = b.toSet()
        val intersection = setA.intersect(setB).size.toDouble()
        val union = setA.union(setB).size.toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }
}
