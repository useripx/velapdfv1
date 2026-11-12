package com.njagakneai.velapdf.domain.nlp

import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.testutil.TestAssetProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NlpLexiconAndPreprocessorTest {

    private lateinit var preprocessor: NlpTextPreprocessor

    @Before
    fun setup() {
        preprocessor = NlpTextPreprocessor(TestAssetProvider)
    }

    @Test
    fun `loadLexiconFromAsset parses stopwords abbreviations sectionMarkers and domainBoostTerms`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()

        assertTrue("Daftar stopword harus cukup besar", lexicon.stopwords.size >= 300)
        assertTrue(lexicon.stopwords.contains("yang"))
        assertTrue(lexicon.stopwords.contains("dan"))
        assertTrue(lexicon.protectedAbbreviations.any { it.equals("No.", ignoreCase = true) })
        assertTrue(lexicon.protectedAbbreviations.any { it.equals("Hal.", ignoreCase = true) })
        assertTrue(lexicon.domainBoostTerms.contains("sensor"))
        assertTrue(lexicon.domainBoostTerms.contains("protokol"))
    }

    @Test
    fun `processDocument protects abbreviations and extracts Indonesian domain keywords`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()
        val pages = listOf(
            OcrPageText(
                pageNumber = 23,
                rawText = """
                    BAB III ARSITEKTUR INTERNET OF THINGS (IoT)
                    Nomor: Hal. 23 Dr. Budi Susanto menjelaskan bahwa sensor suhu membaca data lingkungan fisik.
                    Mikrokontroler ESP32 berfungsi sebagai node pemroses utama sebelum dikirim melalui protokol MQTT.
                """.trimIndent()
            )
        )

        val result = preprocessor.processDocument("doc-nlp-1", pages, lexicon)

        assertTrue("Minimal 2 kalimat terbaca", result.totalSentences >= 2)
        assertFalse(
            "Singkatan 'Dr.' tidak boleh memotong kalimat secara keliru",
            result.sentences.any { it.rawSentence.trim().endsWith("Dr.") }
        )
        assertTrue("Top keywords tidak boleh berisi stopword 'yang'", !result.topKeywords.contains("yang"))
        assertTrue(
            "Top keywords harus memuat istilah domain teknis",
            result.topKeywords.any { it in setOf("sensor", "iot", "mikrokontroler", "mqtt", "suhu") }
        )
    }

    @Test
    fun `Celah B preserves negation and modality terms in filteredTokens without polluting stopwords`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()

        assertFalse("'tidak' tidak boleh ada di stopwords", "tidak" in lexicon.stopwords)
        assertFalse("'bukan' tidak boleh ada di stopwords", "bukan" in lexicon.stopwords)
        assertFalse("'belum' tidak boleh ada di stopwords", "belum" in lexicon.stopwords)
        assertFalse("'harus' tidak boleh ada di stopwords", "harus" in lexicon.stopwords)
        assertTrue("'tidak' harus dilestarikan", "tidak" in lexicon.preservedNegationAndModalityTerms)
        assertTrue("'harus' harus dilestarikan", "harus" in lexicon.preservedNegationAndModalityTerms)

        val pages = listOf(
            OcrPageText(
                pageNumber = 1,
                rawText = """
                    ABSTRAK
                    Pihak terkait wajib mengumpulkan laporan bulanan secara tepat waktu.
                    Pihak terkait tidak wajib mengumpulkan laporan tambahan setelah masa evaluasi berakhir.
                """.trimIndent()
            )
        )

        val processed = preprocessor.processDocument("doc-neg-b", pages, lexicon)
        val nonObligSentence = processed.sentences.first { it.rawSentence.contains("tidak wajib") }
        val obligSentence = processed.sentences.first { !it.rawSentence.contains("tidak wajib") && it.rawSentence.contains("wajib") }

        assertTrue("Kalimat 'tidak wajib' harus menandai hasNegation=true", nonObligSentence.hasNegation)
        assertFalse("Kalimat positif tidak boleh menandai hasNegation", obligSentence.hasNegation)
        assertTrue("Token 'tidak' wajib tetap ada di filteredTokens", "tidak" in nonObligSentence.filteredTokens)
        assertFalse("Negasi 'tidak' tidak boleh mengotori topKeywords topik", "tidak" in processed.topKeywords)
    }
}
