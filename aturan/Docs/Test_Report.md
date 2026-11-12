# Laporan Hasil Pengujian (Test Report) — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** LAPORAN VERIFIKASI TAHAP 03 + HOTFIX UI (`v1.5.0` — `versionCode = 5`)  
> **DESIGNATED APPROVER:** Yogi Ario (`@useripx` — rujuk [config/organization.md](../config/organization.md))  
> **PENANGGUNG JAWAB QA & PENGUJIAN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL EKSEKUSI VERIFIKASI AKHIR:** 12 November 2026  
> **ACUAN METODOLOGI & TESTBED:** [Standards/Testing_Strategy.md](Standards/Testing_Strategy.md), [Standards/QA_Framework.md](Standards/QA_Framework.md), & [alatuji/](../alatuji/README.md)

---

## 1. Ringkasan Eksekutif & Status Gerbang Kualitas (Tahap 03)

Seluruh pengujian otomatis **JVM Unit Test (`26/26` PASS)**, **Android Lint (`0` error)**, **Build APK Debug & AndroidTest (`BUILD SUCCESSFUL`)**, **Android Instrumentation Benchmark Empiris 17 PDF nyata pasca-Paket 1B Hibrida `iText7` + Fallback ML Kit (`3/3` PASS dalam `29.467s` — ~11.7x lebih cepat dari Tahap 02)**, serta **Verifikasi Visual UI Tahap 03 (`7` tangkapan layar terinspeksi)** telah diselesaikan dengan **100% PASS Gate (0 kegagalan, 0 *crash*)** di Emulator Offline. *(Menunggu eksekusi UAT kamera fisik pada HP Infinix Note 30 sebelum rilis `v1.5.0`.)*

### Tabel Riwayat Eksekusi Gerbang Mutu Lintas Tahap

| Tahap | Branch | Lingkup Uji | Perintah Eksekusi | Hasil Teramati |
| :---: | :--- | :--- | :--- | :--- |
| **Tahap 00** | `develop` | Smoke test + Lint fix (`DEF-LEG-009`) | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` | `BUILD SUCCESSFUL` (`3m 17s`), `1/1` test, `0` lint error (`46` warn, `1` hint) |
| **Tahap 01** | `feature/01_ocr_dan_ask_vela` | 4-JSON AI Pipeline (`SK-01..05` + IoT) | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` | `BUILD SUCCESSFUL` (`1m 53s`), `11/11` test, `0` lint error (`48` warn, `1` hint) |
| **Tahap 02** | `feature/02_evaluasi_testbed_dan_ui` (`v1.4.0`) | Celah `A–D`, Search/Mark/Batch Delete, Testbed 17 PDF & UI Emulator + UAT HP | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest --no-daemon` + `am instrument` | **`BUILD SUCCESSFUL`**, **`20/20` JVM PASS**, **`0` lint error**, **`3/3` androidTest PASS (`344.329s`)**, **17/17 PDF luring**, **15 PNG UI** |
| **Tahap 03** | `feature/03_uji_perangkat_fisik_dan_polish` | UI Polish (`[OBS-UI-001]`), Kompresi Pendek (`[OBS-NLP-001]`), & Hybrid `iText7` + ML Kit (`Paket 1B`) | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest --console=plain` + `am instrument` | **`BUILD SUCCESSFUL` (`2m 4s`)**, **`26/26` JVM PASS**, **`0` lint error** (`47` warn, `1` hint), **`3/3` androidTest PASS (`29.467s`)**, **17/17 PDF luring**, **7 PNG UI Tahap 3** |
| **Hotfix + Rilis `v1.5.0`** | `fix/pdf-to-image-overlap-and-history-back` -> `main` (`v1.5.0`) | Fix scroll/overlap `PdfToImageScreen`, tombol kembali ke Beranda `HistoryScreen`, & bump `versionCode = 5` | `./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease --console=plain` | **`BUILD SUCCESSFUL` (`11m 51s`)**, **`26/26` JVM PASS**, **`0` lint error**, **APK Debug & Release siap**, **3 PNG UI Hotfix**, UAT HP Infinix Note 30 **OK** |

---

## 2. Rincian Pengujian Unit JVM (`app/src/test/`) — `26 / 26 PASS`

