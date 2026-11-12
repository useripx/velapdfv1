# Implementation Plan — Tahap 03: Uji Perangkat Fisik (Infinix Note 30 + Kamera Langsung), Polish UI Visual & Akurasi Ringkas Surat Pendek, Serta Persiapan Rilis

> **STATUS:** MENUNGGU PERSETUJUAN YOGI ARIO  
> **ATURAN RILIS GITHUB:** Jika setelah menguji hasil Tahap 02 di HP **Infinix Note 30** (panduan: [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md)) Anda mengomentari rencana ini dengan **`buatkan rilis`**, Carl akan merilis hasil Tahap 02 terlebih dahulu ke GitHub (`versionCode = 4`, `v1.4.0`, `main` bersih tanpa `aturan/`), baru kemudian mengerjakan Tahap 03 di bawah ini (`target berikutnya: v1.5.0`).  
> **NOMOR TAHAPAN:** Tahap 03 (Physical Hardware Verification, UI/NLP Polish & Pre-Release Integration)  
> **PROYEK:** VelaPDF (`com.njagakneai.velapdf` — saat ini `versionCode = 3` / `"1.3 (Beta Revision 3)"`)  
> **PEMILIK PROYEK:** Yogi Ario Pratama (`@useripx`)  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL:** 12/11/2026  
> **TARGET BRANCH:** `feature/03_uji_perangkat_fisik_dan_polish`  
> **PRASYARAT SELESAI:** Tahap 00 (`develop`), Tahap 01 (`feature/01_ocr_dan_ask_vela`), & Tahap 02 (`feature/02_evaluasi_testbed_dan_ui`)

---

## 1. Latar Belakang & Tujuan Tahap 03

Berdasarkan hasil verifikasi **Tahap 02** pada emulator `Tes_untuk_Real_Phone`, mesin AI 4-JSON, pencarian/hapus massal riwayat, dan 17 PDF testbed telah lulus **100%** tanpa *crash*. Untuk melangkah ke Tahap 03:
1. **Validasi Kamera Langsung & Testbed di HP Fisik Nyata (`Infinix Note 30`):** Memotret dokumen buku fisik & surat cetak secara nyata melalui *ML Kit Document Scanner* dalam Airplane Mode untuk melengkapi data uji Bab IV (perbandingan latensi ARM64 **Infinix Note 30** vs Emulator x86_64; catatan dokumen baseline proposal `testbed_hardware_software.md` tetap dijaga SHA-256-nya).
2. **Polish Kontras UI (`[OBS-UI-001]`) & Kompresi Surat Pendek (`[OBS-NLP-001]`):** Memperbaiki kontras ikon kotak menu `AskVelaScreen`, pill progress `"Analisis AI 100% Luring"`, pill `"Pemadatan Teks"`, keterpotongan teks hint pencarian pada layar sempit, dan strategi narasi ringkas pada dokumen sangat pendek (`< 200` kata seperti `SK-01`).
3. **Finalisasi Rilis `v1.5.0` & Bahan Skripsi Bab IV:** Menjalankan verifikasi penuh, membuat checklist pasca-tahap untuk Infinix Note 30, dan mempersiapkan paket tabel Bab IV Skripsi.

---

## 2. Rincian Paket Kerja (Work Packages)

### Paket 1 — Perbaikan Visual Kontras UI (`[OBS-UI-001]`) & Adaptasi Surat Pendek (`[OBS-NLP-001]`)
- **Kontras Ikon & Pill ([`AskVelaScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/AskVelaScreen.kt), [`DocumentSummaryScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/DocumentSummaryScreen.kt)):**
  - Pastikan warna ikon di dalam kotak gelap `Color(0xFF111827)` pada `Unggah Berkas PDF`, `Pindai Kamera`, `Pilih Gambar`, dan pill `"Analisis AI 100% Luring"` memakai tint kontras tinggi (`Color.White`), serta latar kartu info atas & pill `"Pemadatan Teks"` memiliki kontras teks WCAG >= 4.5:1.
  - Persingkat teks placeholder search bar (mis. `"Cari judul, topik, atau jenis..."`) agar tidak terpotong elipsis pada lebar `1280px` @ `480dpi`.
