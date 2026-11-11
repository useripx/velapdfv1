package com.njagakneai.velapdf.metopen.domain.nlp

import com.njagakneai.velapdf.metopen.domain.model.NlpSentence
import com.njagakneai.velapdf.metopen.domain.model.OcrPageText
import com.njagakneai.velapdf.metopen.domain.model.ProcessedDocumentText

/**
 * METOPEN TA 2026/2027 - PROYEK VELAPDF
 * Mahasiswa: Yogi Ario Pratama (NPM: 2313020004)
 * 
 * Modul Natural Language Processing (NLP) Ringan Lokal Bahasa Indonesia
 * Menjalankan segmentasi kalimat, pembersihan teks, tokenisasi, penyaringan kata tugas
 * (stopword removal), serta pembobotan heuristik relevansi konsep dokumen secara luring.
 */
class NlpTextPreprocessor {

    // Korpus Kata Tugas (Stopwords) Bahasa Indonesia Standar untuk Dokumen Resmi/Akademik
    private val indonesianStopwords = setOf(
        "yang", "di", "ke", "dari", "pada", "dalam", "untuk", "dengan", "dan", "atau",
        "ini", "itu", "adalah", "sebagai", "oleh", "bahwa", "tersebut", "maka", "akan",
        "dapat", "telah", "bisa", "ada", "karena", "kepada", "antara", "serta", "atas",
        "secara", "bagi", "setiap", "para", "suatu", "agar", "supaya", "jika", "apabila",
        "namun", "seperti", "melalui", "tanpa", "ia", "kami", "mereka", "kita", "kamu"
    )

    // Pola penanda struktur dokumen formal (Section Markers)
    private val headerPattern = Regex("""(?i)(pemerintah|kementerian|yayasan|universitas|institut|sekolah|dinas|kantor|fakultas)""")
    private val openingPattern = Regex("""(?i)(menimbang|mengingat|memperhatikan|dasar\s*hukum|latar\s*belakang)""")
    private val clausePattern = Regex("""(?i)(memutuskan|menetapkan|pasal|ketentuan|kewajiban|larangan|hak|jadwal)""")
    private val closingPattern = Regex("""(?i)(ditetapkan\s*di|pada\s*tanggal|rektor|dekan|ketua|kepala|direktur|tertanda)""")

    /**
     * Memproses teks mentah per halaman menjadi kumpulan kalimat terstruktur,
     * token-token bermakna, dan sebaran kata kunci.
     */
    fun processDocument(documentId: String, pages: List<OcrPageText>): ProcessedDocumentText {
        val allSentences = mutableListOf<NlpSentence>()
        val keywordFrequencies = mutableMapOf<String, Int>()
        var sentenceCounter = 1

        for (page in pages) {
            val rawPageText = page.rawText
            val splitSentences = splitIntoSentences(rawPageText)

            for (rawSentence in splitSentences) {
                val cleaned = cleanText(rawSentence)
                if (cleaned.length < 5) continue // Abaikan penggalan terlalu pendek/artefak garis

                val tokens = tokenize(cleaned)
                val filteredTokens = tokens.filter { token ->
                    token.length > 2 && !indonesianStopwords.contains(token) && !token.all { it.isDigit() }
                }

                // Hitung frekuensi kata kunci
                for (token in filteredTokens) {
                    keywordFrequencies[token] = (keywordFrequencies[token] ?: 0) + 1
                }

                // Identifikasi seksi dokumen
                val section = detectSection(rawSentence)

                // Hitung skor heuristik awal (panjang token informatif + penanda seksi penting)
                var heuristicScore = filteredTokens.size.toDouble()
                if (section == "CLAUSE" || section == "HEADER") {
                    heuristicScore += 5.0
                }

                allSentences.add(
                    NlpSentence(
                        sentenceIndex = sentenceCounter++,
                        pageNumber = page.pageNumber,
                        rawSentence = rawSentence.trim(),
                        cleanedSentence = cleaned,
                        tokens = tokens,
                        filteredTokens = filteredTokens,
                        heuristicScore = heuristicScore,
                        detectedSection = section
                    )
                )
            }
        }

        return ProcessedDocumentText(
            documentId = documentId,
            totalSentences = allSentences.size,
            sentences = allSentences,
            keywordFrequencies = keywordFrequencies
        )
    }

    /**
     * Memecah blok teks menjadi kalimat-kalimat mandiri (Sentence Boundary Disambiguation),
     * dengan melindungi tanda titik pada singkatan gelar atau penomoran surat.
     */
    private fun splitIntoSentences(text: String): List<String> {
        // Ganti sementara titik singkatan umum agar tidak terpecah salah
        val protectedText = text
            .replace(Regex("""(?i)No\.\s*"""), "No_DOT_ ")
            .replace(Regex("""(?i)Dr\.\s*"""), "Dr_DOT_ ")
            .replace(Regex("""(?i)M\.Kom\.\s*"""), "M_DOT_Kom_DOT_ ")
            .replace(Regex("""(?i)S\.Kom\.\s*"""), "S_DOT_Kom_DOT_ ")
            .replace(Regex("""(?i)H\.\s*"""), "H_DOT_ ")
            .replace(Regex("""(?i)dkk\.\s*"""), "dkk_DOT_ ")
            .replace(Regex("""(?i)Hal\.\s*"""), "Hal_DOT_ ")
            .replace(Regex("""(?i)Lamp\.\s*"""), "Lamp_DOT_ ")

        // Pisahkan berdasarkan tanda akhir kalimat (. ? !) atau baris baru ganda
        val rawSplits = protectedText.split(Regex("""(?<=[.?!])\s+(?=[A-Z0-9])|\n{2,}"""))

        // Kembalikan tanda titik singkatan
        return rawSplits.map { s ->
            s.replace("_DOT_", ".")
                .replace(Regex("""\s+"""), " ")
                .trim()
        }.filter { it.isNotEmpty() }
    }

    /**
     * Membersihkan karakter anomali, tanda baca berlebih, dan normalisasi spasi.
     */
    private fun cleanText(text: String): String {
        return text
            .replace(Regex("""[^\w\s.,;:()/\-–]"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    /**
     * Memotong kalimat menjadi token-token kata dalam huruf kecil (case folding).
     */
    private fun tokenize(text: String): List<String> {
        return text.lowercase()
            .split(Regex("""[^\w\-]+"""))
            .filter { it.isNotBlank() }
    }

    /**
     * Mendeteksi posisi struktural kalimat dalam dokumen formal.
     */
    private fun detectSection(sentence: String): String {
        return when {
            clausePattern.containsMatchIn(sentence) -> "CLAUSE"
            headerPattern.containsMatchIn(sentence) -> "HEADER"
            openingPattern.containsMatchIn(sentence) -> "OPENING"
            closingPattern.containsMatchIn(sentence) -> "CLOSING"
            else -> "BODY"
        }
    }
}