Berkas XML hasil uji pada `app/build/test-results/testDebugUnitTest/`:

| Suite Test (`com.njagakneai.velapdf...`) | Jumlah Test | Lulus | Gagal / Skip | Fokus Cakupan Terverifikasi |
| :--- | :---: | :---: | :---: | :--- |
| [`NlpLexiconAndPreprocessorTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/nlp/NlpLexiconAndPreprocessorTest.kt) | 3 | **3** | 0 / 0 | Pemuatan `nlp_lexicon_id.json`, proteksi singkatan baku, penghapusan stopword biasa sekaligus **pelestarian kata negasi/modalitas (`tidak`, `bukan`, `belum`, `wajib`, `dilarang`, `harus`)** *(Celah B)*. |
| [`SpoPatternAndRuleEngineTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/inference/SpoPatternAndRuleEngineTest.kt) | 10 | **10** | 0 / 0 | Asal fakta `mengindikasikanKonsep` *(Celah A)*, pencegahan false-positive `"tidak wajib"` vs `"wajib"` *(Celah B)*, pemetaan eksplisit properti Ontologi–SPO `ConceptId.propertyName` *(Celah C)*, evaluasi `SK-01..SK-05`, Buku IoT Hal. 23–35, serta klasifikasi `ArtikelJurnalIlmiah` (`RULE_14`–`RULE_16`). |
| [`TextExtractionCleanerAndPdfExtractorTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/nlp/TextExtractionCleanerAndPdfExtractorTest.kt) | 5 | **5** | 0 / 0 | Penyambungan kata terpenggal tanda hubung (`-\n`) dengan pelestarian kata ulang (`undang-undang`), deteksi teks PDF native vs scan rusak, pengurutan blok spasial dua kolom, dan uji ekstraksi `iText7` atas PDF nyata testbed. |
| [`FactBasedSummarizerTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizerTest.kt) | 4 | **4** | 0 / 0 | Pemisahan tegas *Document Summarization* vs *Rule-Based Key Points* *(Celah D)* serta **kompresi adaptif dokumen pendek `< 220` kata (`[OBS-NLP-001]`)**. |
| [`HistorySearchAndBatchDeleteTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/ui/viewmodel/HistorySearchAndBatchDeleteTest.kt) | 3 | **3** | 0 / 0 | Filter pencarian riwayat (*case-insensitive* lintas judul/topik/jenis/isi), batas ukuran riwayat kosong, serta penghapusan massal *chunked* `batchSize = 500` agar tetap aman di bawah batas bind SQLite `999`. |
| [`ExampleUnitTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/ExampleUnitTest.kt) | 1 | **1** | 0 / 0 | Baseline JVM test runner. |
| **TOTAL** | **26** | **26** | **0 / 0** | **100% PASS** |

---

## 3. Pengujian Instrumentasi & Benchmark Empiris 17 PDF Offline (`androidTest`)

- **Lingkungan Isolasi:** Android Emulator AVD **`Tes_untuk_Real_Phone`** (`emulator-5554`, API Level `36`), **Airplane Mode ON** (`Wi-Fi` & `Mobile Data` dinonaktifkan penuh = `0 byte` transmisi jaringan).
- **Kelas Uji:** [`TestbedPipelineBenchmarkTest.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/androidTest/java/com/njagakneai/velapdf/TestbedPipelineBenchmarkTest.kt) via `am instrument -w com.njagakneai.velapdf.test/androidx.test.runner.AndroidJUnitRunner`.
- **Hasil Run Instrumentasi:** **`OK (3 tests)`**, total waktu **`344.329` detik**.
- **Data Mentah CSV:** [`benchmark_results_tahap2.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap2.csv) (`17` dokumen uji).

### 3.1 Tabel Hasil Empiris Seluruh 17 PDF (`PdfRenderer` + ML Kit OCR + Domain NLP/Ontologi/Rules + Summarizer)

