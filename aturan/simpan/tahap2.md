# Panduan Belajar & Tanya Jawab Dosen — Tahap 02 (`feature/02_evaluasi_testbed_dan_ui`)

> **STATUS DOKUMEN:** MATERI PEMBELAJARAN & PERSIAPAN BIMBINGAN / SEMPRO / SIDANG TAHAP 02  
> **MAHASISWA / PENGEMBANG:** Yogi Ario Pratama (`NPM: 2313020004` / `@useripx`)  
> **DOSEN PEMBIMBING:** Rony Heri Irawan, M.Kom. (Universitas Nusantara PGRI Kediri)  
> **PARTNER AI:** Carl (`AGT-PRD-DEV-001`)  
> **PRASYARAT:** Telah mempelajari [`tahap1.md`](tahap1.md) (Arsitektur Pipeline OCR Luring + 4-JSON + Rule-Based Inference + Room v2)  
> **TANGGAL DISUSUN:** 12 November 2026

---

## 1. Gambaran Besar: Apa yang Dicapai di Tahap 02?

Jika **Tahap 01** berfokus membangun fitur inti OCR Luring + 4-JSON + `ASK Vela` hingga terkompilasi, maka **Tahap 02** menyempurnakan **ketajaman ilmiah mesin AI**, **kegunaan UI pengguna**, dan **pembuktian empiris di perangkat Android (Emulator Offline)**:

1. **Penutupan 4 Celah Ilmiah (`Celah A, B, C, D`):**
   - **Celah A (Asal Fakta Klasifikasi):** Mendefinisikan proses eksplisit penghasil fakta `Dokumen -> mengindikasikanKonsep : <ConceptId>` serta memperluas ontologi & aturan untuk **`ArtikelJurnalIlmiah` (`RULE_14`–`RULE_16`)**.
   - **Celah B (Pelestarian Kata Negasi & Modalitas):** Mengeluarkan kata seperti `tidak`, `bukan`, `belum`, `jangan`, `tanpa`, `wajib`, `dilarang`, `harus` dari penapisan *stopword* sehingga makna `"tidak wajib"` tidak tertukar dengan `"wajib"`.
   - **Celah C (Pemetaan Eksplisit Properti Ontologi ↔ Predikat SPO):** Menghubungkan setiap fakta SPO ke pasangan eksplisit `<KelasOntologi>.<Properti>` melalui `mapFactsToOntologyProperties(...)` beserta pelacakan properti wajib yang belum terpenuhi (`missingRequiredProperties`).
   - **Celah D (Pemisahan Tegas *Document Summarization* vs *Rule-Based Key Points*):** Memisahkan tampilan dan struktur data **Ringkasan Isi Dokumen** (`contentSummaryPoints`) dengan **Poin Penting Berbasis Aturan** (`ruleBasedKeyPoints`).
