# VelaPDF v1.4.0 — ASK Vela Offline AI (Stage 1 & Stage 2)

> **Tag Git:** `v1.4.0` (`versionCode = 4`, `versionName = "1.4.0"`)  
> **Repositori:** `https://github.com/useripx/velapdfv1` (`main` bersih tanpa folder `aturan/`)  
> **Tanggal Rilis:** 12 November 2026  
> **Status Uji HP Fisik:** Lulus UAT pada **Infinix Note 30** (*Airplane Mode ON* — dikonfirmasi via `"buatkan rilis"` oleh `@useripx`)

---

## Ringkasan Fitur pada Rilis `v1.4.0`

### 1. ASK Vela — Analisis & Ringkasan Dokumen Cerdas 100% Luring (*Offline-First*)
- **Input Fleksibel:** Mendukung pemindaian kamera langsung (*ML Kit Document Scanner*), pemilihan gambar galeri multi-halaman, serta unggah berkas PDF dengan pemilihan **Semua Halaman** atau **Rentang Halaman Tertentu** (*default guard* maks. 35 halaman/sesi untuk menjaga heap memori rendah).
- **Pratinjau & Edit Teks OCR (`OcrPreviewScreen`):** Pengguna dapat meninjau dan mengoreksi hasil bacaan OCR terlebih dahulu sebelum ringkasan dibuat.
- **Arsitektur 4-JSON Knowledge Engine (`app/src/main/assets/`):**
  1. `nlp_lexicon_id.json` — Prapemrosesan NLP Bahasa Indonesia beserta pelestarian token negasi & modalitas deontik (`tidak`, `bukan`, `belum`, `wajib`, `dilarang`, `harus`).
  2. `spo_pattern_dictionary.json` — Ekstraksi fakta semantik **Subjek–Predikat–Objek (SPO)** dan *negation guards*.
  3. `document_ontology.json` — Klasifikasi konsep dokumen (SK, Surat Edaran, SOP, Peraturan, Laporan, Artikel Jurnal Ilmiah, Buku Referensi) beserta pemetaan eksplisit properti ontologi.
  4. `production_rules.json` — Mesin inferensi **Forward Chaining** deterministik (`RULE_01` s.d. `RULE_16`) yang menghasilkan jejak inferensi transparan.
- **Pemisahan Hasil (`DocumentSummaryScreen` & `SummaryPdfExporter`):** Memisahkan secara tegas antara **Ringkasan Isi Dokumen (*Document Summarization* lintas bagian Awal–Tengah–Akhir)** dengan **Poin Penting Berbasis Aturan (*Rule-Based Key Points*)**, lengkap dengan accordion *Fakta Semantik SPO & Ontologi*, *Jejak Rule-Based Inference*, salin teks, bagikan, dan ekspor ringkasan ke berkas `.pdf`.

### 2. Manajemen Riwayat Lokal (`Room DB v2` + `ASK Vela`)
- **Migrasi Aman `MIGRATION_1_2`:** Menambah tabel `analysis_history_table` tanpa menghapus riwayat utilitas PDF lama.
- **Pencarian & Hapus Massal (`Tandai`):** Pencarian riwayat secara *real-time* (judul, topik, kategori, isi), pemilihan item / *Pilih Semua*, serta hapus massal *chunked* (`batchSize = 500`) dengan dialog konfirmasi aman.

---

## Hasil Verifikasi Rilis `v1.4.0`

- `./gradlew testDebugUnitTest`: **`20 / 20` PASS** (`0 failed`, `0 error`, `0 skipped` — `SpoPatternAndRuleEngineTest` 10, `NlpLexiconAndPreprocessorTest` 3, `FactBasedSummarizerTest` 3, `HistorySearchAndBatchDeleteTest` 3, `ExampleUnitTest` 1)
- `./gradlew lintDebug`: **`0 errors`** (`47 warnings, 1 hint`)
- `./gradlew assembleDebug`: **PASS** -> `app/build/outputs/apk/debug/app-debug.apk` (`76,860,617` byte)
- `./gradlew assembleRelease`: **PASS** -> `app/build/outputs/apk/release/app-release-unsigned.apk` (`68,265,827` byte — *unsigned*, karena `signingConfig` release keystore belum dikonfigurasi di proyek)
- **Benchmark Emulator Offline (Tahap 02 Testbed, 17 PDF nyata):** **`3 / 3` PASS** (`344.329s`, `Tes_untuk_Real_Phone`, Airplane Mode ON)

---

## Cara Unggah ke GitHub Releases (Jika `gh` CLI Belum Login)
1. Buka halaman Releases repositori: `https://github.com/useripx/velapdfv1/releases/new?tag=v1.4.0`
2. Pilih tag **`v1.4.0`** (yang sudah di-*push* ke GitHub).
3. Isi judul rilis **`VelaPDF v1.4.0 — ASK Vela Offline AI (Stage 1 & 2)`** dan salin isi dokumen ini ke deskripsi rilis.
4. Lampirkan berkas APK yang siap pasang di HP Android: `app/build/outputs/apk/debug/app-debug.apk` *(atau APK release yang sudah ditandatangani dengan keystore Anda)*, lalu klik **Publish release**.
