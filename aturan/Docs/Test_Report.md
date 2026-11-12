# Laporan Hasil Pengujian (Test Report) — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** LAPORAN VERIFIKASI TAHAP 02 (`feature/02_evaluasi_testbed_dan_ui`)  
> **DESIGNATED APPROVER:** Yogi Ario (`@useripx` — rujuk [config/organization.md](../config/organization.md))  
> **PENANGGUNG JAWAB QA & PENGUJIAN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL EKSEKUSI VERIFIKASI AKHIR:** 12 November 2026  
> **ACUAN METODOLOGI & TESTBED:** [Standards/Testing_Strategy.md](Standards/Testing_Strategy.md), [Standards/QA_Framework.md](Standards/QA_Framework.md), & [alatuji/](../alatuji/README.md)

---

## 1. Ringkasan Eksekutif & Status Gerbang Kualitas (Tahap 02)

Seluruh rangkaian pengujian otomatis **JVM Unit Test (`20/20` PASS)**, **Android Lint (`0` error)**, **Build APK Debug & AndroidTest (`BUILD SUCCESSFUL`)**, **Android Instrumentation Benchmark Empiris 17 PDF nyata (`3/3` PASS di Emulator `Tes_untuk_Real_Phone` dalam Mode Pesawat / *Offline* Total)**, serta **Verifikasi Visual Interaktif UI end-to-end (`15` bukti tangkapan layar terinspeksi)** telah diselesaikan dengan **100% PASS Gate (0 kegagalan, 0 *crash*)**.

### Tabel Riwayat Eksekusi Gerbang Mutu Lintas Tahap

| Tahap | Branch | Lingkup Uji | Perintah Eksekusi | Hasil Teramati |
| :---: | :--- | :--- | :--- | :--- |
| **Tahap 00** | `develop` | Smoke test + Lint fix (`DEF-LEG-009`) | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` | `BUILD SUCCESSFUL` (`3m 17s`), `1/1` test, `0` lint error (`46` warn, `1` hint) |
| **Tahap 01** | `feature/01_ocr_dan_ask_vela` | 4-JSON AI Pipeline (`SK-01..05` + IoT) | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` | `BUILD SUCCESSFUL` (`1m 53s`), `11/11` test, `0` lint error (`48` warn, `1` hint) |
| **Tahap 02** | `feature/02_evaluasi_testbed_dan_ui` | Celah `A–D`, Search/Mark/Batch Delete, Testbed 17 PDF & UI Emulator | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest --no-daemon` + `am instrument` (`TestbedPipelineBenchmarkTest`) | **`BUILD SUCCESSFUL` (`6m 24s`)**, **`20/20` JVM unit test PASS**, **`0` lint error** (`47` warn, `1` hint), **`3/3` androidTest PASS (`344.329s`)**, **17/17 PDF luring tanpa *crash***, **15 PNG UI terverifikasi** |

---

## 2. Rincian Pengujian Unit JVM (`app/src/test/`) — `20 / 20 PASS`

Berkas XML hasil uji pada `app/build/test-results/testDebugUnitTest/`:

| Suite Test (`com.njagakneai.velapdf...`) | Jumlah Test | Lulus | Gagal / Skip | Fokus Cakupan Terverifikasi |
| :--- | :---: | :---: | :---: | :--- |
| [`NlpLexiconAndPreprocessorTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/nlp/NlpLexiconAndPreprocessorTest.kt) | 3 | **3** | 0 / 0 | Pemuatan `nlp_lexicon_id.json`, proteksi singkatan baku, penghapusan stopword biasa sekaligus **pelestarian kata negasi/modalitas (`tidak`, `bukan`, `belum`, `wajib`, `dilarang`, `harus`)** *(Celah B)*. |
| [`SpoPatternAndRuleEngineTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/inference/SpoPatternAndRuleEngineTest.kt) | 10 | **10** | 0 / 0 | Asal fakta `mengindikasikanKonsep` *(Celah A)*, pencegahan false-positive `"tidak wajib"` vs `"wajib"` *(Celah B)*, pemetaan eksplisit properti Ontologi–SPO `ConceptId.propertyName` *(Celah C)*, evaluasi `SK-01..SK-05`, Buku IoT Hal. 23–35, serta klasifikasi `ArtikelJurnalIlmiah` (`RULE_14`–`RULE_16`). |
| [`FactBasedSummarizerTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizerTest.kt) | 3 | **3** | 0 / 0 | Pemisahan tegas *Document Summarization* (keseluruhan + perwakilan awal/inti/akhir) vs *Rule-Based Key Points* *(Celah D)* lengkap dengan rujukan `Hal. X`. |
| [`HistorySearchAndBatchDeleteTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/ui/viewmodel/HistorySearchAndBatchDeleteTest.kt) | 3 | **3** | 0 / 0 | Filter pencarian riwayat (*case-insensitive* lintas judul/topik/jenis/isi), batas ukuran riwayat kosong, serta penghapusan massal *chunked* `batchSize = 500` agar tetap aman di bawah batas bind SQLite `999`. |
| [`ExampleUnitTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/test/java/com/njagakneai/velapdf/ExampleUnitTest.kt) | 1 | **1** | 0 / 0 | Baseline JVM test runner. |
| **TOTAL** | **20** | **20** | **0 / 0** | **100% PASS** |

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
