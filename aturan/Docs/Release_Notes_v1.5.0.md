# VelaPDF v1.5.0 — Hybrid iText7+OCR, UI Polish & Physical UAT Fixes

> **Tag Git:** `v1.5.0` (`versionCode = 5`, `versionName = "1.5.0"`)  
> **Repositori:** `https://github.com/useripx/velapdfv1` (`main` bersih tanpa folder `aturan/`)  
> **Tanggal Rilis:** 12 November 2026  
> **Status Uji HP Fisik:** Terverifikasi pada **Infinix Note 30** (*Airplane Mode ON* — dikonfirmasi *"oke sudah work"* oleh `@useripx`)

---

## Ringkasan Fitur & Perbaikan pada Rilis `v1.5.0`

### 1. Ekstraksi PDF Hibrida `Paket 1B` & Perbaikan XML Parser Android (`ADR-006`)
- **iText7 Native Extractor + Fallback ML Kit OCR Per-Halaman (`MlKitRepository.kt` & `TextExtractionCleaner.kt`):** Halaman PDF digital standar dibaca langsung melalui **iText7 `PdfTextExtractor`** (`SimpleTextExtractionStrategy` & cadangan `LocationTextExtractionStrategy`), sedangkan halaman citra/scan atau *ToUnicode CMap* rusak secara otomatis dialihkan ke `PdfRenderer + ML Kit OCR`.
- **Android XML Parser Compatibility (`AndroidSafeXmlParserFactory`):** Menghindari `UnsupportedOperationException (setXIncludeAware)` dari parser XML bawaan runtime Android ART saat iText7 membaca metadata XMP PDF.
- **Pembersih Teks & Spasial (`TextExtractionCleaner.kt`):** Menyambung kata terpenggal tanda hubung akhir baris (`-\n`) dengan tetap menjaga kata ulang Bahasa Indonesia (*"undang-undang"*), membuang nomor halaman terisolasi, serta merapikan urutan blok multi-kolom.
- **Dampak Performa Terukur:** Waktu benchmark offline 17 dokumen PDF testbed turun **~11.7x** dari **`344.329 detik`** menjadi **`29.467 detik`**, dengan nomor surat resmi seperti `112/SK/UNP-KD/X/2026` terbaca utuh 100%.

### 2. Polish Kontras UI & Narasi Adaptif Surat Pendek (`Tahap 03`)
- **Kontras UI (`AskVelaScreen`, `AnalysisProgressOverlay`, `DocumentSummaryScreen`):** Ikon kartu menu memakai tint putih kontras tinggi, pill `"Analisis AI 100% Luring"` dan `"Pemadatan Teks"` diperjelas keterbacaannya, serta placeholder bar pencarian diringkas (`"Cari judul, topik, atau jenis..."`) agar tidak terpotong elipsis.
- **Pemadatan Dokumen Pendek (`FactBasedSummarizer.kt`):** Dokumen `< 220` kata (`<= 2` halaman) memakai narasi eksekutif proporsional sehingga rasio pemadatan `SK-01..SK-05` meningkat dari `0.0%` menjadi **`38.0% – 58.0%`** (`SK-01` mencatat **`49.4%`**, `154 -> 78` kata).

### 3. Perbaikan Hotfix UAT HP Fisik (`fix/pdf-to-image-overlap-and-history-back`)
- **Scrollability & Layout `PDF to Image` (`PdfToImageScreen.kt`):** Menambahkan `rememberScrollState()`, `.verticalScroll(...)`, dan `.imePadding()` pada kontainer utama serta merapikan spasi vertikal agar baris `"Pilih lokasi manual (Save As)"` + switch tampil utuh di atas tombol `"Konversi ke ZIP"` dan tidak terpotong di layar standar maupun skala font besar.
- **Tombol Kembali ke Beranda pada Riwayat (`HistoryScreen.kt` & `NavGraph.kt`):** Menambahkan `navigationIcon` kembali ke Beranda pada `TopAppBar` layar `"Riwayat Konversi"`, tombol `Kembali ke Beranda` pada kondisi kosong, dan pembersihan stack menuju `Screen.Dashboard.route`.

---

## Hasil Verifikasi Rilis `v1.5.0`

- `./gradlew testDebugUnitTest`: **`26 / 26` PASS** (`0 failed`, `0 error`, `0 skipped` — `SpoPatternAndRuleEngineTest` 10, `TextExtractionCleanerAndPdfExtractorTest` 5, `FactBasedSummarizerTest` 4, `NlpLexiconAndPreprocessorTest` 3, `HistorySearchAndBatchDeleteTest` 3, `ExampleUnitTest` 1)
- `./gradlew lintDebug`: **`0 errors`** (`48` findings level non-error/warning/hint)
- `./gradlew assembleDebug`: **PASS** -> `VelaPDF-v1.5.0-debug.apk` (`76,877,049` byte, `SHA256: 3A1BFD02360C321A6D5E791015190F16E740FD6F8E26D5C6698C4959BB8601E9`) — *siap pasang langsung di HP Android*
- `./gradlew assembleRelease`: **PASS** -> `VelaPDF-v1.5.0-release-unsigned.apk` (`68,282,231` byte, `SHA256: 81C037F4B7F62C14FE66E9717973C7187AF6C62D7D6FC28E9EF63FD2F90049FF`) — *unsigned, perlu tanda tangan keystore release bila dipakai produksi*
- **Benchmark Emulator Offline (`TestbedPipelineBenchmarkTest`, 17 PDF):** **`3 / 3` PASS** (`29.467s`, `Tes_untuk_Real_Phone`, Airplane Mode ON)

---

## Cara Unggah Berkas APK ke GitHub Releases

Karena `gh` CLI / token API GitHub belum terpasang di mesin lokal ini:

### Untuk Rilis Baru `v1.5.0`:
1. Buka: `https://github.com/useripx/velapdfv1/releases/new?tag=v1.5.0`
2. Isi judul rilis **`VelaPDF v1.5.0 — Hybrid iText7+OCR, UI Polish & Hotfix`** dan salin isi dokumen ini ke deskripsi rilis.
3. Seret & lepas (*drag-and-drop*) berkas **`release/VelaPDF-v1.5.0-debug.apk`** *(siap pasang di Android)* ke kotak lampiran (*Attach binaries*), lalu klik **Publish release**.

### Untuk Melengkapi Lampiran APK pada Rilis Sebelumnya `v1.4.0`:
1. Buka: `https://github.com/useripx/velapdfv1/releases/edit/v1.4.0`
2. Salin isi `aturan/Docs/Release_Notes_v1.4.0.md` jika ingin melengkapi deskripsi, lalu lampirkan APK dan klik **Update release**.