| No | Nama Berkas (`4_sampel_dokumen_uji`) | Hal | OCR (ms) | NLP+Inf (ms) | Total (ms) | Klasifikasi Terdeteksi | Fakta SPO | Rules Fired | Content Sum / Rule KP | Onto Bind (Miss) | Java Heap B→A (Δ MB) | Native (MB) | PSS (MB) |
| :-: | :--- | :-: | --: | --: | --: | :--- | :-: | :--- | :-: | :-: | --: | --: | --: |
| 1 | `SK-01_Surat_Keputusan_Rektor.pdf` | 1 | 2921 | 30 | 2951 | `Surat Keputusan (SK)` | 17 | `RULE_01\|RULE_07` | 6 / 4 | 6 (0) | 5.30→6.91 (+1.61) | 124.85 | 325.63 |
| 2 | `SK-02_Surat_Edaran_Jadwal_KRS.pdf` | 1 | 2421 | 24 | 2445 | `Surat Edaran (SE)` | 8 | `RULE_02\|RULE_05\|RULE_07` | 3 / 5 | 7 (0) | 5.10→6.42 (+1.31) | 124.37 | 324.36 |
| 3 | `SK-03_Pedoman_SOP_Ujian_Skripsi.pdf` | 1 | 2773 | 45 | 2818 | `Pedoman SOP` | 7 | `RULE_03\|RULE_05\|RULE_07` | 6 / 5 | 6 (1) | 5.09→6.67 (+1.57) | 125.34 | 324.87 |
| 4 | `SK-04_Peraturan_Tata_Tertib_Kampus.pdf` | 1 | 2218 | 28 | 2246 | `Peraturan & Regulasi` | 9 | `RULE_04\|RULE_06\|RULE_07` | 6 / 6 | 7 (0) | 5.11→6.49 (+1.38) | 124.90 | 323.98 |
| 5 | `SK-05_Laporan_Ringkasan_Kegiatan.pdf` | 1 | 3373 | 24 | 3397 | `Laporan Resmi` | 5 | `RULE_08\|RULE_07` | 6 / 2 | 5 (1) | 5.10→6.48 (+1.38) | 124.77 | 324.41 |
| 6 | `12 Penerapan Model Ontologi...pdf` | 10 | 61795 | 494 | 62289 | `Artikel / Jurnal Ilmiah` | 59 | `RULE_14\|RULE_10\|RULE_11\|RULE_16` | 6 / 4 | 6 (0) | 3.34→4.48 (+1.13) | 88.96 | 305.60 |
| 7 | `13 Konsep Ontologi pada Manajemen RPL...pdf` | 9 | 75926 | 470 | 76396 | `Artikel / Jurnal Ilmiah` | 47 | `RULE_14\|RULE_10\|RULE_16` | 6 / 3 | 6 (0) | 5.08→7.17 (+2.09) | 130.55 | 335.06 |
| 8 | `14 Pengklasifikasian Dokumen Teks...pdf` | 8 | 64119 | 489 | 64608 | `Artikel / Jurnal Ilmiah` | 67 | `RULE_14\|RULE_10\|RULE_11\|RULE_16` | 6 / 5 | 7 (0) | 5.62→7.19 (+1.57) | 131.10 | 338.51 |
| 9 | `Roadmap.pdf` | 19 | 44317 | 240 | 44557 | `Buku Ajar / Referensi Teknis` | 21 | `RULE_09\|RULE_10\|RULE_12` | 6 / 6 | 6 (0) | 5.17→5.97 (+0.79) | 125.01 | 332.29 |
| 10 | `next.pdf` | 4 | 11091 | 82 | 11173 | `Buku Ajar / Referensi Teknis` | 8 | `RULE_09\|RULE_10` | 6 / 4 | 4 (0) | 5.13→7.70 (+2.57) | 147.81 | 336.74 |
| 11 | `KHS1.pdf` | 1 | 5295 | 46 | 5341 | `Buku Ajar / Referensi Teknis` *(fallback kata TI)* | 5 | `RULE_09\|RULE_07` | 6 / 2 | 4 (1) | 5.60→6.49 (+0.89) | 129.33 | 324.75 |
| 12 | `KHS2.pdf` | 1 | 5798 | 44 | 5842 | `Dokumen Informatif / Materi Bacaan` | 5 | `RULE_07\|RULE_13` | 6 / 2 | 4 (0) | 5.17→6.23 (+1.05) | 129.30 | 325.13 |
| 13 | `KHS3.pdf` | 1 | 5771 | 42 | 5813 | `Dokumen Informatif / Materi Bacaan` | 5 | `RULE_07\|RULE_13` | 6 / 2 | 4 (0) | 5.17→6.11 (+0.94) | 129.28 | 324.85 |
| 14 | `KHS4.pdf` | 1 | 5754 | 35 | 5789 | `Dokumen Informatif / Materi Bacaan` | 5 | `RULE_07\|RULE_13` | 6 / 2 | 4 (0) | 5.17→6.17 (+1.00) | 129.26 | 325.06 |
| 15 | `KHS5.pdf` | 1 | 5817 | 51 | 5868 | `Dokumen Informatif / Materi Bacaan` | 5 | `RULE_07\|RULE_13` | 6 / 2 | 4 (0) | 5.17→6.17 (+1.00) | 129.31 | 325.17 |
| 16 | `Transkip1.pdf` | 2 | 22986 | 61 | 23047 | `Buku Ajar / Referensi Teknis` *(fallback kata TI)* | 4 | `RULE_09\|RULE_07` | 6 / 2 | 3 (1) | 5.10→9.15 (+4.05) | 148.27 | 340.10 |
| 17 | `Transkip2.pdf` | 2 | 16800 | 72 | 16872 | `Dokumen Informatif / Umum` | 3 | `RULE_07` | 6 / 1 | 3 (0) | 5.28→9.00 (+3.71) | 151.19 | 347.52 |

