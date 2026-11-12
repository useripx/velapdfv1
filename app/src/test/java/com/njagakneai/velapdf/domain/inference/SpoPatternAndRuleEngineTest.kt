package com.njagakneai.velapdf.domain.inference

import com.njagakneai.velapdf.domain.model.OcrPageText
import com.njagakneai.velapdf.domain.nlp.NlpTextPreprocessor
import com.njagakneai.velapdf.domain.ontology.OntologyLoader
import com.njagakneai.velapdf.testutil.TestAssetProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpoPatternAndRuleEngineTest {

    private lateinit var preprocessor: NlpTextPreprocessor
    private lateinit var ontologyLoader: OntologyLoader
    private lateinit var ruleEngine: RuleInferenceEngine

    @Before
    fun setup() {
        preprocessor = NlpTextPreprocessor(TestAssetProvider)
        ontologyLoader = OntologyLoader(TestAssetProvider)
        ruleEngine = RuleInferenceEngine(TestAssetProvider)
    }

    @Test
    fun `SK-01 Surat Keputusan Rektor fires RULE_01 and RULE_07`() = runBlocking {
        val text = """
            UNIVERSITAS NUSANTARA PGRI KEDIRI
            KEPUTUSAN REKTOR UNIVERSITAS NUSANTARA PGRI KEDIRI
            Nomor: 112/SK/UNP-KD/X/2026
            TENTANG PENETAPAN KALENDER AKADEMIK DAN BATAS AKHIR STUDI TAHUN AJARAN 2026/2027
            REKTOR UNIVERSITAS NUSANTARA PGRI KEDIRI,
            Menimbang: a. bahwa demi menjamin kelancaran tata kelola akademik dan ketertiban administrasi pembelajaran;
            Mengingat: 1. Undang-Undang Nomor 12 Tahun 2012 tentang Pendidikan Tinggi;
            MEMUTUSKAN:
            Menetapkan: SURAT KEPUTUSAN REKTOR TENTANG KALENDER AKADEMIK.
            KESATU: Perkuliahan Semester Genap Tahun Akademik 2026/2027 resmi dimulai pada tanggal 15 Februari 2027.
            KEDUA: Batas akhir registrasi administratif dan pembayaran biaya kuliah ditetapkan paling lambat tanggal 31 Januari 2027.
            KEEMPAT: Keputusan ini mulai berlaku sejak tanggal ditetapkan.
            Ditetapkan di Kediri pada tanggal 10 Oktober 2026
            Rektor Universitas Nusantara PGRI Kediri, Dr. H. Zainal Afandi, M.Pd.
        """.trimIndent()

        val (facts, firedRuleIds) = analyzeText("sk-01", listOf(OcrPageText(1, text)))

        assertTrue("Nomor SK harus terekstrak", facts.any { it.predicate == "memilikiNomor" && it.objectValue.contains("112/SK") })
        assertTrue("SK-01 harus menembakkan RULE_01 (fired=$firedRuleIds)", "RULE_01" in firedRuleIds)
        assertTrue("SK-01 harus menembakkan RULE_07 (fired=$firedRuleIds)", "RULE_07" in firedRuleIds)
    }

    @Test
    fun `SK-02 Surat Edaran Jadwal KRS fires RULE_02 RULE_05 and RULE_07`() = runBlocking {
        val text = """
            UNIVERSITAS NUSANTARA PGRI KEDIRI
            SURAT EDARAN RESMI
            Nomor: 048/SE/UNP-KD/I/2026
            Perihal: Pelaksanaan Pemrograman Kartu Rencana Studi (KRS) Semester Genap
            Mahasiswa wajib berkonsultasi dan mendapatkan persetujuan Dosen Pembimbing Akademik sebelum melakukan penguncian KRS.
            Tenggat waktu pengisian dan finalisasi KRS ditutup paling lambat pada tanggal 25 Januari 2026 pukul 23.59 WIB.
            Peringatan penting: Sistem tidak melayani pengisian KRS susulan setelah batas waktu penutupan.
            Kediri, 08 Januari 2026
            Wakil Rektor I Bidang Akademik, Dr. Suryo Widodo, M.Pd.
        """.trimIndent()

        val (facts, firedRuleIds) = analyzeText("sk-02", listOf(OcrPageText(1, text)))

        assertTrue("Harus ada fakta kewajiban", facts.any { it.predicate == "memilikiKewajiban" })
        assertTrue("Harus ada fakta batas waktu", facts.any { it.predicate == "berakhirPada" })
        assertTrue("SK-02 harus menembakkan RULE_02 (fired=$firedRuleIds)", "RULE_02" in firedRuleIds)
        assertTrue("SK-02 harus menembakkan RULE_05 (fired=$firedRuleIds)", "RULE_05" in firedRuleIds)
        assertTrue("SK-02 harus menembakkan RULE_07 (fired=$firedRuleIds)", "RULE_07" in firedRuleIds)
    }

    @Test
    fun `SK-03 Pedoman SOP Ujian Skripsi fires RULE_03 and RULE_05`() = runBlocking {
        val text = """
            FAKULTAS TEKNIK - UNIVERSITAS NUSANTARA PGRI KEDIRI
            STANDAR OPERASIONAL PROSEDUR (SOP) PELAKSANAAN UJIAN SKRIPSI
            Nomor Dokumen: SOP/FT-TI/2026/03
            Tanggal Berlaku: 15 September 2026
            1. TUJUAN DAN RUANG LINGKUP
            Pedoman ini mengatur tata cara baku pendaftaran, verifikasi berkas, dan pelaksanaan sidang ujian skripsi.
            a. Mahasiswa telah menuntaskan beban minimal 138 SKS dengan IPK minimal 2.50.
            d. Mahasiswa wajib menyerahkan naskah revisi final paling lambat 14 hari kerja kalender setelah ujian diselenggarakan.
            Disahkan oleh Ketua Program Studi Teknik Informatika, Rony Heri Irawan, M.Kom.
        """.trimIndent()

        val (facts, firedRuleIds) = analyzeText("sk-03", listOf(OcrPageText(1, text)))

        assertTrue("Harus ada fakta tenggat revisi", facts.any { it.predicate == "berakhirPada" })
        assertTrue("SK-03 harus menembakkan RULE_03 (fired=$firedRuleIds)", "RULE_03" in firedRuleIds)
        assertTrue("SK-03 harus menembakkan RULE_05 (fired=$firedRuleIds)", "RULE_05" in firedRuleIds)
    }

    @Test
    fun `SK-04 Peraturan Tata Tertib Kampus fires RULE_04 and RULE_06`() = runBlocking {
        val text = """
            UNIVERSITAS NUSANTARA PGRI KEDIRI
            PERATURAN REKTOR TENTANG KODE ETIK DAN TATA TERTIB MAHASISWA
            Nomor: 05 TAHUN 2026
            Tanggal Ditetapkan: 02 Februari 2026
            Pasal 1: Setiap mahasiswa wajib menjunjung tinggi etika akademik, integritas moral, dan ketertiban lingkungan kampus.
            Pasal 4: Mahasiswa berhak menggunakan fasilitas pembelajaran dan perpustakaan sesuai regulasi yang berlaku.
            Pasal 8: Mahasiswa dilarang keras melakukan tindakan plagiarisme, perjokian tugas, pemalsuan tanda tangan, dan perjudian.
            Ditetapkan di Kediri pada tanggal 02 Februari 2026
            Rektor Universitas Nusantara PGRI Kediri
        """.trimIndent()

        val (facts, firedRuleIds) = analyzeText("sk-04", listOf(OcrPageText(1, text)))

        assertTrue("Harus ada fakta larangan", facts.any { it.predicate == "memilikiLarangan" })
        assertTrue("SK-04 harus menembakkan RULE_04 (fired=$firedRuleIds)", "RULE_04" in firedRuleIds)
        assertTrue("SK-04 harus menembakkan RULE_06 (fired=$firedRuleIds)", "RULE_06" in firedRuleIds)
    }

    @Test
    fun `SK-05 Laporan Ringkasan Kegiatan fires RULE_08 and RULE_07`() = runBlocking {
        val text = """
            LEMBAGA PENELITIAN DAN PENGABDIAN KEPADA MASYARAKAT (LPPM)
            UNIVERSITAS NUSANTARA PGRI KEDIRI
            LAPORAN RINGKASAN EKSEKUTIF PELAKSANAAN KKN TEMATIK DIGITALISASI DESA
            Nomor Dokumen: 215/LPPM/UNP-KD/2026
            Tanggal Dokumen: 28 Oktober 2026
            1. RINGKASAN EKSEKUTIF
            Program Kuliah Kerja Nyata (KKN) Tematik Periode Semester Ganjil 2026 telah selesai diselenggarakan dengan melibatkan 450 mahasiswa di 15 desa binaan Kabupaten Kediri.
            3. REKOMENDASI TINDAK LANJUT
            LPPM merekomendasikan perlunya pendampingan teknis berkala setiap 3 bulan.
            Ketua Lembaga Penelitian dan Pengabdian kepada Masyarakat (LPPM)
        """.trimIndent()

        val (facts, firedRuleIds) = analyzeText("sk-05", listOf(OcrPageText(1, text)))

        assertTrue("Nomor laporan harus terekstrak", facts.any { it.predicate == "memilikiNomor" })
        assertTrue("SK-05 harus menembakkan RULE_08 (fired=$firedRuleIds)", "RULE_08" in firedRuleIds)
        assertTrue("SK-05 harus menembakkan RULE_07 (fired=$firedRuleIds)", "RULE_07" in firedRuleIds)
    }

    @Test
    fun `Buku IoT Bahasa Indonesia halaman 23 sampai 35 triggers BukuAjarTeknis and technical SPO facts`() = runBlocking {
        val pages = listOf(
            OcrPageText(
                pageNumber = 23,
                rawText = """
                    BAB III KONSEP DASAR INTERNET OF THINGS (IoT)
                    Internet of Things adalah jaringan perangkat fisik yang tertanam sensor, perangkat lunak, dan konektivitas untuk bertukar data.
                    Arsitektur sistem IoT terdiri dari lapisan persepsi sensor, lapisan jaringan gateway, dan lapisan aplikasi cloud.
                """.trimIndent()
            ),
            OcrPageText(
                pageNumber = 28,
                rawText = """
                    Mikrokontroler ESP32 berfungsi sebagai pengendali utama yang membaca data analog dari sensor suhu DHT22.
                    Perangkat gateway mengirimkan paket telemetri melalui protokol komunikasi MQTT menuju broker server.
                """.trimIndent()
            ),
            OcrPageText(
                pageNumber = 35,
                rawText = """
                    Sistem monitoring pertanian pintar bertujuan untuk meningkatkan efisiensi irigasi lahan secara otomatis.
                    Node sensor wajib dikalibrasi secara berkala agar pembacaan kelembaban tanah tetap akurat.
                """.trimIndent()
            )
        )

        val (facts, firedRuleIds) = analyzeText("buku-iot", pages)

        assertTrue(
            "Konsep BukuAjarTeknis harus terdeteksi (facts=${facts.map { it.predicate + ':' + it.objectValue }})",
            facts.any { it.predicate == "mengindikasikanKonsep" && it.objectValue == "BukuAjarTeknis" }
        )
        assertTrue("Harus ada definisi teknis", facts.any { it.predicate == "memilikiDefinisi" })
        assertTrue("Harus ada komponen/peran teknis", facts.any { it.predicate == "memilikiKomponen" || it.predicate == "berfungsiSebagai" })
        assertTrue("RULE_09 Buku Ajar harus menyala (fired=$firedRuleIds)", "RULE_09" in firedRuleIds)
        assertTrue("RULE_10 Definisi Konsep harus menyala (fired=$firedRuleIds)", "RULE_10" in firedRuleIds)
        assertTrue("RULE_11 atau RULE_12 teknis harus menyala (fired=$firedRuleIds)", "RULE_11" in firedRuleIds || "RULE_12" in firedRuleIds)
    }

    @Test
    fun `Celah A explicitly defines mengindikasikanKonsep in Ontology and provenance metadata`() = runBlocking {
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        assertTrue(
            "Relasi mengindikasikanKonsep harus terdaftar di Ontologi",
            "mengindikasikanKonsep" in taxonomy.relations
        )

        val (facts, firedRuleIds) = analyzeText(
            "celah-a",
            listOf(OcrPageText(1, "SURAT EDARAN Nomor: 01/SE/2026 tentang jadwal kegiatan akademik."))
        )
        val conceptFact = facts.first { it.predicate == "mengindikasikanKonsep" }
        assertTrue(
            "Sumber fakta konsep harus eksplisit (actual=${conceptFact.sourceMethod})",
            conceptFact.sourceMethod == "ONTOLOGY_LEXICAL_CLASSIFICATION"
        )
        assertTrue("RULE_02 wajib menyala", "RULE_02" in firedRuleIds)
    }

    @Test
    fun `Celah B differentiates wajib vs tidak wajib and avoids false positive larangan`() = runBlocking {
        val positiveText = "Pihak terkait wajib mengumpulkan laporan bulanan paling lambat tanggal 20 November 2026."
        val negativeText = "Pihak terkait tidak wajib mengumpulkan laporan tambahan karena sudah terintegrasi."
        val negatedBanText = "Mahasiswa tidak dilarang mengikuti seminar nasional di luar jam kuliah."

        val (posFacts, posRules) = analyzeText("celah-b-pos", listOf(OcrPageText(1, positiveText)))
        val (negFacts, negRules) = analyzeText("celah-b-neg", listOf(OcrPageText(1, negativeText)))
        val (banNegFacts, _) = analyzeText("celah-b-ban", listOf(OcrPageText(1, negatedBanText)))

        assertTrue("Kalimat wajib harus mengekstrak memilikiKewajiban", posFacts.any { it.predicate == "memilikiKewajiban" })
        assertTrue("RULE_05 harus menyala pada kewajiban positif", "RULE_05" in posRules)

        assertTrue(
            "Kalimat 'tidak wajib' DILARANG menghasilkan memilikiKewajiban (facts=$negFacts)",
            negFacts.none { it.predicate == "memilikiKewajiban" }
        )
        assertTrue(
            "Kalimat 'tidak wajib' harus mengekstrak tidakMemilikiKewajiban (facts=$negFacts)",
            negFacts.any { it.predicate == "tidakMemilikiKewajiban" }
        )
        assertTrue("RULE_15 pengecualian wajib menyala (fired=$negRules)", "RULE_15" in negRules)

        assertTrue(
            "Kalimat 'tidak dilarang' DILARANG memicu memilikiLarangan (facts=$banNegFacts)",
            banNegFacts.none { it.predicate == "memilikiLarangan" }
        )
    }

    @Test
    fun `Celah C maps SPO predicates to explicit Ontology properties and detects missing fields`() = runBlocking {
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val lexicon = preprocessor.loadLexiconFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()

        val fullSkPages = listOf(
            OcrPageText(
                1,
                """
                    KEPUTUSAN REKTOR UNIVERSITAS NUSANTARA PGRI KEDIRI
                    Nomor: 112/SK/UNP-KD/X/2026
                    Ditetapkan di Kediri pada tanggal 10 Oktober 2026
                    Rektor Universitas Nusantara PGRI Kediri
                """.trimIndent()
            )
        )
        val processedFull = preprocessor.processDocument("celah-c-full", fullSkPages, lexicon)
        val factsFull = ontologyLoader.extractSemanticFacts("celah-c-full", processedFull, taxonomy, spoDict)
        val (bindingsFull, missingFull) = ontologyLoader.mapFactsToOntologyProperties(taxonomy, factsFull)

        assertTrue(
            "memilikiNomor harus terpetakan ke properti nomorDokumen",
            bindingsFull.any { it.spoPredicate == "memilikiNomor" && it.propertyName == "nomorDokumen" }
        )
        assertTrue(
            "diterbitkanPadaTanggal harus terpetakan ke properti tanggalTerbit",
            bindingsFull.any { it.spoPredicate == "diterbitkanPadaTanggal" && it.propertyName == "tanggalTerbit" }
        )
        assertTrue(
            "SK lengkap tidak boleh kehilangan nomorDokumen/tanggalTerbit (missing=$missingFull)",
            missingFull.none { it.endsWith(".nomorDokumen") || it.endsWith(".tanggalTerbit") }
        )

        val partialSkPages = listOf(OcrPageText(1, "KEPUTUSAN REKTOR tentang pedoman umum tanpa tanggal dan nomor."))
        val processedPartial = preprocessor.processDocument("celah-c-partial", partialSkPages, lexicon)
        val factsPartial = ontologyLoader.extractSemanticFacts("celah-c-partial", processedPartial, taxonomy, spoDict)
        val (_, missingPartial) = ontologyLoader.mapFactsToOntologyProperties(taxonomy, factsPartial)

        assertTrue(
            "SK tanpa nomor harus melaporkan properti wajib nomorDokumen belum terisi",
            missingPartial.any { it.contains("nomorDokumen") }
        )
    }

    @Test
    fun `Artikel Jurnal Ilmiah sampel 12 13 14 classifies as ArtikelJurnalIlmiah even with distractor words`() = runBlocking {
        val paper12Pages = listOf(
            OcrPageText(
                pageNumber = 1,
                rawText = """
                    Jurnal Teknik Informatika Vol. 5 No. 2 ISSN: 2460-1234
                    PENERAPAN MODEL ONTOLOGI DALAM PERKEMBANGAN GAME DIGITAL
                    Abstract: Ontology model for digital game domain classification.
                    1. PENDAHULUAN
                    Dokumen ini bukan laporan keuangan dan bukan pedoman administrasi.
                    2. METODE PENELITIAN
                    Metode penelitian yang digunakan adalah Ontology Development 101 untuk menyusun kelas dan relasi konsep.
                    5. KESIMPULAN
                    Berdasarkan hasil penelitian, model ontologi berhasil memetakan domain game digital secara konsisten.
                    DAFTAR PUSTAKA
                """.trimIndent()
            )
        )

        val (facts12, fired12) = analyzeText("jurnal-12", paper12Pages)

        assertTrue(
            "Makalah jurnal dengan distractor 'laporan/pedoman' tetap harus ArtikelJurnalIlmiah (facts=${facts12.filter { it.predicate == "mengindikasikanKonsep" }})",
            facts12.any { it.predicate == "mengindikasikanKonsep" && it.objectValue == "ArtikelJurnalIlmiah" }
        )
        assertTrue("Fakta metode penelitian harus terekstrak", facts12.any { it.predicate == "menggunakanMetode" })
        assertTrue("RULE_14 Artikel Jurnal harus menyala (fired=$fired12)", "RULE_14" in fired12)
        assertTrue("RULE_16 Metodologi Riset harus menyala (fired=$fired12)", "RULE_16" in fired12)
    }

    private suspend fun analyzeText(
        docId: String,
        pages: List<OcrPageText>
    ): Pair<List<com.njagakneai.velapdf.domain.model.ExtractedFact>, List<String>> {
        val lexicon = preprocessor.loadLexiconFromAsset()
        val taxonomy = ontologyLoader.loadOntologyFromAsset()
        val spoDict = ontologyLoader.loadSpoDictionaryFromAsset()
        val rules = ruleEngine.loadRulesFromAsset()

        val processed = preprocessor.processDocument(docId, pages, lexicon)
        val facts = ontologyLoader.extractSemanticFacts(docId, processed, taxonomy, spoDict)
        val inference = ruleEngine.executeForwardChaining(docId, facts, rules)
        val firedIds = inference.inferenceTraces.map { it.firedRuleId }
        return facts to firedIds
    }
}
