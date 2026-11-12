package com.njagakneai.velapdf.domain.nlp

/**
 * Utilitas murni pembersihan teks hasil ekstraksi PDF native (iText7) maupun OCR (ML Kit):
 * - Deteksi apakah teks PDF native layak pakai atau harus fallback ke ML Kit OCR.
 * - Pengurutan blok berbasis kolom spasial (agar jurnal 2-kolom tidak tercampur per baris).
 * - Penyambungan kata terpenggal tanda hubung di akhir baris & normalisasi baris terbungkus.
 */
object TextExtractionCleaner {

    data class SpatialTextBlock(
        val text: String,
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) {
        val width: Int get() = (right - left).coerceAtLeast(1)
        val centerX: Double get() = (left + right) / 2.0
    }

    private val ISOLATED_PAGE_NOISE = Regex(
        """^\s*(?:halaman|hal\.?|page)?\s*\d{1,4}\s*(?:[/|]\s*\d{1,4}|dari\s*\d{1,4})?\s*$""",
        RegexOption.IGNORE_CASE
    )
    private val SEPARATOR_LINE = Regex("""^\s*[-_=*~•·.]{3,}\s*$""")
    private val HYPHEN_WRAP_REGEX = Regex("""([A-Za-zÀ-ÖØ-öø-ÿ]+)\s*-\s*\n\s*([a-zà-öø-ÿ]+)""")
    private val STRUCTURAL_LINE_START = Regex(
        """^(?:\d+[.)]\s+|[a-zA-Z][.)]\s+|[-•*]\s+|\b(?:BAB|Bab|Pasal|Menimbang|Mengingat|Memperhatikan|MENETAPKAN|Kesimpulan|KESIMPULAN|Abstrak|ABSTRAK)\b)"""
    )

    /**
     * Memilih hasil ekstraksi teks native PDF paling bersih antara stream-order (bagus untuk
     * artikel 2-kolom) dan location-order (cadangan bila stream-order kehilangan spasi antar-kata).
     * Mengembalikan null jika halaman ternyata berupa scan gambar/encoding rusak agar fallback ke ML Kit OCR.
     */
    fun chooseBestNativeExtraction(streamRaw: String, locationRaw: String): String? {
        val streamOk = isMeaningfulNativePdfText(streamRaw) && !hasConcatenatedWordGlitches(streamRaw)
        if (streamOk) {
            return normalizeExtractedPageText(streamRaw)
        }
        val locationOk = isMeaningfulNativePdfText(locationRaw)
        if (locationOk) {
            return normalizeExtractedPageText(locationRaw)
        }
        return null
    }