### 3.2 Analisis Akademik Hasil Pengujian & Profiling Memori

1. **Akurasi Klasifikasi Dokumen Target Domain:**
   - **Baseline Resmi (`SK-01` s.d. `SK-05`):** **`5 / 5` (`100%`)** terklasifikasi tepat beserta seluruh rule kunci (`RULE_01..RULE_08`).
   - **Jurnal Ilmiah Bahasa Indonesia (`12`, `13`, `14`):** **`3 / 3` (`100%`)** terklasifikasi `Artikel / Jurnal Ilmiah`, menembakkan `RULE_14` + `RULE_16`, dan menghasilkan **`0`** *missing required ontology properties*.
   - **Dokumen Non-Domain (`KHS1..5`, `Transkip1..2`):** Berjalan aman tanpa *crash*; `KHS1`/`Transkip1` yang memuat daftar mata kuliah TI (*jaringan, algoritma, sistem*) jatuh ke kategori `Buku Ajar` karena modul parser KHS telah dihapus permanen (`ADR-001` & `batasan_masalah.md`).
2. **Efisiensi Latensi Komputasi:**
   - Lebih dari **99%** waktu eksekusi (`2.2 s` s.d. `75.9 s`) berasal dari tahap render PDF dan OCR optik Google ML Kit per halaman.
   - Mesin **NLP + Ekstraksi SPO + Ontologi + Forward Chaining** bekerja **sangat ringan (`24–494 ms`)** bahkan pada jurnal 10 halaman dengan 59–67 fakta SPO, membuktikan efisiensi desain aturan deterministik lokal.
3. **Konsumsi Memori (Java Heap vs Native ML Kit & Total PSS):**
   - **Java Heap Aplikasi (Objek Kotlin, AST/NLP, SPO, Rule Engine, Summarizer):** Puncak `java_heap_after_mb` hanya **`9.15 MB`** (`Transkip1.pdf`) dengan lonjakan maksimum (`delta`) **`4.05 MB`** (jauh di bawah ambang batas `<= 50 MB`; bahkan dokumen 19 halaman `Roadmap.pdf` hanya menambah `+0.79 MB` Java heap berkat `bitmap.recycle()` per halaman).
   - **Transparansi Akademik (Native Heap & Total PSS):** Native Heap (`88.96–151.19 MB`) dan Total PSS (`305.60–347.52 MB`) mencakup alokasi runtime Android ART serta model native C++/TFLite ML Kit Text Recognition yang dimuat ke RAM sistem saat proses OCR berlangsung. Kedua metrik dilaporkan secara jujur untuk referensi Bab IV Skripsi.

---

## 4. Galeri Bukti Verifikasi UI Visual End-to-End (`aturan/Docs/Evidence/SS/valid/`)

Seluruh tangkapan layar diambil langsung dari aplikasi berjalan di emulator `Tes_untuk_Real_Phone` dalam **Airplane Mode ON** (ikon pesawat tampak pada bilah status) dan telah diinspeksi satu per satu:

