# Implementation Plan — Tahap 03: Uji Perangkat Fisik (Infinix Note 30 + Kamera Langsung), Polish UI Visual & Akurasi Ringkas Surat Pendek, Serta Rilis `v1.5.0`

> **STATUS:** **COMPLETED & RELEASED (`v1.5.0` / `versionCode = 5`)** *(Paket 1, Paket 1B, Uji Emulator `29.467s`, UAT Infinix Note 30, & Hotfix `fix/pdf-to-image-overlap-and-history-back` tuntas terintegrasi)*  
> **NOMOR TAHAPAN:** Tahap 03 (Physical Hardware Verification, UI/NLP Polish, Hotfix UAT & Release `v1.5.0`)  
> **PROYEK:** VelaPDF (`com.njagakneai.velapdf` — `versionCode = 5` / `"1.5.0"`)  
> **PEMILIK PROYEK:** Yogi Ario Pratama (`@useripx`)  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL SELESAI:** 12/11/2026  
> **BRANCH:** `feature/03_uji_perangkat_fisik_dan_polish` + `fix/pdf-to-image-overlap-and-history-back` -> `develop` -> `main` (`v1.5.0`)  
> **PRASYARAT SELESAI:** Tahap 00 (`develop`), Tahap 01, & Tahap 02 (`v1.4.0`)

---

## 1. Tujuan & Ringkasan Hasil Tahap 03

1. **Polish Kontras UI (`[OBS-UI-001]` / `DEF-STAGE3-001`):**
   - Memperbaiki kontras ikon putih di atas `primaryContainer` gelap pada [`AskVelaScreen.kt`](../../../app/src/main/java/com/njagakneai/velapdf/ui/screen/AskVelaScreen.kt), pill progres `"Analisis AI 100% Luring"` pada [`AnalysisProgressOverlay.kt`](../../../app/src/main/java/com/njagakneai/velapdf/ui/components/AnalysisProgressOverlay.kt), hero card/pill rasio pada [`DocumentSummaryScreen.kt`](../../../app/src/main/java/com/njagakneai/velapdf/ui/screen/DocumentSummaryScreen.kt), serta merapikan *placeholder* kotak pencarian (`"Cari judul, topik, atau jenis..."`).
2. **Narasi Adaptif Surat Pendek (`[OBS-NLP-001]` / `DEF-STAGE3-002`):**
   - Menyempurnakan [`FactBasedSummarizer.kt`](../../../app/src/main/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizer.kt) untuk dokumen `< 220` kata sehingga `SK-01` mencatat pemadatan **`49.4%` (`154 -> 78` kata)** dan seluruh `SK-01..SK-05` mencapai **`38.0% – 58.0%`**.
3. **Ekstraksi PDF Hibrida `Paket 1B` & Fix Android XML (`DEF-STAGE3-003` / `ADR-006`):**
   - Menambahkan [`TextExtractionCleaner.kt`](../../../app/src/main/java/com/njagakneai/velapdf/domain/nlp/TextExtractionCleaner.kt) serta membaca PDF digital via **iText7 `PdfTextExtractor`** dengan *fallback* otomatis per halaman scan ke **ML Kit OCR** di [`MlKitRepository.kt`](../../../app/src/main/java/com/njagakneai/velapdf/data/ocr/MlKitRepository.kt) (termasuk `AndroidSafeXmlParserFactory`).
   - Memangkas waktu uji 17 PDF offline dari **`344.329s`** menjadi **`29.467s`** (~11.7x lebih cepat) pada [`benchmark_results_tahap3.csv`](../../alatuji/05_spesifikasi_testbed/benchmark_results_tahap3.csv).
4. **Hotfix UAT Perangkat Fisik (`fix/pdf-to-image-overlap-and-history-back` — `DEF-FIX-001` & `DEF-FIX-002`):**
   - Menambahkan `.verticalScroll(rememberScrollState())` + `.imePadding()` & penyesuaian spasi di [`PdfToImageScreen.kt`](../../../app/src/main/java/com/njagakneai/velapdf/ui/screen/PdfToImageScreen.kt) agar baris `"Pilih lokasi manual (Save As)"` tidak terpotong di atas `"Konversi ke ZIP"`.
   - Menambahkan `navigationIcon` kembali ke Beranda & tombol `Kembali ke Beranda` pada [`HistoryScreen.kt`](../../../app/src/main/java/com/njagakneai/velapdf/ui/screen/HistoryScreen.kt) serta pengaman `popBackStack(Screen.Dashboard.route, inclusive = false)` di [`NavGraph.kt`](../../../app/src/main/java/com/njagakneai/velapdf/ui/navigation/NavGraph.kt).

---

## 2. Rekap Verifikasi Kelulusan

- **Unit Test JVM (`testDebugUnitTest`):** `26 / 26` PASS (`0 failed`, `0 error`, `0 skipped`).
- **Android Lint (`lintDebug`):** `0 errors` (`lint-results-debug.sarif`).
- **Emulator Offline (`Tes_untuk_Real_Phone`, Airplane Mode ON):** `TestbedPipelineBenchmarkTest` `3 / 3` PASS (`29.467s`), `7` screenshot UI Tahap 03 ([`tahap3/`](../Evidence/SS/valid/tahap3/01_ask_vela_empty_fixed.png)), + `3` screenshot UI Hotfix ([`fix/`](../Evidence/SS/valid/fix/01_history_back_button.png)).
- **Uji Perangkat Fisik Infinix Note 30:** Terverifikasi oleh Yogi Ario (`@useripx`) sebelum rilis `v1.5.0` (menyisakan 5 butir opsional/detail di [`Checklist_Uji_HP_Infinix_Note30.md`](../Checklist_Uji_HP_Infinix_Note30.md) & pengambilan metrik USB ARM64 untuk diteruskan pada Tahap 04).
- **Panduan Belajar Dosen:** [`aturan/simpan/tahap3.md`](../../simpan/tahap3.md) (`Q31`–`Q36`).
