# Task & Progress Tracker — VelaPDF (`com.njagakneai.velapdf`)

> **DESIGNATED APPROVER:** Yogi Ario (`@useripx`)  
> **AGEN PENGEMBANG:** Carl (`AGT-PRD-DEV-001`)  
> **BRANCH AKTIF:** `feature/03_uji_perangkat_fisik_dan_polish` *(Tahap 01 + 02 dirilis ke `v1.4.0`; kode & uji emulator Tahap 03 selesai — siap UAT HP fisik Infinix Note 30)*  
> **TERAKHIR DIPERBARUI:** 2026-11-12 10:15 WIB

---

## Ringkasan Progres Lintas Tahap

| Tahap | Deskripsi Pekerjaan | Branch | Status | Progres |
| :---: | :--- | :--- | :---: | :---: |
| **Tahap 00** | Penyelarasan Aturan (`aturan/`), Sterilisasi Legacy Stub (`DEF-LEG-004`), & Fix 2 Lint Error (`DEF-LEG-009`) | `develop` | **COMPLETED** | 100% |
| **Tahap 01** | Implementasi Pipeline OCR Luring + 4-JSON (`NLP + SPO + Ontologi + Rules`) + Menu `ASK Vela` + Room DB `v2` + Ekspor PDF + `aturan/simpan/tahap1.md` | `develop` & `main` (`v1.4.0`) | **RELEASED (`v1.4.0`)** | 100% |
| **Tahap 02** | Perbaikan Celah `A–D`, Pencarian & Hapus Massal Riwayat, Benchmark 17 PDF Offline Emulator + Profiling Memori, 15 Screenshot UI, UAT Infinix Note 30 (`"buatkan rilis"`), & `aturan/simpan/tahap2.md` | `develop` & `main` (`v1.4.0`) | **RELEASED (`v1.4.0`)** | 100% |
| **Tahap 03** | Polish Kontras UI (`[OBS-UI-001]`), Pemadatan Surat Pendek (`[OBS-NLP-001]`), Hybrid `iText7` + ML Kit Fallback (`Paket 1B` + `AndroidSafeXmlParserFactory`), Benchmark Emulator (`29.467s`), Screenshot UI Tahap 3, & UAT Kamera Infinix Note 30 | `feature/03_uji_perangkat_fisik_dan_polish` | **IN PROGRESS (Dev & Emulator Done — Menunggu UAT Fisik Infinix Note 30 + `"buatkan rilis"`)** | ~75% *(Total Proyek ~90%)* |

---

## Rincian Paket Kerja Tahap 03

| Paket | Deliverables | Bukti Verifikasi | Status |
| :---: | :--- | :--- | :---: |
| **Paket 1** | Fix kontras ikon `AskVelaScreen`, pill progress `AnalysisProgressOverlay`, hero card & pill kompresi `DocumentSummaryScreen`, placeholder pencarian ringkas, serta kompresi adaptif dokumen `< 220` kata (`FactBasedSummarizer.kt`). | `FactBasedSummarizerTest` (`4/4` PASS), 7 PNG UI [`tahap3/`](Evidence/SS/valid/tahap3/01_ask_vela_empty_fixed.png), `SK-01` `49.4%` (`154 -> 78` kata) | **PASS** |
| **Paket 1B** | Ekstraksi teks PDF native hibrida via **iText7 `PdfTextExtractor`** + fallback otomatis halaman scan ke ML Kit OCR (`MlKitRepository`), pembersihan teks & pengurutan blok kolom spasial (`TextExtractionCleaner`, `NlpTextPreprocessor`), serta `AndroidSafeXmlParserFactory` (`XmlProcessorCreator`) agar bebas error `setXIncludeAware` di Android runtime. | `TextExtractionCleanerAndPdfExtractorTest` (`5/5` PASS), total `26/26` unit test PASS, `lintDebug` `0 errors`, benchmark 17 PDF offline turun **11.7x** (`344.329s` -> **`29.467s`**), [`benchmark_results_tahap3.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap3.csv) | **PASS** |
| **Paket 2** | Pemasangan & pengujian APK di emulator offline (**selesai**) + uji langsung kamera & dokumen nyata di HP fisik **Infinix Note 30** *(perangkat fisik belum terhubung USB ADB pada sesi ini)*. | APK terbaru di [`app-debug.apk`](../../app/build/outputs/apk/debug/app-debug.apk) + panduan [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) | **MENUNGGU UAT HP FISIK** |
| **Paket 3** | Pembaruan `Defect_Log.md`, `Decision_Log.md` (`ADR-006`), `Test_Report.md`, `Verification_Checklist.md`, `Panduan_Belajar_Dosen.md`, [`aturan/simpan/tahap3.md`](../simpan/tahap3.md), & changelog `05`. *(Rilis `v1.5.0` dieksekusi setelah Yogi selesai UAT HP & komentar `buatkan rilis`)*. | Semua dokumen tersinkron & komit rapi di `feature/03_uji_perangkat_fisik_dan_polish` | **PASS (Pre-Release Docs)** |

---

## Tindakan Menunggu Uji & Persetujuan Yogi Ario (`@useripx`)

1. Pasang [`app-debug.apk`](../../app/build/outputs/apk/debug/app-debug.apk) terbaru pada **Infinix Note 30** (atau sambungkan kabel USB Debugging bila ingin benchmark `connectedDebugAndroidTest` dijalankan langsung di HP).
2. Jalankan checklist **Uji Kamera Langsung (`Pindai Kamera` / `Scan`) + Analisis PDF** sesuai [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) dalam **Airplane Mode ON**.
3. Setelah puas dengan hasil uji di HP, beri komentar **`buatkan rilis`** pada [`implementation_plan.md`](Implementation_plan.md) agar Carl mem-bump versi ke **`v1.5.0` (`versionCode = 5`)**, membuat tag & rilis bersih ke `main`/`develop` (atau lanjut ke finalisasi naskah Bab IV).
4. Konfirmasi pencadangan transkrip percakapan hari ini ke `aturan/Docs/chat/riwayat12112026.md`.