| No | Berkas Bukti | Layar / Skenario yang Diverifikasi | Status Visual |
| :-: | :--- | :--- | :---: |
| 1 | [`01_login_guest_offline.png`](Evidence/SS/valid/01_login_guest_offline.png) | Layar masuk mode tamu offline (**Airplane Mode** aktif) | **VERIFIED** |
| 2 | [`02_dashboard_home.png`](Evidence/SS/valid/02_dashboard_home.png) | Layar `Beranda` (`DashboardScreen`) & bottom bar 4 tab (`Beranda`, `Scan`, `ASK Vela`, `Settings`) | **VERIFIED** |
| 3 | [`03_ask_vela_empty.png`](Evidence/SS/valid/03_ask_vela_empty.png) | Layar `ASK Vela` awal sebelum ada riwayat tersimpan | **VERIFIED** *(Catatan kontras ikon `[OBS-UI-001]`)* |
| 4 | [`04_pdf_page_range_dialog.png`](Evidence/SS/valid/04_pdf_page_range_dialog.png) | Pemilihan berkas `12 Penerapan Model Ontologi...pdf` (10 hal.) dengan opsi rentang halaman khusus **`Dari Hal. 1`** – **`Sampai Hal. 5`** | **VERIFIED** |
| 5 | [`05_summary_top_jurnal12.png`](Evidence/SS/valid/05_summary_top_jurnal12.png) | Layar hasil: badge `Artikel / Jurnal Ilmiah`, `Cakupan: Hal. 1 - 5`, rasio `86.3%`, & Kartu **Ringkasan Isi Dokumen (*Document Summarization*)** | **VERIFIED** |
| 6 | [`06_summary_section_and_keypoints.png`](Evidence/SS/valid/06_summary_section_and_keypoints.png) | Chips **Kata Kunci Dominan** & Kartu **Ringkasan Perwakilan Halaman / Bagian** | **VERIFIED** |
| 7 | [`06b_summary_rule_keypoints.png`](Evidence/SS/valid/06b_summary_rule_keypoints.png) | Kartu terpisah **Poin Penting Berbasis Aturan (*Rule-Based Key Points*)**, accordion SPO & Rules, serta tombol `Salin`/`Bagikan`/`Ekspor PDF` | **VERIFIED** |
| 8 | [`07_summary_spo_ontology_rules.png`](Evidence/SS/valid/07_summary_spo_ontology_rules.png) | Accordion terbuka: fakta SPO `[Subjek] predikat -> objek [Concept.Property] (Hal. X)`, daftar pemetaan properti Ontologi terisi, & jejak **`RULE_14`, `RULE_10`, `RULE_11`, `RULE_16`** | **VERIFIED** |
| 9 | [`08_summary_export_pdf_dialog.png`](Evidence/SS/valid/08_summary_export_pdf_dialog.png) | Dialog sukses `"PDF Ringkasan Siap"` hasil ekspor iText7 secara luring | **VERIFIED** |
| 10 | [`09_summary_sk01_formal.png`](Evidence/SS/valid/09_summary_sk01_formal.png) | Hasil analisis dokumen resmi `SK-01_Surat_Keputusan_Rektor.pdf` terklasifikasi `Surat Keputusan (SK)` | **VERIFIED** *(Catatan `[OBS-NLP-001]`)* |
| 11 | [`10_ask_vela_history_populated.png`](Evidence/SS/valid/10_ask_vela_history_populated.png) | Daftar **Riwayat Ringkasan Tersimpan** terisi (`KHS1.pdf`, `SK-01`, `Jurnal 12`) lengkap dengan kotak pencarian & tombol `"Tandai"` | **VERIFIED** |
| 12 | [`11_ask_vela_search_filter.png`](Evidence/SS/valid/11_ask_vela_search_filter.png) | Filter pencarian `"Ontologi"` menyaring tepat hanya `12 Penerapan Model Ontologi...pdf` | **VERIFIED** |
| 13 | [`12_ask_vela_mark_selected.png`](Evidence/SS/valid/12_ask_vela_mark_selected.png) | Mode seleksi `"Tandai"`: indikator `"1 ditandai"`, tombol `"Pilih Semua"`, tombol merah `"Hapus (1)"`, dan kartu terpilih bercentang | **VERIFIED** |
| 14 | [`13_ask_vela_bulk_delete_dialog.png`](Evidence/SS/valid/13_ask_vela_bulk_delete_dialog.png) | Dialog konfirmasi aman `"Hapus 1 Riwayat?"` (berkas PDF asli tetap aman) | **VERIFIED** |
| 15 | [`14_ask_vela_after_delete.png`](Evidence/SS/valid/14_ask_vela_after_delete.png) | Status pasca-konfirmasi hapus: toast `"Riwayat yang ditandai berhasil dihapus"` & hanya 2 riwayat tersisa (`SK-01` dan `Jurnal 12`) | **VERIFIED** |