- **Narasi Adaptif Dokumen Pendek ([`FactBasedSummarizer.kt`](../../app/src/main/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizer.kt)):**
  - Pada dokumen `< 200` kata (seperti `SK-01` 152 kata), padatkan kalimat pembuka/penutup narasi atau ambil inti diktum/perihal secara ringkas agar jumlah kata ringkasan tetap <= jumlah kata sumber (`Pemadatan Teks > 0%`).
  - Uji unit baru pada [`FactBasedSummarizerTest.kt`](../../app/src/test/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizerTest.kt) guna memastikan rasio kompresi positif pada naskah formal singkat.

### Paket 2 — Uji Perangkat Fisik Nyata (`Infinix Note 30` via ADB USB Debugging / Manual UAT)
- Hubungkan **Infinix Note 30** milik Yogi Ario (aktif **Airplane Mode ON**), lalu:
  1. Jalankan [`TestbedPipelineBenchmarkTest`](../../app/src/androidTest/java/com/njagakneai/velapdf/TestbedPipelineBenchmarkTest.kt) (`17 PDF`) di **Infinix Note 30** (ARM64) untuk mencatat perbandingan angka `ocr_ms`, `nlp_inf_ms`, `java_heap`, `native_heap`, dan `total_pss` vs Emulator x86_64.
  2. Uji langsung **Alur Kamera Fisik**: pindai halaman buku cetak (mis. buku IoT / materi kuliah) memakai *ML Kit Document Scanner* -> periksa/edit via [`OcrPreviewScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/OcrPreviewScreen.kt) -> tekan **`"Buat Ringkasan"`** -> ekspor PDF -> verifikasi keterbacaan & simpan bukti tangkapan layar di `aturan/Docs/Evidence/SS/valid/real_device/`.

### Paket 3 — Rekap Bab IV, Checklist HP & Persiapan Rilis Tahap 03
- Susun rekap tabel perbandingan **Emulator x86_64 vs Infinix Note 30 (ARM64)** ke [`Test_Report.md`](Test_Report.md), `aturan/alatuji/06_pemetaan_tugas_akademik/`, & **`aturan/simpan/tahap3.md`**.
- Jalankan ulang gerbang wajib (`testDebugUnitTest`, `lintDebug`, `assembleDebug`, `connectedDebugAndroidTest`).
- Perbarui [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) bila ada poin pengujian baru di Tahap 03 agar Anda mudah mengetes sebelum meminta rilis **`v1.5.0`**.

---

## 3. Verifikasi & Kriteria Selesai (Tahap 03)
1. **Unit Test & Lint:** `testDebugUnitTest` (termasuk tes kompresi dokumen pendek) 100% `PASS`, `lintDebug` = `0 errors`.
2. **Perangkat Fisik Testbed & Kamera:** Pengujian pada **Infinix Note 30** dalam Airplane Mode + foto hasil pindai kamera nyata terverifikasi di `Evidence/SS/valid/real_device/` (atau dicatat bila uji USB dijalankan manual oleh Yogi memakai APK).
3. **Panduan Dosen:** `aturan/simpan/tahap3.md` + `Panduan_Belajar_Dosen.md` tersedia lengkap.

---

> 🛑 **Menunggu tanggapan Yogi Ario (`@useripx`):**  
> - Ketik **`buatkan rilis`** bila Anda sudah selesai mengetes APK Tahap 02 di **Infinix Note 30** dan ingin hasil Tahap 02 dirilis terlebih dahulu (`v1.4.0`) sebelum Tahap 03 dikerjakan; atau  
> - Berikan komentar revisi / persetujuan langsung atas rencana Tahap 03 di atas.