2. **Manajemen Riwayat `ASK Vela`:** Pencarian teks real-time di memori ViewModel (`filterAnalysisHistory`) + mode seleksi `"Tandai"` dengan penghapusan massal aman SQLite (`AnalysisHistoryDao.deleteByIds` via `deleteAnalysesBatched(..., batchSize = 500)`) tanpa migrasi DB baru (Room tetap `v2`).
3. **Evaluasi Testbed Empiris 17 PDF Nyata (Mode Pesawat / *Offline* Total):** Diukur pada Android Emulator (`Tes_untuk_Real_Phone`, API `36`) melalui `TestbedPipelineBenchmarkTest` (`3/3` androidTest PASS, `344.329s`) menghasilkan data [`benchmark_results_tahap2.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap2.csv) & **15 tangkapan layar UI terinspeksi** (`aturan/Docs/Evidence/SS/valid/`).

---

## 2. Bedah Mendalam 4 Penyempurnaan Ilmiah (`Celah A–D`)

### 2.1 Celah A — Dari Mana Fakta `mengindikasikanKonsep` Berasal?
Pada prototipe awal, `production_rules.json` langsung menguji kondisi `mengindikasikanKonsep`, padahal 15 pola regex SPO tidak memproduksi predikat itu. Di Tahap 02, proses tersebut didefinisikan transparan:
1. `OntologyLoader.kt` memuat `lexicalClassificationConfig` dari [`document_ontology.json`](../../app/src/main/assets/document_ontology.json) (`emittedSubject = "Dokumen"`, `emittedPredicate = "mengindikasikanKonsep"`).
2. Saat menganalisis teks hasil Prapemrosesan NLP, `OntologyLoader` menghitung skor kecocokan berbasis **`lexicalKeywords`** dan penanda struktur dokumen untuk setiap konsep Ontologi (`SuratKeputusan`, `SuratEdaran`, `PedomanSOP`, `PeraturanRegulasi`, `LaporanResmi`, `ArtikelJurnalIlmiah`, `BukuAjarTeknis`, `DokumenInformatifUmum`).
3. Pemenang klasifikasi konsep leksikal tersebut **secara eksplisit memproduksi fakta baru (`ExtractedFact`)**:  
   `[Dokumen] --(mengindikasikanKonsep)--> [<ConceptId>]`.
4. Baru setelah fakta `mengindikasikanKonsep` masuk ke *Working Memory*, mesin *Forward Chaining* menembakkan aturan klasifikasi resmi (`RULE_01..04, 08, 09, 13, 14`). **Tidak ada fakta "siluman" yang muncul tiba-tiba.**

### 2.2 Celah B — Mengapa Kata Negasi (`tidak`, `bukan`, `harus`) Tidak Boleh Dibuang?
Perhatikan dua kalimat berikut:
- *"Pihak terkait **wajib** mengumpulkan laporan."* $\rightarrow$ Kewajiban positif (`memilikiKewajiban`).
- *"Pihak terkait **tidak wajib** mengumpulkan laporan."* $\rightarrow$ Pengecualian kewajiban (`tidakMemilikiKewajiban` / negasi).

Jika `"tidak"` dibuang oleh daftar *stopword*, kedua kalimat menjadi identik (*"pihak terkait wajib kumpul laporan"*). Penanganannya di Tahap 02:
- `nlp_lexicon_id.json` & [`NlpTextPreprocessor.kt`](../../app/src/main/java/com/njagakneai/velapdf/domain/nlp/NlpTextPreprocessor.kt#L37-L108) mendeklarasikan **`preservedNegationAndModalityTerms`** (`NEGATION_FUNCTION_TOKENS + MODALITY_FUNCTION_TOKENS`), menyaringnya keluar dari set `stopwords`, melestarikan token negasi/modalitas saat tokenisasi, dan menandai flag kalimat `hasNegation`.
- Pola SPO & aturan penarikan kalimat kunci memakai `negationGuards` + `isPrecededByNegation(...)` (`OntologyLoader.kt`) serta penalti/pemisahan di `FactBasedSummarizer.kt` (`L315`) agar kalimat mengandung `"tidak wajib"` **tidak** salah diekstrak sebagai kewajiban positif.

### 2.3 Celah C — Jembatan Ontologi (`nomorDokumen`) vs Predikat SPO (`memilikiNomor`)
Ontologi menamai atribut kelas dengan gaya properti data (misal `SuratKeputusan.nomorDokumen`, `ArtikelJurnalIlmiah.metodePenelitian`), sementara pola ekstraktor kalimat memakai predikat kerja SPO (`memilikiNomor`, `menggunakanMetode`). Agar keduanya nyambung secara formal:
- Setiap fakta SPO dipetakan eksplisit lewat [`OntologyLoader.mapFactsToOntologyProperties(...)`](../../app/src/main/java/com/njagakneai/velapdf/domain/ontology/OntologyLoader.kt) menggunakan konfigurasi pemetaan JSON.
- Hasilnya tampak langsung di UI pada [`07_summary_spo_ontology_rules.png`](../Docs/Evidence/SS/valid/07_summary_spo_ontology_rules.png):  
  `• [Konseptualisasi] memilikiFungsi -> menyusun domain pengetahuan... -> [BukuAjarTeknis.mekanismeKerja] (Hal. 5)`  
  beserta daftar `Pemetaan properti Ontologi terisi` dan evaluasi properti wajib yang belum ditemukan (`missingRequiredProperties`).

### 2.4 Celah D — Pemisahan *Document Summarization* vs *Rule-Based Key Points*
- **Ringkasan Isi Dokumen (*Document Summarization* — `contentSummaryPoints`):** Menyintesis **isi utuh dokumen** (pembuka/tujuan awal, pembahasan inti tengah, hingga simpulan akhir halaman) berbantuan skor TF-IDF posisi & frekuensi kata kunci domain, lengkap dengan daftar **Ringkasan Perwakilan Halaman/Bagian (`Hal. X`)**.
- **Poin Penting Berbasis Aturan (*Rule-Based Key Points* — `ruleBasedKeyPoints`):** Menampilkan poin-poin normatif/kategorikal yang disimpulkan khusus oleh aturan produksi IF–THEN (`RULE_01..RULE_16`), misalnya batas waktu kritis, klausul larangan, kelengkapan administrasi, atau identifikasi metodologi riset.
Di layar UI ([`05_summary_top_jurnal12.png`](../Docs/Evidence/SS/valid/05_summary_top_jurnal12.png) & [`06b_summary_rule_keypoints.png`](../Docs/Evidence/SS/valid/06b_summary_rule_keypoints.png)) dan hasil ekspor PDF ([`SummaryPdfExporter.kt`](../../app/src/main/java/com/njagakneai/velapdf/utils/SummaryPdfExporter.kt#L83-L115)), kedua komponen tersebut tampil di kartu dan bagian yang **terpisah secara eksplisit**.

---

## 3. Cara Membaca Hasil Uji Empiris 17 PDF (`benchmark_results_tahap2.csv`)

Berikut cara menjawab dosen bila membedah angka hasil pengujian di [`Test_Report.md`](../Docs/Test_Report.md) dan [`benchmark_results_tahap2.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap2.csv):

