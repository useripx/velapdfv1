# Task & Progress Tracker — VelaPDF (`com.njagakneai.velapdf`)

> **DESIGNATED APPROVER:** Yogi Ario (`@useripx`)  
> **AGEN PENGEMBANG:** Carl (`AGT-PRD-DEV-001`)  
> **BRANCH AKTIF:** `develop` *(Tahap 03 + Hotfix UI dirilis ke `v1.5.0`; menunggu persetujuan `Proceed` Implementation Plan Tahap 04)*  
> **TERAKHIR DIPERBARUI:** 2026-11-12 11:00 WIB

---

## Ringkasan Progres Lintas Tahap

| Tahap | Deskripsi Pekerjaan | Branch | Status | Progres |
| :---: | :--- | :--- | :---: | :---: |
| **Tahap 00** | Penyelarasan Aturan (`aturan/`), Sterilisasi Legacy Stub (`DEF-LEG-004`), & Fix 2 Lint Error (`DEF-LEG-009`) | `develop` | **COMPLETED** | 100% |
| **Tahap 01** | Implementasi Pipeline OCR Luring + 4-JSON (`NLP + SPO + Ontologi + Rules`) + Menu `ASK Vela` + Room DB `v2` + Ekspor PDF + `aturan/simpan/tahap1.md` | `develop` & `main` (`v1.4.0`) | **RELEASED (`v1.4.0`)** | 100% |
| **Tahap 02** | Perbaikan Celah `A–D`, Pencarian & Hapus Massal Riwayat, Benchmark 17 PDF Offline Emulator + Profiling Memori, 15 Screenshot UI, UAT Infinix Note 30 (`"buatkan rilis"`), & `aturan/simpan/tahap2.md` | `develop` & `main` (`v1.4.0`) | **RELEASED (`v1.4.0`)** | 100% |
| **Tahap 03** | Polish Kontras UI (`[OBS-UI-001]`), Pemadatan Surat Pendek (`[OBS-NLP-001]`), Hybrid `iText7` + ML Kit Fallback (`Paket 1B` + `AndroidSafeXmlParserFactory`), Benchmark Emulator (`29.467s`), UAT Infinix Note 30, & Hotfix `fix/pdf-to-image-overlap-and-history-back` | `develop` & `main` (`v1.5.0`) | **RELEASED (`v1.5.0`)** | 100% |
| **Tahap 04** | Finalisasi Skripsi Bab IV (Black-Box, SUS & Opsional Benchmark USB ARM64), Audit Utilitas Legacy Opsional, & Persiapan Rilis Akhir (`v1.6.0`) | Menunggu `Proceed` | **PROPOSED (Plan Ready)** | 0% *(Total Proyek ~90%)* |

---

## Rincian Paket Kerja Tahap 03

| Paket | Deliverables | Bukti Verifikasi | Status |
| :---: | :--- | :--- | :---: |
| **Paket 1** | Fix kontras ikon `AskVelaScreen`, pill progress `AnalysisProgressOverlay`, hero card & pill kompresi `DocumentSummaryScreen`, placeholder pencarian ringkas, serta kompresi adaptif dokumen `< 220` kata (`FactBasedSummarizer.kt`). | `FactBasedSummarizerTest` (`4/4` PASS), 7 PNG UI [`tahap3/`](Evidence/SS/valid/tahap3/01_ask_vela_empty_fixed.png), `SK-01` `49.4%` (`154 -> 78` kata) | **PASS** |
| **Paket 1B** | Ekstraksi teks PDF native hibrida via **iText7 `PdfTextExtractor`** + fallback otomatis halaman scan ke ML Kit OCR (`MlKitRepository`), pembersihan teks & pengurutan blok kolom spasial (`TextExtractionCleaner`, `NlpTextPreprocessor`), serta `AndroidSafeXmlParserFactory` (`XmlProcessorCreator`) agar bebas error `setXIncludeAware` di Android runtime. | `TextExtractionCleanerAndPdfExtractorTest` (`5/5` PASS), total `26/26` unit test PASS, `lintDebug` `0 errors`, benchmark 17 PDF offline turun **11.7x** (`344.329s` -> **`29.467s`**), [`benchmark_results_tahap3.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap3.csv) | **PASS** |
| **Paket 2** | Pengujian APK di emulator offline (**selesai**) + UAT langsung HP fisik **Infinix Note 30** (*Airplane Mode ON* — dikonfirmasi `"oke sudah work"` beserta hotfix UI `PdfToImageScreen` & `HistoryScreen`). | 3 PNG [`fix/`](Evidence/SS/valid/fix/01_history_back_button.png) + [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) *(5 item opsional/detail tersisa lanjut Tahap 04)* | **PASS (UAT Rilis)** |
| **Paket 3** | Rilis **`v1.5.0` (`versionCode = 5`)** (`develop` + `main` bersih + tag `v1.5.0`), [`Release_Notes_v1.5.0.md`](Release_Notes_v1.5.0.md), arsip [`03_tahap03...md`](Implementation_plan/03_tahap03_Implementation_plan_uji_perangkat_fisik_dan_polish.md), [`aturan/simpan/tahap3.md`](../simpan/tahap3.md), & changelog `06`. | Semua dokumen rilis tersinkron ke `develop` & `main` bersih | **PASS** |

---

## Tindakan Menunggu Uji & Persetujuan Yogi Ario (`@useripx`)

1. Unggah berkas [`VelaPDF-v1.5.0-debug.apk`](../../release/VelaPDF-v1.5.0-debug.apk) pada halaman rilis tag `v1.5.0` di GitHub (`https://github.com/useripx/velapdfv1/releases/new?tag=v1.5.0` bila `gh` CLI belum terpasang).
2. Tinjau **Implementation Plan — Tahap 04** pada `implementation_plan.md` / [`Implementation_plan.md`](Implementation_plan.md) lalu beri komentar/pilih paket kerja atau klik **`Proceed`** untuk memulai eksekusi Tahap 04.
3. Selesaikan 5 butir checklist tersisa (`L51`, `L52`, `L53`, `L62`, `L71`) pada [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) (dan sambungkan USB HP bila ingin benchmark ARM64 untuk tabel Bab IV).
4. Konfirmasi pencadangan transkrip percakapan hari ini ke `aturan/Docs/chat/riwayat12112026.md`.
