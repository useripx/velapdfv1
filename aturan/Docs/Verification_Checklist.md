# Checklist Verifikasi & Kualitas (Quality Gate Checklist) — VelaPDF

> **NAMA BERKAS:** `aturan/Docs/Verification_Checklist.md`  
> **FUNGSI DOKUMEN:** Daftar periksa pengendalian mutu (*quality gate*) sebelum pengajuan persetujuan tahap kepada Designated Approver (`Yogi Ario` / `@useripx`).  
> **STANDAR TERKAIT:** [QA_Framework.md](Standards/QA_Framework.md), [Testing_Strategy.md](Standards/Testing_Strategy.md), [Defect_Management.md](Standards/Defect_Management.md), [Git_Version_Control.md](Standards/Git_Version_Control.md)  
> **TERAKHIR DIPERBARUI:** 2026-11-12 11:10 WIB *(Rilis `v1.5.0` — Tahap 03 + Hotfix UI `fix/pdf-to-image-overlap-and-history-back`)*

---

## Checklist Gerbang Kualitas (Mandatory Release Gates)

### 1. Gate 1 — Kebersihan Repositori & Konfigurasi (Repository & Security)
- [x] File kredensial rahasia (`google-services.json`, `*.keystore`, `*.jks`, `.env`, `local.properties`) tercatat aman di `.gitignore` *(terkecuali metadata publik proyek; `app/gs.json` telah dibersihkan)*.
- [x] Tidak ada file build sementara (`/build/`, `/.gradle/`), file IDE (`.idea/`, `.vscode/`, `app/.idea/`), atau crash log (`hs_err_pid*.log`) yang masuk ke stage/commit Git.
- [x] Folder [`aturan/`](../README.md) dan [`aturan/simpan/`](../simpan/tahap3.md) tetap terjaga di cabang kerja/pengembangan dan tidak digabung ke branch publik `main`.

### 2. Gate 2 — Kompilasi & Analisis Statis (Static Analysis & Build)
- [x] Kompilasi Debug & Release pasca-bump **`versionCode = 5` / `versionName = "1.5.0"`** (`./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease --console=plain`) lulus **100%** (`BUILD SUCCESSFUL in 11m 51s`).
- [x] Pemeriksaan Android Lint (`lintDebug` & `lintVitalRelease`) mencatat **`0 errors`** pada `lint-results-debug.sarif`.
- [x] Arsitektur **MVVM + Clean Architecture** (Compose, Hilt, Room v2 `MIGRATION_1_2`, `Dispatchers.IO`, `AndroidSafeXmlParserFactory`) dipatuhi dan terdokumentasi di `Decision_Log.md` (`ADR-005`, `ADR-006`).

### 3. Gate 3 — Verifikasi Fungsional & Ketangguhan Mesin AI (Test Suite Execution)
- [x] **100%** unit test JVM lulus pasca-edit terakhir: **`26 / 26` PASS** (`SpoPatternAndRuleEngineTest` `10/10`, `NlpLexiconAndPreprocessorTest` `3/3`, `TextExtractionCleanerAndPdfExtractorTest` `5/5`, `FactBasedSummarizerTest` `4/4`, `HistorySearchAndBatchDeleteTest` `3/3`, `ExampleUnitTest` `1/1`).
- [x] **100%** uji instrumentasi testbed offline lulus pasca-edit terakhir: [`TestbedPipelineBenchmarkTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/androidTest/java/com/njagakneai/velapdf/TestbedPipelineBenchmarkTest.kt) **`3 / 3` PASS** (`Time: 29.467s`, **~11.7x lebih cepat** daripada `344.329s` di Tahap 02) pada emulator `Tes_untuk_Real_Phone` dalam **Airplane Mode ON**.
- [x] Seluruh **17 dokumen PDF uji** tereksekusi tanpa *crash*: `SK-01..05` (`49–57 ms`, kompresi **`38.0%–58.0%`**), Jurnal `12..14` (`2.8–4.9 s`, kompresi **`89.5%–92.5%`**, `0` missing props), `Roadmap.pdf` (`HYBRID` 19 hal. dalam `11.19s`).
- [x] **7 tangkapan layar verifikasi Tahap 03** (`aturan/Docs/Evidence/SS/valid/tahap3/`) + **3 tangkapan layar verifikasi Hotfix UI** (`aturan/Docs/Evidence/SS/valid/fix/`) diinspeksi langsung: memverifikasi kontras ikon putih `AskVelaScreen`, pill `"Analisis AI 100% Luring"`, placeholder `"Cari judul, topik, atau jenis..."`, `Pemadatan Teks: 49.4%` (`SK-01`), akurasi nomor surat `112/SK/UNP-KD/X/2026`, scrollability `PdfToImageScreen`, & tombol kembali ke Beranda `HistoryScreen`.
- [x] **UAT Perangkat Fisik Infinix Note 30 (`v1.5.0`):** Terkonfirmasi *"oke sudah work"* oleh Yogi Ario (`@useripx`) setelah perbaikan UI `PdfToImageScreen` & `HistoryScreen` *(sisa 5 butir detail checklist & opsional metrik USB ARM64 Bab IV dijadwalkan pada Tahap 04)*.

### 4. Gate 4 — Pembaruan Dokumentasi (Documentation Sync)
- [x] `Test_Report.md`, `Decision_Log.md` (`ADR-006`), `Defect_Log.md` (`DEF-FIX-001`, `DEF-FIX-002`), `Task_Status.md`, `Checklist_Uji_HP_Infinix_Note30.md`, `Panduan_Belajar_Dosen.md`, `benchmark_results_tahap3.csv`, `aturan/simpan/tahap3.md`, `Release_Notes_v1.5.0.md`, arsip `03_tahap03...md`, & changelog `06` tersinkron penuh.

### 5. Gate 5 — Persetujuan Rilis (`v1.5.0`)
- [x] Rilis **`v1.5.0` (`versionCode = 5`)** disetujui langsung oleh Yogi Ario (`@useripx`): `develop` & `main` bersih (tanpa `aturan/`) + tag `v1.5.0` di-push ke `origin` bersama artefak APK [`VelaPDF-v1.5.0-debug.apk`](../../release/VelaPDF-v1.5.0-debug.apk).

> *Checklist diperbarui oleh `AGT-PRD-DEV-001` (Carl) berdasarkan verifikasi rilis `v1.5.0` pada tanggal `2026-11-12`.*