1. **Akurasi Dokumen Target:**
   - **5 Dokumen Formal Baseline (`SK-01..05`):** **`5/5 (100%)`** terklasifikasi akurat & menembakkan seluruh rule inti (`RULE_01..08`).
   - **3 Jurnal Ilmiah Bahasa Indonesia (`12`, `13`, `14`):** **`3/3 (100%)`** terklasifikasi `Artikel / Jurnal Ilmiah` (`RULE_14`, `RULE_10`, `RULE_16`) dengan `0` *missing required ontology properties*.
   - **Dokumen Panjang 19 Halaman (`Roadmap.pdf`):** Selesai luring dalam `44,317 ms` OCR + `240 ms` NLP/Inference.
   - **Dokumen Non-Domain (`KHS1..5` & `Transkip1..2`):** Diuji sebagai *robustness stress test* (`0` crash). Sebagian KHS (`KHS1`, `Transkip1`) masuk ke fallback `Buku Ajar` karena tabel nilai mata kuliah TI mengandung kata *"jaringan"*/*“algoritma”*/*“sistem”* — hal ini **wajar & jujur dijelaskan** karena modul parser KHS memang dihapus permanen (`ADR-001`, [`batasan_masalah.md`](../Docs/batasan_masalah.md)).
2. **Komposisi Waktu (`ocr_ms` vs `nlp_inf_ms`):**
   - Tahap *Forward Chaining + NLP + Ontologi + Summarizer* hanya memakan **`24 ms` – `494 ms` (< 0,5 detik!)**.
   - **> 99%** waktu tunggu (`2.2` – `75.9` detik) murni berada di proses render `PdfRenderer` dan pengenalan karakter citra ML Kit OCR per halaman.