---

## 5. Pengujian Optimasi Tahap 03: Perbandingan Sebelum vs Sesudah (`Paket 1` & `Paket 1B`)

Setelah penerapan perbaikan kontras UI (`[OBS-UI-001]`), narasi adaptif dokumen pendek (`[OBS-NLP-001]`), serta ekstraksi hibrida **`iText7` (`NATIVE_ITEXT7`) + `AndroidSafeXmlParserFactory` + Fallback ML Kit OCR** pada emulator offline **`Tes_untuk_Real_Phone`** (**Airplane Mode ON**):
- **Waktu Total 17 PDF (`TestbedPipelineBenchmarkTest` `3/3 PASS`):** Turun dari **`344.329` detik** (Tahap 02) menjadi **`29.467` detik** (Tahap 03 — **~11.7x lebih cepat**).
- **Data Mentah CSV Tahap 03:** [`benchmark_results_tahap3.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap3.csv).

### 5.1 Tabel Perbandingan Performa & Kompresi Kunci (Tahap 02 vs Tahap 03)

| Berkas Uji | Mode Ekstraksi (Tahap 03) | Total Waktu Tahap 02 (Pure OCR) | Total Waktu Tahap 03 (Hibrida) | Native Heap T2 → T3 (MB) | Rasio Pemadatan T2 → T3 |
| :--- | :---: | --: | --: | --: | --: |
| `12 Penerapan Model Ontologi...pdf` (10 hal.) | `NATIVE_ITEXT7` | `62,289 ms` | **`4,937 ms`** *(12.6x)* | `88.96` → **`12.87`** | `~86.3%` → **`89.5%`** |
| `13 Konsep Ontologi pada Manajemen RPL...pdf` (9 hal.) | `NATIVE_ITEXT7` | `76,396 ms` | **`3,796 ms`** *(20.1x)* | `130.55` → **`6.12`** | — → **`92.5%`** |
| `14 Pengklasifikasian Dokumen Teks...pdf` (8 hal.) | `NATIVE_ITEXT7` | `64,608 ms` | **`2,817 ms`** *(22.9x)* | `131.10` → **`7.29`** | — → **`91.5%`** |
| `Roadmap.pdf` (19 hal., hal. 11–16 scan gambar) | **`HYBRID`** | `44,557 ms` | **`11,187 ms`** *(4.0x)* | `125.01` → **`61.64`** | — → **`87.0%`** |
| `SK-01_Surat_Keputusan_Rektor.pdf` (1 hal.) | `NATIVE_ITEXT7` | `2,951 ms` | **`57 ms`** *(51.8x)* | — | `0.0%` → **`49.4%`** |
| `SK-02_Surat_Edaran_Jadwal_KRS.pdf` (1 hal.) | `NATIVE_ITEXT7` | `2,445 ms` | **`56 ms`** *(43.7x)* | — | — → **`58.0%`** |
| `SK-03_Pedoman_SOP_Ujian_Skripsi.pdf` (1 hal.) | `NATIVE_ITEXT7` | `2,818 ms` | **`55 ms`** *(51.2x)* | — | — → **`48.4%`** |
| `SK-04_Peraturan_Tata_Tertib_Kampus.pdf` (1 hal.) | `NATIVE_ITEXT7` | `2,246 ms` | **`57 ms`** *(39.4x)* | — | — → **`48.5%`** |
| `SK-05_Laporan_Ringkasan_Kegiatan.pdf` (1 hal.) | `NATIVE_ITEXT7` | `3,397 ms` | **`49 ms`** *(69.3x)* | — | — → **`38.0%`** |

### 5.2 Bukti Visual Verifikasi UI Tahap 03 (`aturan/Docs/Evidence/SS/valid/tahap3/`)

| No | Berkas Bukti | Skenario & Perubahan Terinspeksi | Status |
| :-: | :--- | :--- | :---: |
| 1 | [`01_ask_vela_empty_fixed.png`](Evidence/SS/valid/tahap3/01_ask_vela_empty_fixed.png) | Ikon kartu sumber `ASK Vela` tampil putih kontras tinggi di atas latar gelap `#131B2E` | **VERIFIED** |
| 2 | [`02_progress_overlay_fixed.png`](Evidence/SS/valid/tahap3/02_progress_overlay_fixed.png) | Pill `"Analisis AI 100% Luring"` kontras putih di atas navy gelap dengan indikator progres halus | **VERIFIED** |
| 3 | [`03_summary_jurnal12_top_fixed.png`](Evidence/SS/valid/tahap3/03_summary_jurnal12_top_fixed.png) | Pill `"Pemadatan Teks: 89.4% (2879 -> 304 kata)"` & hero card gelap teks putih terbaca tajam | **VERIFIED** |
| 4 | [`04_summary_jurnal12_keypoints_fixed.png`](Evidence/SS/valid/tahap3/04_summary_jurnal12_keypoints_fixed.png) | Chips **Kata Kunci Dominan** & **Ringkasan Perwakilan Halaman / Bagian** lengkap lencana `Hal. X` | **VERIFIED** |
| 5 | [`05_ask_vela_history_search_fixed.png`](Evidence/SS/valid/tahap3/05_ask_vela_history_search_fixed.png) | Hint kotak pencarian `"Cari judul, topik, atau jenis..."` tampil utuh tanpa terpotong elipsis | **VERIFIED** |
| 6 | [`06_summary_sk01_top_fixed.png`](Evidence/SS/valid/tahap3/06_summary_sk01_top_fixed.png) | **`SK-01` selesai `NATIVE_ITEXT7`:** nomor surat utuh sempurna `112/SK/UNP-KD/X/2026` & pemadatan **`49.4% (154 -> 78 kata)`** | **VERIFIED** |
| 7 | [`07_summary_sk01_keypoints_fixed.png`](Evidence/SS/valid/tahap3/07_summary_sk01_keypoints_fixed.png) | Pemisahan kartu perwakilan halaman & kartu berbingkai amber **Poin Penting Berbasis Aturan** | **VERIFIED** |

### 5.3 Bukti Visual Hotfix UAT (`aturan/Docs/Evidence/SS/valid/fix/`)

| No | Berkas Bukti | Skenario Perbaikan yang Diverifikasi | Status |
| :-: | :--- | :--- | :---: |
| 1 | [`01_history_back_button.png`](Evidence/SS/valid/fix/01_history_back_button.png) | `HistoryScreen` menampilkan ikon kembali di `TopAppBar` & tombol `"Kembali ke Beranda"`, dan navigasi kembali menuju Dashboard (`Beranda`) terverifikasi. | **VERIFIED** |
| 2 | [`02_pdf_to_image_with_zip_fixed.png`](Evidence/SS/valid/fix/02_pdf_to_image_with_zip_fixed.png) | `PdfToImageScreen` dengan dokumen 10 halaman & mode ZIP aktif menampilkan `"Pilih lokasi manual (Save As)"` utuh di atas `"Konversi ke ZIP"`. | **VERIFIED** |
| 3 | [`03_pdf_to_image_large_font_scrolled.png`](Evidence/SS/valid/fix/03_pdf_to_image_large_font_scrolled.png) | Mode gelap + skala font `1.25x` tetap menampilkan baris Save As utuh dan kontainer isi dapat digulir mulus. | **VERIFIED** |

### 5.4 Status Uji Perangkat Fisik (`Infinix Note 30`)
Uji manual di HP fisik **Infinix Note 30** telah dijalankan oleh peneliti (`@useripx`) hingga konfirmasi *"oke sudah work"* untuk rilis **`v1.5.0`**. Karena kabel USB ADB HP fisik belum disambungkan ke komputer pengembangan, metrik benchmark terotomatisasi ARM64 (`TestbedPipelineBenchmarkTest`) vs Emulator x86_64 beserta penyelesaian sisa 5 poin opsional pada [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) dijadwalkan pada **Tahap 04**.
