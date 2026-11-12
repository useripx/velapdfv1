package com.njagakneai.velapdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Debug
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.njagakneai.velapdf.data.database.AppDatabase
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import com.njagakneai.velapdf.data.ocr.MlKitRepository
import com.njagakneai.velapdf.data.repository.deleteAnalysesBatched
import com.njagakneai.velapdf.domain.inference.RuleInferenceEngine
import com.njagakneai.velapdf.domain.model.AssetTextProvider
import com.njagakneai.velapdf.domain.model.DocumentMetadata
import com.njagakneai.velapdf.domain.nlp.NlpTextPreprocessor
import com.njagakneai.velapdf.domain.ontology.OntologyLoader
import com.njagakneai.velapdf.domain.summary.FactBasedSummarizer
import com.njagakneai.velapdf.ui.viewmodel.filterAnalysisHistory
import com.njagakneai.velapdf.utils.SummaryPdfExporter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class TestbedPipelineBenchmarkTest {

    private fun appContext(): Context =
        InstrumentationRegistry.getInstrumentation().targetContext

    private fun assetProvider(context: Context): AssetTextProvider =
        AssetTextProvider { fileName ->
            context.assets.open(fileName).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }

    @Test
    fun roomDatabase_historySearchAndBatchDelete_operatesCorrectlyInSqlite() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(appContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.analysisHistoryDao()
            val id1 = dao.insertAnalysis(
                AnalysisHistoryEntity(
                    documentTitle = "SK-01_Surat_Keputusan_Rektor.pdf",
                    documentType = "Surat Keputusan (SK)",
                    dominantTopic = "Kalender Akademik",
                    summaryText = "1. Registrasi maksimal 31 Januari 2027",
                    conclusionText = "Keputusan resmi jadwal semester genap.",
                    pageRangeLabel = "Hal. 1 - 2",
                    factsCount = 6,
                    sourceUri = "content://test/1",
                    timestamp = 100L
                )
            ).toInt()
            val id2 = dao.insertAnalysis(
                AnalysisHistoryEntity(
                    documentTitle = "12_Penerapan_Model_Ontologi.pdf",
                    documentType = "Artikel Jurnal Ilmiah",
                    dominantTopic = "Model Ontologi Game Digital",
                    summaryText = "1. Ontology Development 101",
                    conclusionText = "Artikel ilmiah membahas ontologi pada game digital.",
                    pageRangeLabel = "Hal. 1 - 10",
                    factsCount = 8,
                    sourceUri = "content://test/2",
                    timestamp = 200L
                )
            ).toInt()
            val id3 = dao.insertAnalysis(
                AnalysisHistoryEntity(
                    documentTitle = "Roadmap.pdf",
                    documentType = "Dokumen Informatif / Materi Bacaan",
                    dominantTopic = "Peta Jalan Penelitian",
                    summaryText = "1. Tahapan sistem offline",
                    conclusionText = "Roadmap pengembangan penelitian.",
                    pageRangeLabel = "Hal. 1 - 19",
                    factsCount = 4,
                    sourceUri = "content://test/3",
                    timestamp = 300L
                )
            ).toInt()

            val allRows = dao.getAllAnalyses().first()
            assertEquals(3, allRows.size)

            val searched = filterAnalysisHistory(allRows, "ontologi")
            assertEquals(listOf(id2), searched.map { it.id })

            deleteAnalysesBatched(dao, listOf(id1, id3))

            val remaining = dao.getAllAnalyses().first()
            assertEquals(1, remaining.size)
            assertEquals(id2, remaining.first().id)
        } finally {
            db.close()
        }
    }

    @Test
    fun offlineAssetsAndInferencePipeline_benchmarkAllAvailableTestbedPdfs() = runBlocking {
        val context = appContext()
        val provider = assetProvider(context)
        val mlKitRepo = MlKitRepository(context)
        val preprocessor = NlpTextPreprocessor(provider)
        val ontologyLoader = OntologyLoader(provider)
        val ruleEngine = RuleInferenceEngine(provider)
        val summarizer = FactBasedSummarizer()
        val pdfExporter = SummaryPdfExporter(context)

        val lexicon = preprocessor.loadLexiconFromAsset()
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
        val rules = ruleEngine.loadRulesFromAsset()

        assertTrue("NLP stopwords harus >= 700", lexicon.stopwords.size >= 700)
        assertTrue("mengindikasikanKonsep wajib ada", "mengindikasikanKonsep" in taxonomy.relations)

        val testbedDir = File(context.getExternalFilesDir("testbed")?.absolutePath ?: context.filesDir.absolutePath)
        val pushedPdfs = testbedDir.listFiles { file ->
            file.isFile && file.name.endsWith(".pdf", ignoreCase = true) &&
                !file.name.startsWith("benchmark_", ignoreCase = true)
        }?.sortedBy { it.name }.orEmpty()

        val targetPdfs = if (pushedPdfs.isNotEmpty()) {
            pushedPdfs
        } else {
            listOf(createSyntheticSamplePdf(context))
        }

        val csvLines = mutableListOf(
            "file_name,total_pages,ocr_ms,nlp_inf_ms,total_ms,detected_type,facts_count,fired_rules_count,fired_rules,content_summary_points,rule_key_points,ontology_bindings,missing_required,java_heap_before_mb,java_heap_after_mb,java_heap_delta_mb,native_heap_after_mb,total_pss_mb"
        )

        var maxJavaDeltaMb = 0.0
        var exportedSampleUri: Uri? = null

        for (pdfFile in targetPdfs) {
            Runtime.getRuntime().gc()
            val runtime = Runtime.getRuntime()
            val javaBeforeMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)

            val pdfUri = Uri.fromFile(pdfFile)
            val totalPagesResult = mlKitRepo.getPdfPageCount(pdfUri)
            assertTrue("Gagal membaca jumlah halaman ${pdfFile.name}", totalPagesResult.isSuccess)
            val totalPages = totalPagesResult.getOrThrow()

            val ocrStart = System.currentTimeMillis()
            val ocrResult = mlKitRepo.extractFromPdfUri(
                pdfUri = pdfUri,
                startPage = 1,
                endPage = totalPages,
                maxPagesLimit = 25
            )
            val ocrMs = System.currentTimeMillis() - ocrStart
            assertTrue("OCR gagal pada ${pdfFile.name}: ${ocrResult.exceptionOrNull()?.message}", ocrResult.isSuccess)

            val pages = ocrResult.getOrThrow()
            val infStart = System.currentTimeMillis()
            val metadata = DocumentMetadata(
                documentId = "bench-${pdfFile.name.hashCode()}",
                fileName = pdfFile.name,
                sourceUriString = pdfUri.toString(),
                pageRangeLabel = "Hal. 1 - ${pages.size}",
                pageCount = pages.size
            )
            val processed = preprocessor.processDocument(metadata.documentId, pages, lexicon)
            val facts = ontologyLoader.extractSemanticFacts(metadata.documentId, processed, taxonomy, spoDict)
            val inference = ruleEngine.executeForwardChaining(metadata.documentId, facts, rules)
            val (bindings, missingReq) = ontologyLoader.mapFactsToOntologyProperties(taxonomy, facts)
            val summary = summarizer.generateDocumentSummary(
                metadata = metadata,
                processedDoc = processed,
                extractedFacts = facts,
                inferenceResult = inference,
                ontologyPropertyBindings = bindings,
                missingRequiredProperties = missingReq
            )
            val nlpInfMs = System.currentTimeMillis() - infStart
            val totalMs = ocrMs + nlpInfMs

            val javaAfterMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
            val javaDeltaMb = (javaAfterMb - javaBeforeMb).coerceAtLeast(0.0)
            if (javaDeltaMb > maxJavaDeltaMb) {
                maxJavaDeltaMb = javaDeltaMb
            }
            val nativeAfterMb = Debug.getNativeHeapAllocatedSize() / (1024.0 * 1024.0)
            val memInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(memInfo)
            val totalPssMb = memInfo.totalPss / 1024.0

            assertTrue("Kesimpulan kosong untuk ${pdfFile.name}", summary.conclusionParagraph.isNotBlank())
            assertFalse("Topik dominan kosong untuk ${pdfFile.name}", summary.dominantTopic.isBlank())

            if (exportedSampleUri == null) {
                val exportRes = pdfExporter.exportSummaryToPdf(summary)
                assertTrue("Ekspor PDF gagal pada ${pdfFile.name}", exportRes.isSuccess)
                exportedSampleUri = exportRes.getOrNull()
            }

            val firedIds = inference.inferenceTraces.joinToString("|") { it.firedRuleId }
            val line = String.format(
                Locale.US,
                "\"%s\",%d,%d,%d,%d,\"%s\",%d,%d,\"%s\",%d,%d,%d,%d,%.2f,%.2f,%.2f,%.2f,%.2f",
                pdfFile.name.replace("\"", "'"),
                totalPages,
                ocrMs,
                nlpInfMs,
                totalMs,
                summary.detectedDocumentType.replace("\"", "'"),
                facts.size,
                inference.firedRulesCount,
                firedIds,
                summary.contentSummaryPoints.size,
                summary.ruleBasedKeyPoints.size,
                bindings.size,
                missingReq.size,
                javaBeforeMb,
                javaAfterMb,
                javaDeltaMb,
                nativeAfterMb,
                totalPssMb
            )
            csvLines.add(line)
            Log.i("VelaBenchmark", "BENCHMARK_ROW|$line")
        }

        if (testbedDir.exists() || testbedDir.mkdirs()) {
            File(testbedDir, "benchmark_results.csv").writeText(csvLines.joinToString("\n"), Charsets.UTF_8)
        }

        assertTrue("Kenaikan Java heap delta ($maxJavaDeltaMb MB) harus < 50 MB", maxJavaDeltaMb < 50.0)
    }

    private fun createSyntheticSamplePdf(context: Context): File {
        val file = File(context.cacheDir, "synthetic_sk_testbed.pdf")
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawColor(Color.WHITE)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        val lines = listOf(
            "UNIVERSITAS NUSANTARA PGRI KEDIRI",
            "KEPUTUSAN REKTOR NOMOR: 112/SK/UNP-KD/X/2026",
            "TENTANG PENETAPAN KALENDER AKADEMIK 2026/2027",
            "Mahasiswa wajib melakukan registrasi sebelum 31 Januari 2027.",
            "Mahasiswa tidak wajib mengikuti kegiatan tambahan yang opsional.",
            "Ditetapkan di Kediri pada tanggal 10 Oktober 2026."
        )
        lines.forEachIndexed { idx, txt ->
            canvas.drawText(txt, 40f, 80f + idx * 28f, paint)
        }
        doc.finishPage(page)
        FileOutputStream(file).use { out -> doc.writeTo(out) }
        doc.close()
        return file
    }
}