3. **Transparansi Memori (Java Heap vs Native Heap vs Total PSS):**
   - **Java Heap Aplikasi VelaPDF** hanya mencapai puncak **`9.15 MB`** (kenaikan per eksekusi maks **`+4.05 MB`**, dan hanya **`+0.79 MB`** pada PDF 19 halaman `Roadmap.pdf` karena `bitmap.recycle()` per halaman). Ini jauh memenuhi target desain $\le 50\text{ MB}$.
   - **Native Heap (`88.96–151.19 MB`)** dan **Total PSS (`305.60–347.52 MB`)** adalah memori level sistem Android (runtime ART + model TFLite/C++ Google ML Kit OCR). Melaporkan **ketiganya** (bukan menyembunyikan PSS) menunjukkan kejujuran akademik yang sangat kuat di hadapan penguji.

---

## 4. Bank Pertanyaan & Jawaban Dosen — Tahap 02 (`Q19` s.d. `Q30`)

*(Melanjutkan `Q1`–`Q18` di [`tahap1.md`](tahap1.md))*

**Q19. Jelaskan secara konkret bagaimana fakta `mengindikasikanKonsep` dihasilkan! Apakah berasal dari aturan atau proses lain?**  
**Jawaban:** Bukan sulap dan bukan hasil aturan melingkar. `OntologyLoader.kt` menjalankan tahap klasifikasi leksikal-struktural menggunakan `lexicalClassificationConfig` di `document_ontology.json`. Sistem menghitung skor kecocokan tiap kelas ontologi terhadap kata kunci leksikal dan penanda seksi hasil prapemrosesan NLP, lalu memancarkan fakta dasar `[Dokumen] mengindikasikanKonsep [<ConceptId>]` ke *Working Memory*. Setelah fakta itu ada, barulah `RuleInferenceEngine` menembakkan `RULE_01` s.d. `RULE_16`.

**Q20. Apa buktinya sistem Anda tidak tertipu kalimat negasi seperti *"tidak wajib"* menjadi kewajiban positif?**  
**Jawaban:** Ada 3 lapis pengaman: (1) Kata negasi (`tidak, bukan, belum, jangan, tanpa`) dan modalitas (`wajib, dilarang, harus, dilarang keras`) dikeluarkan dari pembuangan *stopword* di `NlpTextPreprocessor.kt` via `preservedNegationAndModalityTerms`; (2) Setiap kalimat menyimpan flag `hasNegation`; (3) Pola SPO memiliki `negationGuards` yang diperiksa lewat `isPrecededByNegation(...)` di `OntologyLoader.kt` (misal *"tidak wajib"* menghasilkan pengecualian / tidak menembak kewajiban positif). Pengujian otomatisnya lulus di `NlpLexiconAndPreprocessorTest` (`3/3`) & `SpoPatternAndRuleEngineTest` (`10/10`).

**Q21. Mengapa Ontologi punya atribut `nomorDokumen`, sedangkan fakta SPO memakai `memilikiNomor`? Bukankah tidak konsisten?**  
**Jawaban:** Justru di Tahap 02 perbedaan tingkat abstraksi itu dijembatani secara eksplisit melalui `OntologyLoader.mapFactsToOntologyProperties(...)`. Relasi triple gramatikal SPO berbentuk kata kerja aktif (`Dokumen -> memilikiNomor -> X`), sedangkan properti kelas Ontologi berbentuk atribut data (`DokumenResmi.nomorDokumen`). Pemetaan eksplisit di kamus JSON menjamin setiap triple SPO diikat tepat ke atribut kelas Ontologi yang sah serta langsung menampilkan properti wajib apa yang belum ditemukan (`missingRequiredProperties`).