    internal fun hasConcatenatedWordGlitches(rawText: String): Boolean {
        val compact = rawText.trim()
        if (compact.length < 80) return false
        val tokens = compact.split(Regex("""\s+""")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return true
        val longGluedTokens = tokens.count { tok -> tok.count { it.isLetter() } >= 26 }
        return longGluedTokens.toDouble() / tokens.size.toDouble() > 0.15
    }

    fun isMeaningfulNativePdfText(rawText: String): Boolean {
        if (rawText.isBlank()) return false
        val stripped = rawText.filterNot { it.isWhitespace() }
        if (stripped.length < 25) return false

        val replacementCount = stripped.count { it == '\uFFFD' }
        if (replacementCount.toDouble() / stripped.length.toDouble() > 0.03) return false

        val letterCount = stripped.count { it.isLetter() }
        if (letterCount < 12) return false

        val readableTokens = rawText.trim()
            .split(Regex("""\s+"""))
            .filter { token -> token.any { it.isLetterOrDigit() } }
        if (readableTokens.size < 4) return false

        // PDF dengan CMap/ToUnicode rusak sering mengekstrak huruf terpisah "a b c d".
        val singleCharTokens = readableTokens.count { tok -> tok.count { it.isLetterOrDigit() } == 1 }
        if (readableTokens.size >= 10 && singleCharTokens.toDouble() / readableTokens.size > 0.45) {
            return false
        }

        val multiLetterWordsWithVowel = readableTokens.count { tok ->
            val letters = tok.filter { it.isLetter() }.lowercase()
            letters.length >= 3 && letters.any { it in "aiueo" }
        }
        return multiLetterWordsWithVowel >= 2
    }

    fun normalizeExtractedPageText(rawText: String): String {
        if (rawText.isBlank()) return ""
        val sanitized = rawText
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .replace("\u00AD", "")
            .replace('\u00A0', ' ')
            .replace(Regex("""[\u200B-\u200D\uFEFF]"""), "")

        // Sambung kata terpenggal garis hubung ("pem-\nrosesan" -> "pemrosesan"),
        // sambil mempertahankan kata ulang Bahasa Indonesia ("undang-\nundang" -> "undang-undang").
        val rejoinedHyphens = HYPHEN_WRAP_REGEX.replace(sanitized) { match ->
            val left = match.groupValues[1]
            val right = match.groupValues[2]
            if (left.equals(right, ignoreCase = true)) "$left-$right" else "$left$right"
        }

        val filteredLines = rejoinedHyphens.lines()
            .map { line -> line.replace(Regex("""[ \t]{2,}"""), " ").trim() }
            .filterNot { line ->
                line.isNotEmpty() && (ISOLATED_PAGE_NOISE.matches(line) || SEPARATOR_LINE.matches(line))
            }

        val out = StringBuilder()
        var previousLine = ""
        for (line in filteredLines) {
            if (line.isEmpty()) {
                if (out.isNotEmpty() && !out.endsWith("\n\n")) {
                    out.append("\n\n")
                }
                previousLine = ""
                continue
            }

            if (out.isEmpty() || out.endsWith("\n\n")) {
                out.append(line)
            } else if (STRUCTURAL_LINE_START.containsMatchIn(line)) {
                val lastChar = previousLine.lastOrNull()
                if (lastChar != null && lastChar !in setOf('.', '?', '!', ':', ';')) {
                    out.append('.')
                }
                out.append('\n').append(line)
            } else {
                val prevEndedCleanly = previousLine.lastOrNull() in setOf('.', '?', '!')
                val curStartsUpper = line.firstOrNull()?.isUpperCase() == true
                if (prevEndedCleanly && curStartsUpper) {
                    out.append(' ').append(line)
                } else {
                    out.append(' ').append(line)
                }
            }
            previousLine = line
        }

        return out.toString()
            .replace(Regex("""\n{3,}"""), "\n\n")
            .trim()
    }

    /**
     * Mengurutkan blok teks hasil ML Kit secara sadar-kolom:
     * bila terdeteksi tata letak 2-kolom (kolom kiri & kanan yang tumpang tindih vertikal),
     * header atas -> kolom kiri (atas ke bawah) -> kolom kanan (atas ke bawah) -> footer bawah.
     */
    fun orderSpatialBlocks(blocks: List<SpatialTextBlock>): String {
        val cleanBlocks = blocks.filter { it.text.isNotBlank() }
        if (cleanBlocks.isEmpty()) return ""
        if (cleanBlocks.size == 1) return normalizeExtractedPageText(cleanBlocks.first().text)

        val minLeft = cleanBlocks.minOf { it.left }
        val maxRight = cleanBlocks.maxOf { it.right }
        val span = (maxRight - minLeft).coerceAtLeast(1)
        val midX = minLeft + span / 2.0

        val wideThreshold = span * 0.62
        val narrowColThreshold = span * 0.56

        val narrowLeft = cleanBlocks.filter { it.width <= narrowColThreshold && it.centerX < midX - span * 0.04 }
        val narrowRight = cleanBlocks.filter { it.width <= narrowColThreshold && it.centerX > midX + span * 0.04 }

        val hasTwoColumnOverlap = if (narrowLeft.size >= 2 && narrowRight.size >= 2) {
            val leftTop = narrowLeft.minOf { it.top }
            val leftBottom = narrowLeft.maxOf { it.bottom }
            val rightTop = narrowRight.minOf { it.top }
            val rightBottom = narrowRight.maxOf { it.bottom }
            val overlap = minOf(leftBottom, rightBottom) - maxOf(leftTop, rightTop)
            overlap > 40
        } else {
            false
        }

        val ordered = if (hasTwoColumnOverlap) {
            val columnRegionTop = minOf(narrowLeft.minOf { it.top }, narrowRight.minOf { it.top })
            val columnRegionBottom = maxOf(narrowLeft.maxOf { it.bottom }, narrowRight.maxOf { it.bottom })

            val topHeaders = cleanBlocks
                .filter { it.width > wideThreshold && it.bottom <= columnRegionTop + 20 }
                .sortedWith(compareBy<SpatialTextBlock> { it.top }.thenBy { it.left })
            val bottomFooters = cleanBlocks
                .filter { it.width > wideThreshold && it.top >= columnRegionBottom - 20 }
                .sortedWith(compareBy<SpatialTextBlock> { it.top }.thenBy { it.left })

            val remaining = cleanBlocks - topHeaders.toSet() - bottomFooters.toSet()
            val leftCol = remaining
                .filter { it.centerX <= midX }
                .sortedWith(compareBy<SpatialTextBlock> { it.top }.thenBy { it.left })
            val rightCol = remaining
                .filter { it.centerX > midX }
                .sortedWith(compareBy<SpatialTextBlock> { it.top }.thenBy { it.left })

            topHeaders + leftCol + rightCol + bottomFooters
        } else {
            cleanBlocks.sortedWith(compareBy<SpatialTextBlock> { it.top / 16 }.thenBy { it.left })
        }

        val joined = ordered.joinToString("\n\n") { it.text.trim() }
        return normalizeExtractedPageText(joined)
    }
}
