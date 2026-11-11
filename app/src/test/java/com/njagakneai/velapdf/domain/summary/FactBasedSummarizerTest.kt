package com.njagakneai.velapdf.domain.summary

import com.njagakneai.velapdf.domain.inference.RuleInferenceEngine
import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.domain.nlp.NlpTextPreprocessor
import com.njagakneai.velapdf.domain.ontology.OntologyLoader
import com.njagakneai.velapdf.testutil.TestAssetProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FactBasedSummarizerTest {

    private lateinit var preprocessor: NlpTextPreprocessor
    private lateinit var ontologyLoader: OntologyLoader
    private lateinit var ruleEngine: RuleInferenceEngine
    private lateinit var summarizer: FactBasedSummarizer

    @Before
    fun setup() {
        preprocessor = NlpTextPreprocessor(TestAssetProvider)
        ontologyLoader = OntologyLoader(TestAssetProvider)
        ruleEngine = RuleInferenceEngine(TestAssetProvider)
        summarizer = FactBasedSummarizer()
    }

    @Test
    fun `generateDocumentSummary produces structured Indonesian conclusion with page attributions`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
        val rules = ruleEngine.loadRulesFromAsset()

        val pages = listOf(
            OcrPageText(
                pageNumber = 23,
                rawText = "Internet of Things merupakan paradigma jaringan objek fisik yang saling terhubung melalui sensor cerdas dan protokol komunikasi nirkabel."
            ),
            OcrPageText(
                pageNumber = 24,
                rawText = "Arsitektur dasar IoT terdiri dari lapisan persepsi, lapisan jaringan transmisi data, serta lapisan pemrosesan aplikasi cloud."
            ),
            OcrPageText(
                pageNumber = 29,
                rawText = "Protokol MQTT bekerja dengan pola publish-subscribe yang sangat hemat bandwidth untuk perangkat mikrokontroler berdaya rendah."
            )
        )
        val metadata = DocumentMetadata(
            documentId = "doc-summary-iot",
            fileName = "Buku_IoT_Bab3.pdf",
            pageRangeLabel = "Hal. 23 - 29",
            pageCount = 3
        )

        val processed = preprocessor.processDocument(metadata.documentId, pages, lexicon)
        val facts = ontologyLoader.extractSemanticFacts(metadata.documentId, processed, taxonomy, spoDict)
        val inference = ruleEngine.executeForwardChaining(metadata.documentId, facts, rules)
        val summary = summarizer.generateDocumentSummary(metadata, processed, facts, inference)

        assertEquals("Buku Ajar / Referensi Teknis", summary.detectedDocumentType)
        assertTrue("Kesimpulan Bahasa Indonesia tidak boleh kosong", summary.conclusionParagraph.length > 40)
        assertTrue("Kesimpulan harus menyebut jenis dokumen", summary.conclusionParagraph.contains("Buku Ajar", ignoreCase = true))
        assertTrue("Minimal 2 poin intisari harus terbentuk", summary.keyPoints.size >= 2)
        assertTrue(
            "Referensi halaman harus tercatat (23, 24, atau 29)",
            summary.pageReferences.values.all { it in listOf(23, 24, 29) }
        )
        assertFalse("Top keywords harus tersedia", summary.topKeywords.isEmpty())
    }

    @Test
    fun `generateDocumentSummary handles short minimal text safely without crashing`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
        val rules = ruleEngine.loadRulesFromAsset()

        val pages = listOf(OcrPageText(1, "Pengumuman singkat rapat kerja."))
        val metadata = DocumentMetadata(
            documentId = "doc-short",
            fileName = "Catatan.jpg",
            pageRangeLabel = "Hal. 1",
            pageCount = 1
        )

        val processed = preprocessor.processDocument(metadata.documentId, pages, lexicon)
        val facts = ontologyLoader.extractSemanticFacts(metadata.documentId, processed, taxonomy, spoDict)
        val inference = ruleEngine.executeForwardChaining(metadata.documentId, facts, rules)
        val summary = summarizer.generateDocumentSummary(metadata, processed, facts, inference)

        assertTrue("Paragraf kesimpulan tetap terbentuk aman", summary.conclusionParagraph.isNotBlank())
        assertTrue("Fallback keyPoints harus terisi", summary.keyPoints.isNotEmpty())
    }
}
