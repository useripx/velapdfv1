# Panduan Belajar & Tanya Jawab Dosen — Tahap 03 (`feature/03_uji_perangkat_fisik_dan_polish`)

> **STATUS DOKUMEN:** MATERI PEMBELAJARAN & PERSIAPAN BIMBINGAN / SIDANG AKHIR TAHAP 03  
> **MAHASISWA / PENGEMBANG:** Yogi Ario Pratama (`NPM: 2313020004` / `@useripx`)  
> **DOSEN PEMBIMBING:** Rony Heri Irawan, M.Kom. (Universitas Nusantara PGRI Kediri)  
> **PARTNER AI:** Carl (`AGT-PRD-DEV-001`)  
> **PRASYARAT:** Telah mempelajari [`tahap1.md`](tahap1.md) & [`tahap2.md`](tahap2.md)  
> **TANGGAL DISUSUN:** 12 November 2026

---

## 1. Ringkasan Pencapaian Teknis Tahap 03

**Tahap 03** berfokus pada tiga aspek utama penyempurnaan mutu sebelum pengujian akhir pada perangkat fisik **Infinix Note 30**:
1. **Perbaikan Visual Kontras & Antarmuka (`[OBS-UI-001]`):** Meningkatkan keterbacaan ikon putih di atas komponen gelap pada [`AskVelaScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/AskVelaScreen.kt), [`AnalysisProgressOverlay.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/components/AnalysisProgressOverlay.kt), dan [`DocumentSummaryScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/DocumentSummaryScreen.kt), serta merapikan *hint* bilah pencarian riwayat (`"Cari judul, topik, atau jenis..."`).
2. **Narasi Adaptif Dokumen Pendek (`[OBS-NLP-001]`):** Memperbaiki logika panjang ringkasan di [`FactBasedSummarizer.kt`](../../app/src/main/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizer.kt) untuk naskah `< 220` kata sehingga surat singkat seperti `SK-01` s.d. `SK-05` tetap mengalami pemadatan nyata (**`38.0% – 58.0%`**, tidak lagi `0.0%`).
3. **Arsitektur Ekstraksi Teks Hibrida Per-Halaman (`Paket 1B` + Fix Android XML Parser — [`ADR-006`](../Docs/Decision_Log.md)):**
   - PDF digital dibaca langsung via **iText7 `PdfTextExtractor`** (`SimpleTextExtractionStrategy` + cadangan `LocationTextExtractionStrategy`), menghasilkan presisi karakter ~100% (misal nomor `112/SK/UNP-KD/X/2026` utuh sempurna).
   - Halaman gambar/scan (misal halaman 11–16 `Roadmap.pdf` dengan `streamLen = 0`) otomatis melakukan *fallback* ke **`PdfRenderer` + Google ML Kit OCR** hanya pada halaman bersangkutan (`HYBRID`), dibersihkan tanda hubung terpenggal baris (`-\n`) serta diurutkan kolom spasialnya via [`TextExtractionCleaner.kt`](../../app/src/main/java/com/njagakneai/velapdf/domain/nlp/TextExtractionCleaner.kt).
   - Mendaftarkan `AndroidSafeXmlParserFactory` ke `XmlProcessorCreator` di [`MlKitRepository.kt`](../../app/src/main/java/com/njagakneai/velapdf/data/ocr/MlKitRepository.kt) untuk mencegah `UnsupportedOperationException (setXIncludeAware)` dari parser XML bawaan runtime Android ART.

---

## 2. Dampak Terukur (Data Empiris untuk Bab IV Skripsi)

Data benchmark emulator offline (**Airplane Mode ON**, [`benchmark_results_tahap3.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap3.csv)) mencatat peningkatan ekstrem setelah arsitektur hibrida diaktifkan:
- **Waktu Eksekusi Seluruh 17 PDF Uji (`TestbedPipelineBenchmarkTest`):** Turun dari **`344.329 detik`** (Tahap 02) menjadi **`29.467 detik`** (Tahap 03 — **~11.7x lebih cepat**).
- **Percepatan per Kategori Dokumen:**
  - Surat Resmi 1 Halaman (`SK-01..05`): dari `2.2–3.4 detik` menjadi **`49–57 ms`** (**~45–69x lebih cepat**).
  - Jurnal Ilmiah 8–10 Halaman (`12`, `13`, `14`): dari `62–76 detik` menjadi **`2.8–4.9 detik`** (**~12.6–22.9x lebih cepat**).
  - PDF Campuran Teks + Scan Gambar (`Roadmap.pdf`, 19 hal.): otomatis mendeteksi mode **`HYBRID`** (`11.19 detik`, vs `44.56 detik` sebelumnya).
- **Konsumsi Native Heap:** Turun drastis dari `88.96–151.19 MB` (saat model TFLite ML Kit harus dimuat untuk semua PDF) menjadi hanya **`6.12–12.87 MB`** pada dokumen PDF digital native.

---

## 3. Bank Pertanyaan & Jawaban Dosen — Tahap 03 (`Q31` s.d. `Q36`)