**Q22. Apa beda *Rule-Based Key-Point Extraction* dengan *Document Summarization* di VelaPDF? Kenapa tidak digabung saja?**  
**Jawaban:** Jika hanya memakai aturan IF–THEN, ringkasan cenderung kaku mengikuti kalimat template aturan (`summaryBulletPoint`) walaupun paragraf lain membahas topik teknis yang penting. Dengan memisahkan keduanya di Tahap 02: **Ringkasan Isi Dokumen** (`contentSummaryPoints`) menjamin keterwakilan isi seluruh bagian awal, tengah, dan akhir naskah beserta rujukan halaman (`Hal. X`), sementara **Poin Penting Berbasis Aturan** (`ruleBasedKeyPoints`) menyajikan kesimpulan normatif hasil penembakan `RULE_01..RULE_16`. Pengguna dan penguji dapat membandingkan langsung keduanya secara terpisah di layar `DocumentSummaryScreen` maupun PDF ekspor.

**Q23. Mengapa di pengujian 17 PDF pada `benchmark_results_tahap2.csv`, total waktu proses Jurnal 13 (9 hal.) sampai `76.3 detik`?**  
**Jawaban:** Karena mesin *AndroidTest* merender setiap halaman PDF menjadi bitmap beresolusi tinggi lalu mengeksekusi **pengenalan karakter optik Google ML Kit (`ocr_ms = 75,926 ms`)** di atas CPU emulator x86_64 tanpa NPU kamera khusus, supaya pengujian 100% adil mensimulasikan dokumen scan/citra. Sementara waktu **mesin AI NLP + Ontologi + Forward Chaining (`nlp_inf_ms`) hanya `470 ms` (< 0,5 detik)**! Pemisahan dua kolom metrik ini penting agar dosen melihat bahwa algoritma *Rule-Based Inference* sangat cepat, sedangkan latensi panjang berasal dari ML Kit OCR per halaman.

**Q24. Di tabel benchmark, memori `Total PSS` mencapai `305–347 MB`, sedangkan di proposal tertulis batas `< 50 MB`. Apakah gagal?**  
**Jawaban:** Tidak gagal, keduanya adalah lapisan memori yang berbeda: angka `<= 50 MB` mengacu pada **Java Heap aplikasi VelaPDF** (objek teks, struktur ontologi, triple SPO, dan mesin inferensi), yang hasil ukurnya hanya **`3.34 – 9.15 MB`** (lonjakan delta maksimal **`+4.05 MB`**, bahkan **`+0.79 MB`** pada PDF 19 halaman `Roadmap.pdf`!). Sementara `Native Heap` (`88.96–151.19 MB`) dan `Total PSS` (`305–347 MB`) adalah gabungan memory-mapped framework Android ART + model pengenal huruf C++/TFLite Google ML Kit + driver grafis emulator. Di Bab IV Skripsi, ketiganya dilaporkan terbuka agar transparan.

**Q25. Mengapa berkas `KHS1.pdf..KHS5.pdf` yang sudah dihapus fiturnya tetap diikutkan dalam uji benchmark, dan kok terklasifikasi sebagai `Buku Ajar`?**  
**Jawaban:** Karena Anda (peneliti) menambahkan dokumen tersebut ke folder sampel uji untuk menguji ketangguhan (*stress test*) terhadap input di luar domain target (*out-of-domain tabular documents*). Hasilnya membuktikan sistem **aman tanpa force-close/crash**. Klasifikasi `KHS1`/`Transkip1` jatuh ke `Buku Ajar` karena di daftar mata kuliah Teknik Informatika tercantum kata kunci akademik seperti *"jaringan"*, *"algoritma"*, *"sistem"* tanpa ciri surat resmi SK/SE. Sistem berperilaku sesuai batasan masalah (`ADR-001`, tidak ada lagi parser KHS).

