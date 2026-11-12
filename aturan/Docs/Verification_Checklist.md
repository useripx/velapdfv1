# Checklist Verifikasi & Kualitas (Quality Gate Checklist) — VelaPDF

> **NAMA BERKAS:** `aturan/Docs/Verification_Checklist.md`  
> **FUNGSI DOKUMEN:** Daftar periksa pengendalian mutu (*quality gate*) sebelum pengajuan persetujuan tahap kepada Designated Approver (`Yogi Ario` / `@useripx`).  
> **STANDAR TERKAIT:** [QA_Framework.md](Standards/QA_Framework.md), [Testing_Strategy.md](Standards/Testing_Strategy.md), [Defect_Management.md](Standards/Defect_Management.md), [Git_Version_Control.md](Standards/Git_Version_Control.md)  
> **TERAKHIR DIPERBARUI:** 2026-11-12 09:05 WIB *(Tahap 02 — `feature/02_evaluasi_testbed_dan_ui`)*

---

## Checklist Gerbang Kualitas (Mandatory Release Gates)

### 1. Gate 1 — Kebersihan Repositori & Konfigurasi (Repository & Security)
- [x] File kredensial rahasia (`google-services.json`, `*.keystore`, `*.jks`, `.env`, `local.properties`) tercatat aman di `.gitignore` *(terkecuali metadata publik proyek; `app/gs.json` telah dibersihkan)*.
- [x] Tidak ada file build sementara (`/build/`, `/.gradle/`), file IDE (`.idea/`, `.vscode/`, `app/.idea/`), atau crash log (`hs_err_pid*.log`) yang masuk ke stage/commit Git.
- [x] Folder [`aturan/`](../README.md) dan [`aturan/simpan/`](../simpan/tahap2.md) tetap terjaga di cabang kerja/pengembangan dan tidak digabung ke branch publik `main`.

### 2. Gate 2 — Kompilasi & Analisis Statis (Static Analysis & Build)
- [x] Kompilasi Debug Build (`.\gradlew.bat assembleDebug assembleDebugAndroidTest --no-daemon`) lulus **100%** tanpa galat.
- [x] Pemeriksaan Android Lint (`.\gradlew.bat lintDebug --no-daemon`) mencatat **`0 errors`** (`47 warnings, 1 hint` pada `lint-results-debug.txt`; `UseKtx` pada `AskVelaScreen.kt` telah ditutup).
- [x] Arsitektur **MVVM + Clean Architecture** (Compose, Hilt, Room v2 `MIGRATION_1_2`, `Dispatchers.IO` untuk OCR/PDF/IO) dipatuhi dan terdokumentasi di `Decision_Log.md` (`ADR-005`).

### 3. Gate 3 — Verifikasi Fungsional & Ketangguhan Mesin AI (Test Suite Execution)
- [x] **100%** unit test JVM lulus: **`20 / 20` PASS** (`NlpLexiconAndPreprocessorTest` `3/3`, `SpoPatternAndRuleEngineTest` `10/10`, `FactBasedSummarizerTest` `3/3`, `HistorySearchAndBatchDeleteTest` `3/3`, `ExampleUnitTest` `1/1`).
- [x] **100%** uji instrumentasi testbed offline lulus: [`TestbedPipelineBenchmarkTest`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/androidTest/java/com/njagakneai/velapdf/TestbedPipelineBenchmarkTest.kt) **`3 / 3` PASS** (`Time: 344.329s`) pada emulator `Tes_untuk_Real_Phone` dalam **Airplane Mode ON**.
- [x] Seluruh **17 dokumen PDF uji** tereksekusi tanpa *crash* (`SK-01..05` `5/5 = 100%`, Jurnal `12..14` `3/3 = 100%`, `Roadmap.pdf` 19 hal. stabil dengan delta Java heap `+0.79 MB`; puncak Java heap `9.15 MB` << `50 MB`).
- [x] **15 tangkapan layar UI visual** (`01_login_guest_offline.png` s.d. `14_ask_vela_after_delete.png` termasuk `06b`) diinspeksi langsung untuk alur unggah PDF rentang halaman, ringkasan, ekspansi SPO/Rules, ekspor PDF, pencarian, tandai, & hapus massal riwayat.
- [ ] **Uji Kamera Langsung & HP Fisik Xiaomi Redmi Note 11:** Belum dapat disimulasikan penuh tanpa perangkat fisik USB (*terjadwal resmi pada Tahap 03*).

### 4. Gate 4 — Pembaruan Dokumentasi (Documentation Sync)
- [x] `Test_Report.md`, `Decision_Log.md` (`ADR-005`), `Database_Design.md`, `Defect_Log.md`, `Task_Status.md`, `dataset_catalog.md`, `ground_truth_facts.json`, dan `benchmark_results_tahap2.csv` selaras dengan kondisi aktual.
- [x] Rencana Tahap 02 diarsipkan ke [`02_tahap02_Implementation_plan_penyempurnaan_ai_dan_testbed.md`](Implementation_plan/02_tahap02_Implementation_plan_penyempurnaan_ai_dan_testbed.md) dan Rencana Tahap 03 ditulis di [`Implementation_plan.md`](Implementation_plan.md).
- [x] Materi belajar dosen [`aturan/simpan/tahap2.md`](../simpan/tahap2.md), indeks [`Panduan_Belajar_Dosen.md`](Panduan_Belajar_Dosen.md), & [`03changelog0905am12112026.md`](../changelog/03changelog0905am12112026.md) dibuat lengkap.

### 5. Gate 5 — Persetujuan Pemilik Proyek (Approval Sign-Off)
- [ ] Perubahan telah ditinjau oleh Yogi Ario (`@useripx`) sebelum merge ke `develop` dan sebelum penawaran pencadangan chat sesi (`riwayat12112026.md`).

> *Checklist ditandatangani oleh `AGT-PRD-DEV-001` (Carl) berdasarkan verifikasi langsung pasca-edit pada `feature/02_evaluasi_testbed_dan_ui` tanggal `2026-11-12`.*
