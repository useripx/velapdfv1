# Implementation Plan — Tahap 03: Uji Perangkat Fisik (Xiaomi Redmi Note 11 + Kamera Langsung), Polish UI Visual & Akurasi Ringkas Surat Pendek, Serta Persiapan Merge `develop`

> **STATUS:** MENUNGGU PERSETUJUAN YOGI ARIO  
> **NOMOR TAHAPAN:** Tahap 03 (Physical Hardware Verification, UI/NLP Polish & Pre-Release Integration)  
> **PROYEK:** VelaPDF (`com.njagakneai.velapdf` — `v1.3 Beta Revision 3`)  
> **PEMILIK PROYEK:** Yogi Ario Pratama (`@useripx`)  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL:** 12/11/2026  
> **TARGET BRANCH:** `feature/03_uji_perangkat_fisik_dan_polish` (bercabang dari `feature/02_evaluasi_testbed_dan_ui` / pasca-persetujuan merge `develop`)  
> **PRASYARAT SELESAI:** Tahap 00 (`develop`), Tahap 01 (`feature/01_ocr_dan_ask_vela`), & Tahap 02 (`feature/02_evaluasi_testbed_dan_ui`)

---

## 1. Latar Belakang & Tujuan Tahap 03

Berdasarkan hasil verifikasi **Tahap 02** pada emulator `Tes_untuk_Real_Phone`, mesin AI 4-JSON, pencarian/hapus massal riwayat, dan 17 PDF testbed telah lulus **100%** tanpa *crash*. Namun, inspeksi langsung atas screenshot emulator serta matriks kelengkapan proposal Metopen mencatat tiga kebutuhan konkret untuk Tahap 03:
1. **Validasi Kamera Langsung di HP Fisik (Xiaomi Redmi Note 11, Snapdragon 680 / Android 13):** Memotret dokumen buku fisik & surat cetak secara nyata melalui *ML Kit Document Scanner* dalam Airplane Mode untuk melengkapi data uji Bab IV (membandingkan latensi ARM64 vs emulator x86_64).
2. **Polish Kontras UI (`[OBS-UI-001]`) & Kompresi Surat Pendek (`[OBS-NLP-001]`):** Memperbaiki kontras ikon kotak menu `AskVelaScreen`, pill progress `"Analisis AI 100% Luring"`, pill `"Pemadatan Teks"`, keterpotongan teks hint pencarian pada layar sempit, dan strategi narasi ringkas pada dokumen sangat pendek (`< 200` kata seperti `SK-01`).
3. **Finalisasi Integrasi `develop` & Bahan Skripsi Bab IV:** Setelah seluruh pengujian fisik lulus, menggabungkan berurutan `feature/01` -> `feature/02` -> `feature/03` ke `develop` dengan tetap mengunci [`aturan/`](../README.md) agar **tidak pernah** masuk ke branch publik `main`.

---

## 2. Rincian Paket Kerja (Work Packages)

### Paket 1 — Perbaikan Visual Kontras UI (`[OBS-UI-001]`) & Adaptasi Surat Pendek (`[OBS-NLP-001]`)
- **Kontras Ikon & Pill (`AskVelaScreen.kt`, `DocumentSummaryScreen.kt`):**
  - Pastikan warna ikon di dalam kotak gelap `Color(0xFF111827)` pada `Unggah Berkas PDF`, `Pindai Kamera`, `Pilih Gambar`, dan pill `"Analisis AI 100% Luring"` memakai tint kontras tinggi (`Color.White`), serta latar kartu info atas & pill `"Pemadatan Teks"` memiliki kontras teks WCAG >= 4.5:1.
  - Persingkat teks placeholder search bar (mis. `"Cari judul, topik, atau jenis..."`) agar tidak terpotong elipsis pada lebar `1280px` @ `480dpi`.
- **Narasi Adaptif Dokumen Pendek (`FactBasedSummarizer.kt`):**
  - Pada dokumen `< 200` kata (seperti `SK-01` 152 kata), padatkan kalimat pembuka/penutup narasi atau ambil inti diktum/perihal secara ringkas agar jumlah kata ringkasan tetap $\le$ jumlah kata sumber (`Pemadatan Teks > 0%`).
  - Uji unit baru pada `FactBasedSummarizerTest.kt` guna memastikan rasio kompresi positif pada naskah formal singkat.

### Paket 2 — Uji Perangkat Fisik Nyata (`Xiaomi Redmi Note 11` via ADB USB Debugging)
- Hubungkan perangkat fisik **Xiaomi Redmi Note 11** (`Snapdragon 680`, `6 GB RAM`, `Android 13`), aktifkan **Airplane Mode ON**, lalu:
  1. Jalankan `TestbedPipelineBenchmarkTest` (`17 PDF`) secara penuh di perangkat ARM64 nyata untuk mencatat perbandingan angka `ocr_ms`, `nlp_inf_ms`, `java_heap`, `native_heap`, dan `total_pss` vs Emulator x86_64.
  2. Uji manual **Alur Kamera Langsung**: pindai halaman buku cetak (mis. buku IoT / materi kuliah) memakai *ML Kit Document Scanner* -> periksa/edit via `OcrPreviewScreen` -> tekan **`"Buat Ringkasan"`** -> ekspor PDF -> verifikasi keterbacaan hasil kamera riil & simpan bukti tangkapan layar di `aturan/Docs/Evidence/SS/valid/real_device/`.

### Paket 3 — Gerbang Rilis `develop` & Paket Tabel Bab IV Skripsi
- Susun rekap tabel perbandingan **Emulator x86_64 vs Xiaomi Redmi Note 11 (ARM64)** ke `aturan/Docs/Test_Report.md`, `aturan/alatuji/06_pemetaan_tugas_akademik/`, & **`aturan/simpan/tahap3.md`**.
- Jalankan ulang gerbang wajib (`testDebugUnitTest`, `lintDebug`, `assembleDebug`, `connectedDebugAndroidTest`).
- Ajukan persetujuan akhir ke Yogi Ario (`@useripx`) sebelum merge berurutan ke `develop` (dan *cherry-pick* kode produksi bersih `app/` ke `main` apabila diputuskan rilis, **tanpa** menyertakan `aturan/`).

---

## 3. Verifikasi & Kriteria Selesai (Tahap 03)
1. **Unit Test & Lint:** `testDebugUnitTest` (termasuk tes kompresi dokumen pendek) 100% `PASS`, `lintDebug` = `0 errors`.
2. **Perangkat Fisik Testbed & Kamera:** `connectedDebugAndroidTest` `100% PASS` di **Xiaomi Redmi Note 11** dalam Airplane Mode + foto hasil pindai kamera nyata terverifikasi di `Evidence/SS/valid/real_device/`.
3. **Panduan Dosen:** `aturan/simpan/tahap3.md` + `Panduan_Belajar_Dosen.md` tersedia lengkap.

---

> 🛑 **Menunggu persetujuan Yogi Ario (`@useripx`) untuk melaksanakan Rencana Tahap 03 (atau instruksi terlebih dahulu untuk menggabungkan `feature/01` & `feature/02` ke `develop`).**
