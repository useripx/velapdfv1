package com.njagakneai.velapdf.ui.viewmodel

import com.njagakneai.velapdf.data.database.AnalysisHistoryDao
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import com.njagakneai.velapdf.data.repository.deleteAnalysesBatched
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistorySearchAndBatchDeleteTest {

    private fun sampleHistory(): List<AnalysisHistoryEntity> = listOf(
        AnalysisHistoryEntity(
            id = 1,
            documentTitle = "112_SK_Kalender_Akademik.pdf",
            documentType = "Surat Keputusan (SK)",
            dominantTopic = "Kalender Akademik & Registrasi",
            summaryText = "Batas akhir pembayaran 31 Januari 2027",
            conclusionText = "Rektor menetapkan jadwal semester genap.",
            pageRangeLabel = "Hal. 1 - 3",
            factsCount = 6,
            sourceUri = "content://doc/1",
            timestamp = 1_700_000_001L
        ),
        AnalysisHistoryEntity(
            id = 2,
            documentTitle = "Buku_IoT_ESP32.pdf",
            documentType = "Buku Ajar / Referensi Teknis",
            dominantTopic = "Mikrokontroler & MQTT",
            summaryText = "Arsitektur lapisan sensor dan gateway",
            conclusionText = "ESP32 berkomunikasi menggunakan protokol MQTT secara offline.",
            pageRangeLabel = "Hal. 23 - 35",
            factsCount = 9,
            sourceUri = "content://doc/2",
            timestamp = 1_700_000_002L
        ),
        AnalysisHistoryEntity(
            id = 3,
            documentTitle = "12_Jurnal_Model_Ontologi.pdf",
            documentType = "Artikel Jurnal Ilmiah",
            dominantTopic = "Ontology Development 101",
            summaryText = "Penerapan model ontologi pada pembelajaran",
            conclusionText = "Penelitian menggunakan metode Ontology Development 101.",
            pageRangeLabel = "Hal. 1 - 10",
            factsCount = 7,
            sourceUri = "content://doc/3",
            timestamp = 1_700_000_003L
        )
    )

    @Test
    fun `filterAnalysisHistory returns all items for blank query`() {
        val all = sampleHistory()
        assertEquals(3, filterAnalysisHistory(all, "").size)
        assertEquals(3, filterAnalysisHistory(all, "   ").size)
    }

    @Test
    fun `filterAnalysisHistory filters case-insensitively across title type topic and conclusion`() {
        val all = sampleHistory()

        val byTitle = filterAnalysisHistory(all, "iot_esp32")
        assertEquals(listOf(2), byTitle.map { it.id })

        val byType = filterAnalysisHistory(all, "Jurnal Ilmiah")
        assertEquals(listOf(3), byType.map { it.id })

        val byTopic = filterAnalysisHistory(all, "mqtt")
        assertEquals(listOf(2), byTopic.map { it.id })

        val byConclusion = filterAnalysisHistory(all, "jadwal semester genap")
        assertEquals(listOf(1), byConclusion.map { it.id })

        val byPageRange = filterAnalysisHistory(all, "23 - 35")
        assertEquals(listOf(2), byPageRange.map { it.id })

        assertTrue(filterAnalysisHistory(all, "tidak_ada_sama_sekali").isEmpty())
    }

    @Test
    fun `deleteAnalysesBatched skips empty list deduplicates ids and chunks safely over 500 items`() = runBlocking {
        val recordedBatches = mutableListOf<List<Int>>()
        val fakeDao = object : AnalysisHistoryDao {
            override suspend fun insertAnalysis(entity: AnalysisHistoryEntity): Long = entity.id.toLong()
            override fun getAllAnalyses(): Flow<List<AnalysisHistoryEntity>> = flowOf(emptyList())
            override suspend fun getAnalysisById(id: Int): AnalysisHistoryEntity? = null
            override suspend fun deleteById(id: Int) = Unit
            override suspend fun deleteByIds(ids: List<Int>) {
                recordedBatches.add(ids)
            }
            override suspend fun clearAll() = Unit
        }

        deleteAnalysesBatched(fakeDao, emptyList())
        assertTrue("List kosong tidak boleh memanggil DAO deleteByIds", recordedBatches.isEmpty())

        deleteAnalysesBatched(fakeDao, listOf(10, 20, 10, 30, 20))
        assertEquals(1, recordedBatches.size)
        assertEquals(listOf(10, 20, 30), recordedBatches.first())

        recordedBatches.clear()
        val largeIds = (1..1200).toList()
        deleteAnalysesBatched(fakeDao, largeIds, batchSize = 500)
        assertEquals(3, recordedBatches.size)
        assertEquals(listOf(500, 500, 200), recordedBatches.map { it.size })
        assertEquals(largeIds.toSet(), recordedBatches.flatten().toSet())
    }
}