**Q26. Di screenshot `SK-01` (`09_summary_sk01_formal.png`), kenapa `Pemadatan Teks` tertulis `0.0% (152 -> 182 kata)`?**  
**Jawaban:** Temuan jujur tersebut sudah kami catat di `Defect_Log.md` sebagai observasi `[OBS-NLP-001]` untuk diperbaiki di **Tahap 03**. Penyebabnya: `SK-01` adalah surat pendek 1 halaman yang teks utuhnya hanya `152` kata, sedangkan kalimat penyambung naratif (*"Ringkasan isi naskah mengidentifikasi... Pada bagian awal pembahasan..."*) menambah ~30 kata penghubung sehingga total kata narasi (`182`) lebih banyak daripada surat mentahnya, memicu rasio pemadatan `0.0%`. Pada jurnal 10 halaman (`2179 -> 298 kata`), pemadatan bekerja sangat efektif (**`86.3%`**).

**Q27. Bagaimana fitur *"Tandai & Hapus Banyak"* pada Riwayat `ASK Vela` dijamin tidak membuat aplikasi crash `SQLiteException`?**  
**Jawaban:** SQLite pada Android membatasi jumlah parameter `IN (:ids)` dalam satu query SQL (umumnya maks `999`). Oleh sebab itu, `DocumentAnalysisRepository.deleteSavedAnalyses` memanggil fungsi `deleteAnalysesBatched(dao, ids, batchSize = 500)` yang menyaring duplikat (`.distinct()`) lalu memecahnya (`.chunked(500)`) sebelum dikirim ke `AnalysisHistoryDao.deleteByIds(...)`. Jadi meski ada ribuan riwayat yang dipilih lewat `"Pilih Semua"`, SQLite tetap aman 100%, tanpa perlu menaikkan versi database Room (tetap `v2`).

**Q28. Kenapa fitur pencarian riwayat (`Cari Riwayat`) dilakukan di Kotlin ViewModel (`filterAnalysisHistory`), bukan `SELECT ... LIKE` ke SQLite Room?**  
**Jawaban:** Karena setiap kartu ringkasan rata-rata berjumlah puluhan hingga ratusan entri ringan dengan `Flow<List<AnalysisHistoryEntity>>` yang sudah reaktif di memori. Melakukan filter murni (`filterAnalysisHistory`) pada StateFlow menghemat I/O baca-tulis disk berulang tiap pengguna mengetik satu huruf, sekaligus langsung bisa diuji cepat di unit test JVM (`HistorySearchAndBatchDeleteTest`, `3/3 PASS`) tanpa emulator.

**Q29. Bagaimana bukti bahwa uji emulator 17 PDF dan uji UI di screenshot benar-benar berjalan offline tanpa menyentuh internet?**  
**Jawaban:** Pertama, di setiap tangkapan layar (`01` s.d. `14` pada [`aturan/Docs/Evidence/SS/valid/`](../Docs/Evidence/SS/valid)), ikon **Mode Pesawat (Airplane Mode)** aktif terlihat nyata di bilah status kanan atas. Kedua, dalam *AndroidTest* `TestbedPipelineBenchmarkTest`, emulator dijalankan dengan koneksi `Wi-Fi` & `Cellular Data` dimatikan total (`Airplane Mode ON`), dan aplikasi `VelaPDF` tidak memakai SDK Cloud LLM / endpoint API inferensi eksternal.

**Q30. Apa kelemahan yang masih terlihat di Tahap 02 dan apa target Tahap 03 selanjutnya?**  
**Jawaban:** Dari inspeksi visual emulator, ditemukan dua hal non-kritis untuk dipoles di Tahap 03: (1) beberapa warna ikon di kotak gelap kartu `AskVelaScreen` serta kontras teks di pill `"Pemadatan Teks"` agak rendah (`[OBS-UI-001]`), dan (2) penyempurnaan strategi pemadatan agar surat pendek `< 200` kata tetap terkompresi ringkas (`[OBS-NLP-001]`). Selain itu, **pengujian fisik langsung kamera belakang pada HP Infinix Note 30** wajib dilakukan sesuai [`Checklist_Uji_HP_Infinix_Note30.md`](../Docs/Checklist_Uji_HP_Infinix_Note30.md) karena emulator tidak memiliki lensa kamera optik nyata dengan pencahayaan ruangan sebenarnya.