**Q31. Mengapa aplikasi Anda sekarang menggabungkan iText7 untuk teks PDF dengan ML Kit OCR? Bukankah cukup pakai ML Kit OCR saja?**  
**Jawaban:** Merender PDF digital (yang sebenarnya sudah menyimpan aliran karakter PDF bersih) menjadi gambar bitmap lalu memindainya lagi dengan OCR optik memboroskan komputasi sekaligus rentan kesalahan karakter kecil seperti hilangnya tanda `/` pada nomor SK (`UNP-KD/X2026`). Pendekatan hibrida membaca teks digital secara presisi via **iText7** (`NATIVE_ITEXT7`), namun tetap mempertahankan **ML Kit OCR** sebagai jaring pengaman otomatis (*fallback* per-halaman) saat menghadapi halaman hasil scan gambar (seperti halaman 11–16 `Roadmap.pdf` $\rightarrow$ `HYBRID`) maupun foto kamera langsung. Hasil ujinya menurunkan latensi 17 PDF hampir 12 kali lipat (`344s` menjadi `29.47s`).

**Q32. Saat Anda memasukkan iText7 ke Android, kendala teknis apa yang sempat muncul dan bagaimana mengatasinya?**  
**Jawaban:** Saat `iText7 9.8.0` membuka PDF bermetadata XMP di runtime Android ART, parser XML bawaan Android (`DocumentBuilderFactory` berbasis Expat/Harmony) tidak mendukung metode `setXIncludeAware(false)` dan melempar `UnsupportedOperationException`. Kami menanganinya dengan membuat `AndroidSafeXmlParserFactory` turunan `DefaultSafeXmlParserFactory` yang mendaftarkan konfigurasi aman via `XmlProcessorCreator.setXmlParserFactory(...)`. Hal tersebut tervalidasi di emulator tanpa regresi (`26/26` unit test + `3/3` instrumentation test PASS).

**Q33. Bagaimana cara sistem membedakan teks PDF digital yang sehat dengan PDF scan/rusak sebelum memutuskan pakai OCR?**  
**Jawaban:** Di dalam [`TextExtractionCleaner.kt`](../../app/src/main/java/com/njagakneai/velapdf/domain/nlp/TextExtractionCleaner.kt), fungsi `isMeaningfulNativePdfText(...)` dan `chooseBestNativeExtraction(...)` memeriksa: (a) panjang karakter non-spasi minimum, (b) rasio karakter rusak `U+FFFD` (`<= 3%`), (c) deteksi ToUnicode/CMap rusak berpola spasi per-huruf tunggal (`> 45%` token 1 huruf), (d) kata berhuruf vokal minimum, serta (e) deteksi kata lengket ekstrem tanpa spasi (`hasConcatenatedWordGlitches`) dengan pembanding `LocationTextExtractionStrategy`. Jika gagal semua, halaman tersebut dialihkan otomatis ke `PdfRenderer + ML Kit OCR`.

**Q34. Mengapa kata yang terputus tanda hubung di akhir baris seperti *"pem-\nrosesan"* bisa bersih tanpa merusak kata ulang Bahasa Indonesia seperti *"undang-\nundang"*?**  
**Jawaban:** Fungsi `normalizeExtractedPageText` memeriksa kata majemuk/reduplikasi secara sederhana via pencocokan akar (*case-insensitive*): jika potongan sebelah kiri dan kanan memiliki bentuk sama persis (atau kanan diawali kata kiri), tanda hubungnya dipertahankan (*"undang-undang"*); jika berbeda (*"pem-" + "rosesan"*), tanda hubung dan pindah baris dihapus lalu digabung mulus menjadi *"pemrosesan"*.

**Q35. Sebelumnya surat resmi `SK-01` mencatat `Pemadatan Teks 0.0% (152 -> 182 kata)`. Bagaimana perbaikannya di Tahap 03?**  
**Jawaban:** Pada [`FactBasedSummarizer.kt`](../../app/src/main/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizer.kt), batas jumlah poin & template narasi disesuaikan proporsional terhadap panjang naskah. Untuk dokumen pendek (`< 220` kata / `<= 2` halaman), narasi memakai format eksekutif ringkas sehingga `SK-01` terkompresi dari `154` kata menjadi `78` kata (**`49.4%`**), dan seluruh `SK-01` s.d. `SK-05` mencatat rasio pemadatan **`38.0% – 58.0%`**.

**Q36. Apakah salah baca OCR karena foto kamera yang gelap, buram, atau tulisan tangan masih dianggap batasan masalah?**  
**Jawaban:** Ya, sesuai [`batasan_masalah.md`](../Docs/batasan_masalah.md) dan [`Decision_Log.md`](../Docs/Decision_Log.md) (`ADR-006`). Ekstraksi digital PDF diselesaikan secara penuh lewat perangkat lunak (`iText7` + *spatial ordering*), sementara kualitas optik foto kamera mentah, tulisan tangan, maupun tabel kompleks dimitigasi secara UX melalui fasilitas peninjauan dan koreksi manual pada layar **"Edit OCR"** sebelum inferensi dijalankan.
