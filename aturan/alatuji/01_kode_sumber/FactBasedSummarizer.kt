package com.njagakneai.velapdf.metopen.domain.summary

import com.njagakneai.velapdf.metopen.domain.model.*

/**
 * METOPEN TA 2026/2027 - PROYEK VELAPDF
 * Mahasiswa: Yogi Ario Pratama (NPM: 2313020004)
 * 
 * Mesin Peringkas Dokumen Berbasis Fakta & Ontologi (Fact-Based Extractive Summarizer)
 * Mensintesis teks hasil prapemrosesan NLP, fakta semantik ontologi (SPO),
 * dan hasil deduksi mesin inferensi menjadi laporan ringkasan intisari dokumen terstruktur.
 * Beroperasi 100% luring tanpa ketergantungan pada model generatif awan / LLM besar.
 */
class FactBasedSummarizer {

    /**
     * Membangun ringkasan intisari dokumen komprehensif.
     */
    fun generateDocumentSummary(
        metadata: DocumentMetadata,
        processedDoc: ProcessedDocumentText,
        extractedFacts: List<ExtractedFact>,
        inferenceResult: InferenceResult
    ): DocumentSummary {
        // 1. Ekstraksi Informasi Identitas Dokumen
        val docNumber = extractedFacts.find { it.predicate == "memilikiNomor" }?.objectValue
            ?: "Nomor Dokumen Tidak Terdeteksi"
        val docDate = extractedFacts.find { it.predicate == "diterbitkanPadaTanggal" }?.objectValue
            ?: "Tanggal Tidak Tertera"
        val issuer = extractedFacts.find { it.predicate == "memilikiKewenangan" }?.objectValue
            ?: "Institusi / Pejabat Berwenang"
        val subjectTitle = extractedFacts.find { it.predicate == "memuatPerihal" }?.objectValue
            ?: metadata.fileName.substringBeforeLast(".")

        // 2. Identifikasi Klasifikasi Jenis Dokumen dari Inferensi atau Fakta Ontologi
        val docTypeFact = inferenceResult.inferredFacts.find { it.category == FactCategory.KLASIFIKASI_DOKUMEN }
            ?: extractedFacts.find { it.category == FactCategory.KLASIFIKASI_DOKUMEN }
        val detectedDocType = docTypeFact?.objectValue ?: "Dokumen Resmi / Pedoman Kelembagaan"

        // 3. Seleksi Kalimat Intisari Berbobot Tertinggi (Extractive Ranking)
        val rankedSentences = processedDoc.sentences
            .filter { it.cleanedSentence.length in 25..250 }
            .sortedByDescending { sentence ->
                calculateSentenceRelevanceScore(sentence, extractedFacts, inferenceResult)
            }
            .take(5) // Ambil 5 kalimat intisari paling berbobot

        val keyPoints = mutableListOf<String>()
        val pageReferences = mutableMapOf<String, Int>()

        for (s in rankedSentences) {
            val pointText = s.rawSentence.trim()
            keyPoints.add(pointText)
            pageReferences[pointText] = s.pageNumber
        }

        // 4. Penggabungan Butir Inferensi Aturan & Klausul Regulasi
        val obligationsAndDeadlines = mutableListOf<String>()
        obligationsAndDeadlines.addAll(inferenceResult.summaryBulletPoints)

        val directObligations = extractedFacts.filter { it.category == FactCategory.KLAUSUL_KEWAJIBAN }
            .take(3)
            .map { "Wajib: ${it.objectValue} (Hal. ${it.pageNumber})" }
        obligationsAndDeadlines.addAll(directObligations)

        val directDeadlines = extractedFacts.filter { it.category == FactCategory.BATAS_WAKTU_DEADLINE }
            .take(2)
            .map { "Batas Waktu: ${it.objectValue} (Hal. ${it.pageNumber})" }
        obligationsAndDeadlines.addAll(directDeadlines)

        // 5. Perhitungan Metrik Pemadatan Ringkasan (Compression Ratio)
        val totalOriginalWords = processedDoc.sentences.sumOf { it.tokens.size }
        val summaryWordsCount = keyPoints.sumOf { it.split(" ").size } +
                obligationsAndDeadlines.sumOf { it.split(" ").size }

        val compressionRatio = if (totalOriginalWords > 0) {
            (1.0 - (summaryWordsCount.toDouble() / totalOriginalWords.toDouble())) * 100.0
        } else 0.0

        return DocumentSummary(
            metadata = metadata,
            detectedDocumentType = detectedDocType,
            documentIssuer = issuer,
            documentDate = docDate,
            coreSubjectTitle = subjectTitle,
            keyPoints = keyPoints.distinct(),
            obligationsAndDeadlines = obligationsAndDeadlines.distinct(),
            pageReferences = pageReferences,
            inferenceResult = inferenceResult,
            totalOriginalWords = totalOriginalWords,
            summaryWordsCount = summaryWordsCount,
            compressionRatioPercentage = Math.round(compressionRatio * 10.0) / 10.0
        )
    }

    /**
     * Menghitung skor keterikatan semantik kalimat terhadap fakta ontologi dan aturan inferensi.
     */
    private fun calculateSentenceRelevanceScore(
        sentence: NlpSentence,
        facts: List<ExtractedFact>,
        inference: InferenceResult
    ): Double {
        var score = sentence.heuristicScore

        // Beri bobot tinggi jika kalimat memuat fakta semantik yang terverifikasi
        for (fact in facts) {
            if (sentence.rawSentence.contains(fact.objectValue, ignoreCase = true)) {
                score += 8.0
            }
        }

        // Beri bobot tambahan jika kalimat memuat kata kunci penanda aksi inferensi
        for (action in inference.actionItems) {
            if (sentence.rawSentence.contains(action, ignoreCase = true)) {
                score += 10.0
            }
        }

        return score
    }
}

