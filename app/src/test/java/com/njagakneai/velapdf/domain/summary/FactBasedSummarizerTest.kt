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

    @Test
    fun `Celah D separates multi-page Document Summarization from Rule-Based Key-Point Extraction`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
        val rules = ruleEngine.loadRulesFromAsset()

        val pages = listOf(
            OcrPageText(
                1,
                """
                    BAB I PENDAHULUAN
                    Sistem pemantauan kualitas udara berbasis Internet of Things dirancang untuk mengukur kadar partikulat secara berkelanjutan.
                """.trimIndent()
            ),
            OcrPageText(
                2,
                """
                    Operator laboratorium wajib melakukan kalibrasi sensor sebelum pengujian dilakukan.
                """.trimIndent()
            ),
            OcrPageText(
                3,
                """
                    BAB III METODOLOGI DAN ARSITEKTUR
                    Mikrokontroler ESP32 berfungsi untuk membaca data sensor gas MQ-135 dan mengirimkannya ke server lokal melalui protokol MQTT.
                """.trimIndent()
            ),
            OcrPageText(
                6,
                """
                    KESIMPULAN
                    Implementasi arsitektur pengiriman data lokal terbukti hemat daya dan tetap berjalan stabil tanpa ketergantungan koneksi internet.
                """.trimIndent()
            )
        )
        val metadata = DocumentMetadata(
            documentId = "doc-celah-d",
            fileName = "Buku_Monitoring_Udara.pdf",
            pageRangeLabel = "Hal. 1 - 6",
            pageCount = 4
        )

        val processed = preprocessor.processDocument(metadata.documentId, pages, lexicon)
        val facts = ontologyLoader.extractSemanticFacts(metadata.documentId, processed, taxonomy, spoDict)
        val inference = ruleEngine.executeForwardChaining(metadata.documentId, facts, rules)
        val (bindings, missing) = ontologyLoader.mapFactsToOntologyProperties(taxonomy, facts)
        val summary = summarizer.generateDocumentSummary(
            metadata = metadata,
            processedDoc = processed,
            extractedFacts = facts,
            inferenceResult = inference,
            ontologyPropertyBindings = bindings,
            missingRequiredProperties = missing
        )

        val coveredPages = summary.contentSummaryPoints.mapNotNull { summary.pageReferences[it] }.toSet()
        assertTrue(
            "Document summarization harus mencakup bagian awal (Hal.1) DAN akhir (Hal.6) secara berstrata; covered=$coveredPages",
            1 in coveredPages && 6 in coveredPages
        )
        assertTrue(
            "ruleBasedKeyPoints harus memuat poin aturan deontik/inferensi terpisah dari contentSummaryPoints",
            summary.ruleBasedKeyPoints.any { it.contains("Kewajiban", ignoreCase = true) || it.contains("KEWAJIBAN", ignoreCase = true) || it.contains("Klasifikasi", ignoreCase = true) }
        )
        assertFalse(
            "Template aturan inferensi tidak boleh bercampur sebagai isi contentSummaryPoints",
            summary.contentSummaryPoints.any { it.startsWith("Klasifikasi Aturan:") || it.startsWith("PERHATIAN KRITIS:") }
        )
        assertEquals(bindings, summary.ontologyPropertyBindings)
        assertEquals(missing, summary.missingRequiredProperties)
    }

    @Test
    fun `OBS-NLP-001 short single-page formal SK letter produces strictly positive compression ratio`() = runBlocking {
        val lexicon = preprocessor.loadLexiconFromAsset()
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
        val rules = ruleEngine.loadRulesFromAsset()

        val shortSkText = """
            SURAT KEPUTUSAN REKTOR UNIVERSITAS NUSANTARA PENDIDIKAN
            Nomor: 042/SK-UNP/AKD/2026
            Tentang: Penetapan Kalender Akademik Semester Genap 2026/2027
            Menimbang bahwa kelancaran proses belajar mengajar dan evaluasi akhir mahasiswa memerlukan jadwal resmi yang mengikat seluruh program studi.
            Mengingat Statuta Universitas dan Peraturan Akademik Tahun 2025 tentang penyelenggaraan perkuliahan dan evaluasi semester.
            Memperhatikan hasil Rapat Pimpinan Universitas tanggal 10 Oktober 2026 di gedung rektorat utama.
            MEMUTUSKAN:
            Pertama, setiap mahasiswa wajib menyelesaikan pengisian Kartu Rencana Studi paling lambat 15 Januari 2027 melalui portal akademik resmi.
            Kedua, dosen pengampu mata kuliah harus mengunggah berkas rancangan pembelajaran semester sebelum perkuliahan minggu pertama dimulai.
            Ketiga, keterlambatan tanpa alasan sah dilarang mengikuti ujian akhir semester sesuai ketentuan yang berlaku di lingkungan fakultas.
            Ditetapkan di Kediri pada tanggal 12 Oktober 2026.
        """.trimIndent()

        val metadata = DocumentMetadata(
            documentId = "doc-sk01-short",
            fileName = "SK-01_Surat_Keputusan_Rektor.pdf",
            pageRangeLabel = "Hal. 1",
            pageCount = 1
        )
        val pages = listOf(OcrPageText(1, shortSkText))
        val processed = preprocessor.processDocument(metadata.documentId, pages, lexicon)
        val facts = ontologyLoader.extractSemanticFacts(metadata.documentId, processed, taxonomy, spoDict)
        val inference = ruleEngine.executeForwardChaining(metadata.documentId, facts, rules)
        val summary = summarizer.generateDocumentSummary(metadata, processed, facts, inference)

        assertTrue("Total kata sumber sekitar 120-200 kata (actual=${summary.totalOriginalWords})", summary.totalOriginalWords in 100..220)
        assertTrue(
            "Rasio pemadatan teks harus > 15% (actual=${summary.compressionRatioPercentage}%, ${summary.totalOriginalWords}->${summary.summaryWordsCount})",
            summary.compressionRatioPercentage > 15.0
        )
        assertTrue("Kata ringkasan harus lebih sedikit dari kata asli", summary.summaryWordsCount < summary.totalOriginalWords)
        assertTrue("Jenis dokumen harus terdeteksi Surat Keputusan", summary.detectedDocumentType.contains("Surat Keputusan", ignoreCase = true))
    }
}
