package com.njagakneai.velapdf.domain.nlp

import com.njagakneai.velapdf.domain.model.AssetTextProvider
import com.njagakneai.velapdf.domain.model.NlpLexiconConfig
import com.njagakneai.velapdf.domain.model.NlpSentence
import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.domain.model.ProcessedDocumentText
import com.njagakneai.velapdf.domain.util.SimpleJsonParser
import com.njagakneai.velapdf.domain.util.str
import com.njagakneai.velapdf.domain.util.strList
import com.njagakneai.velapdf.domain.util.strMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prapemrosesan Natural Language Processing (NLP) Luring Bahasa Indonesia.
 * Kata negasi dan modalitas deontik kritis dipertahankan agar makna kalimat
 * kewajiban/larangan tidak berubah setelah penyaringan stopword.
 */
@Singleton
class NlpTextPreprocessor @Inject constructor(
    private val assetProvider: AssetTextProvider
) {

    private var cachedLexicon: NlpLexiconConfig? = null
    private var compiledSectionMarkers: Map<String, Regex> = emptyMap()

    suspend fun loadLexiconFromAsset(assetFileName: String = "nlp_lexicon_id.json"): NlpLexiconConfig =
        withContext(Dispatchers.IO) {
            cachedLexicon?.let { return@withContext it }

            val jsonString = assetProvider.readAsset(assetFileName)
            val root = SimpleJsonParser.parseObject(jsonString)

            val preserved = root.strList("preservedNegationAndModalityTerms")
                .map { it.lowercase() }
                .toSet()
                .ifEmpty { DEFAULT_PRESERVED_NEGATION_AND_MODALITY }

            val config = NlpLexiconConfig(
                lexiconId = root.str("lexiconId", "LEX-VELAPDF-ID"),
                version = root.str("version", "1.1.0"),
                stopwords = root.strList("stopwords")
                    .map { it.lowercase() }
                    .filterNot { it in preserved }
                    .toSet(),
                preservedNegationAndModalityTerms = preserved,
                protectedAbbreviations = root.strList("protectedAbbreviations"),
                sectionMarkers = root.strMap("sectionMarkers"),
                domainBoostTerms = root.strList("domainBoostTerms").map { it.lowercase() }.toSet()
            )
            compiledSectionMarkers = config.sectionMarkers.mapValues { (_, pattern) ->
                Regex(pattern)
            }
            cachedLexicon = config
            config
        }

    fun processDocument(
        documentId: String,
        pages: List<OcrPageText>,
        lexicon: NlpLexiconConfig? = cachedLexicon
    ): ProcessedDocumentText {
        val activeLexicon = lexicon ?: NlpLexiconConfig(
            lexiconId = "FALLBACK",
            version = "1.1.0",
            stopwords = DEFAULT_FALLBACK_STOPWORDS,
            preservedNegationAndModalityTerms = DEFAULT_PRESERVED_NEGATION_AND_MODALITY,
            protectedAbbreviations = listOf("No.", "Hal.", "Dr.", "dkk."),
            sectionMarkers = emptyMap(),
            domainBoostTerms = emptySet()
        )
        if (compiledSectionMarkers.isEmpty() && activeLexicon.sectionMarkers.isNotEmpty()) {
            compiledSectionMarkers = activeLexicon.sectionMarkers.mapValues { (_, pat) -> Regex(pat) }
        }

        val preservedTerms = activeLexicon.preservedNegationAndModalityTerms
            .ifEmpty { DEFAULT_PRESERVED_NEGATION_AND_MODALITY }
        val allSentences = mutableListOf<NlpSentence>()
        val keywordFrequencies = mutableMapOf<String, Int>()
        var sentenceCounter = 1

        for (page in pages) {
            val normalizedPageText = TextExtractionCleaner.normalizeExtractedPageText(page.rawText)
            val splitSentences = splitIntoSentences(normalizedPageText, activeLexicon.protectedAbbreviations)
            for (rawSentence in splitSentences) {
                val cleaned = cleanText(rawSentence)
                if (cleaned.length < 5) continue

                val tokens = tokenize(cleaned)
                val filteredTokens = tokens.filter { token ->
                    preservedTerms.contains(token) ||
                        (token.length > 2 &&
                            !activeLexicon.stopwords.contains(token) &&
                            !token.all { it.isDigit() })
                }

                // Kata negasi/modalitas tetap dipertahankan pada filteredTokens kalimat,
                // sementara topKeywords memfokuskan topik bahasan non-negasi agar akurat.
                for (token in filteredTokens) {
                    if (token !in NEGATION_FUNCTION_TOKENS && token !in MODALITY_FUNCTION_TOKENS) {
                        keywordFrequencies[token] = (keywordFrequencies[token] ?: 0) + 1
                    }
                }

                val hasNegation = tokens.any { it in NEGATION_FUNCTION_TOKENS }
                val section = detectSection(rawSentence)
                var heuristicScore = filteredTokens.size.toDouble()
                val boostMatches = filteredTokens.count { activeLexicon.domainBoostTerms.contains(it) }
                heuristicScore += boostMatches * 2.5

                if (section in setOf("ABSTRACT", "CLAUSE", "HEADER", "DEFINITION", "MECHANISM", "CONCLUSION")) {
                    heuristicScore += 5.0
                }
                if (tokens.any { it == "wajib" || it == "dilarang" || it == "harus" }) {
                    heuristicScore += 2.0
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
                        detectedSection = section,
                        hasNegation = hasNegation
                    )
                )
            }
        }

        val topKeywords = keywordFrequencies.entries
            .sortedByDescending { it.value }
            .take(8)
            .map { it.key }

        return ProcessedDocumentText(
            documentId = documentId,
            totalSentences = allSentences.size,
            sentences = allSentences,
            keywordFrequencies = keywordFrequencies,
            topKeywords = topKeywords
        )
    }

    private fun splitIntoSentences(text: String, protectedAbbreviations: List<String>): List<String> {
        if (text.isBlank()) return emptyList()
        var protectedText = text.replace("\r\n", "\n")

        val placeholders = mutableListOf<Pair<String, String>>()
        protectedAbbreviations.forEachIndexed { idx, abbr ->
            val token = "__ABBR_${idx}__"
            placeholders.add(token to abbr)
            protectedText = protectedText.replace(
                Regex("(?i)" + Regex.escape(abbr) + "\\s*"),
                "$token "
            )
        }

        protectedText = protectedText.replace(Regex("""(\d+)\.(\d+)"""), "$1_NUMDOT_$2")

        val normalizedParagraphs = protectedText.split(Regex("""\n{2,}|(?=\n(?:BAB|Bab|Pasal)\s+[A-Za-z0-9]+)"""))
        val collected = mutableListOf<String>()
        for (para in normalizedParagraphs) {
            val singleLine = para.replace(Regex("""\n+"""), " ")
            val parts = singleLine.split(Regex("""(?<=[.?!])\s+(?=[A-Z0-9"(\[])"""))
            for (part in parts) {
                var restored = part.replace("_NUMDOT_", ".")
                for ((token, orig) in placeholders) {
                    restored = restored.replace(token, orig)
                }
                val trimmed = restored.replace(Regex("""\s+"""), " ").trim()
                if (trimmed.isNotEmpty()) {
                    collected.add(trimmed)
                }
            }
        }
        return collected
    }

    private fun cleanText(text: String): String {
        return text
            .replace(Regex("""[^\w\s.,;:()/\-–]"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun tokenize(text: String): List<String> {
        return text.lowercase()
            .split(Regex("""[^\w\-]+"""))
            .filter { it.isNotBlank() }
    }

    private fun detectSection(sentence: String): String {
        val priorityOrder = listOf(
            "CLAUSE",
            "CONCLUSION",
            "ABSTRACT",
            "DEFINITION",
            "MECHANISM",
            "HEADER",
            "OPENING",
            "CLOSING"
        )
        for (key in priorityOrder) {
            val regex = compiledSectionMarkers[key]
            if (regex != null && regex.containsMatchIn(sentence)) {
                return key
            }
        }
        return "BODY"
    }

    companion object {
        private val NEGATION_FUNCTION_TOKENS = setOf(
            "tidak", "tidakkah", "tidaklah",
            "bukan", "bukankah", "bukanlah", "bukannya",
            "belum", "belumlah",
            "jangan", "jangankan", "janganlah",
            "tak", "tanpa", "enggak", "enggaknya"
        )

        private val MODALITY_FUNCTION_TOKENS = setOf(
            "harus", "haruslah", "harusnya", "wajib", "dilarang", "kurang"
        )

        private val DEFAULT_PRESERVED_NEGATION_AND_MODALITY =
            NEGATION_FUNCTION_TOKENS + MODALITY_FUNCTION_TOKENS

        private val DEFAULT_FALLBACK_STOPWORDS = setOf(
            "yang", "di", "ke", "dari", "pada", "dalam", "untuk", "dengan", "dan", "atau",
            "ini", "itu", "adalah", "merupakan", "sebagai", "oleh", "bahwa", "tersebut"
        )
    }
}
