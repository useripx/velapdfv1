# Implementation Plan — Tahap 04: Finalisasi Data Bab IV (Black-Box, SUS & Opsional Benchmark ARM64 USB), Audit Sisa UI/Legacy, & Persiapan Rilis Penutup (`v1.6.0`)

> **STATUS:** **MENUNGGU PERSETUJUAN (`Proceed`)** — Belum ada perubahan kode Tahap 04 yang dieksekusi.  
> **NOMOR TAHAPAN:** Tahap 04 (Final Skripsi Data Package, Residual UI/Checklist Verification & Optional Release Automation)  
> **PROYEK:** VelaPDF (`com.njagakneai.velapdf` — saat ini rilis `versionCode = 5` / `"1.5.0"`)  
> **PEMILIK PROYEK:** Yogi Ario Pratama (`@useripx`)  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL:** 12/11/2026  
> **TARGET BRANCH:** `feature/04_finalisasi_bab4_dan_audit_akhir` (berbasis `develop`)  
> **PRASYARAT SELESAI:** Tahap 00, Tahap 01, Tahap 02 (`v1.4.0`), & Tahap 03 + Hotfix UI (`v1.5.0`)

---

## 1. Latar Belakang & Tujuan Tahap 04

Dengan dirilisnya **VelaPDF `v1.5.0`**, seluruh mesin inti AI Luring (`4-JSON`, Hybrid `iText7` + fallback ML Kit OCR, narasi adaptif surat pendek, UI kontras tinggi, scrollability `PDF to Image`, dan navigasi kembali di `Riwayat`) telah berjalan stabil di emulator maupun di **Infinix Note 30**. Untuk menutup sisa ~10% proyek menuju kelulusan **100% siap bimbingan/sidang skripsi & rilis akhir**:

1. **Paket Evaluasi & Naskah Bab IV Skripsi (Berdasarkan `pemetaan_ke_tugas_4.md`):** Data uji empiris Tahap 02 vs Tahap 03 (`344.329s -> 29.467s`), pengujian fungsional *Black-Box* formula `(2.1)` ($P_{valid}$), serta instrumen evaluasi *System Usability Scale* (SUS) 10 butir formula `(2.2)`–`(2.5)` perlu dirangkum ke dalam laporan evaluasi Bab IV yang siap disalin ke naskah skripsi beserta panduan tanya-jawab sidang `aturan/simpan/tahap4.md`.
2. **Penuntasan Uji HP Fisik (`Infinix Note 30`) & Opsional Benchmark Hardware ARM64 via USB:** Masih terdapat 5 butir detail opsional pada [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) (`L51` kartu Celah D, `L52` accordion SPO/Rules, `L53` negasi `"tidak wajib"`, `L62` persistensi swipe-up, `L71` akurasi & kecepatan `SK-01`) serta pencatatan opsional metrik ARM64 fisik melalui kabel USB ADB untuk melengkapi perbandingan Emulator x86_64 vs Smartphone fisik.
3. **Audit Konsistensi Layar Utilitas PDF Lain (Opsional):** Memastikan layar utilitas PDF lain (`ImageToPdfScreen.kt`, `MergePdfScreen.kt`) juga aman dari pemotongan/tumpang tindih pada skala font besar serta mengevaluasi penyambungan ikon riwayat (`/* TODO History */`) bila dibutuhkan.
4. **Otomasi Upload GitHub Release + APK (Opsional):** Menyediakan jalur otomatis agar rilis berikutnya (`v1.6.0`) dapat melampirkan berkas `.apk` ke GitHub Releases tanpa unggah manual.

---

## 2. Rincian Paket Kerja yang Diajukan

### Paket 4A — Paket Finalisasi Bab IV, Black-Box Testing Aktual & Instrumen SUS *(Direkomendasikan Wajib)*
1. Menyusun dokumen rekap ilmiah **`aturan/alatuji/06_pemetaan_tugas_akademik/laporan_bab4_hasil_pengujian.md`**:
   - Tabel perbandingan komprehensif **Tahap 02 (Pure ML Kit OCR)** vs **Tahap 03 (`v1.5.0` Hybrid `iText7` + ML Kit Fallback)** atas 17 dokumen testbed (waktu, heap Java/Native, mode ekstraksi, dan rasio pemadatan).
   - Tabel aktual **Black-Box Testing** formula `(2.1)` mencakup alur `ASK Vela` (`SK-01..05`, Jurnal `12..14`, `Roadmap.pdf`, Edit OCR, Rentang Halaman, Ekspor PDF, Pencarian/Hapus Riwayat) serta utilitas PDF (`Image to PDF`, `Merge PDF`, `PDF to Image` mode ZIP, `Riwayat`) + bukti screenshot.
   - Paket kuesioner **SUS 10 Butir** siap sebar ke responden beserta kalkulator/template tabel perhitungan skor formula `(2.2)`–`(2.5)`.
2. Menyusun **`aturan/simpan/tahap4.md`** & memperbarui [`Panduan_Belajar_Dosen.md`](Panduan_Belajar_Dosen.md) dengan rangkuman arsitektur keseluruhan & bank tanya-jawab kritis Sidang Akhir.

### Paket 4B — Uji Perangkat Fisik `Infinix Note 30` via USB & Penuntasan 5 Poin Checklist *(Opsional sesuai koneksi USB)*
1. Jika Anda menyambungkan **Infinix Note 30** ke PC melalui kabel USB Debugging (`adb devices` terdeteksi):
   - Menjalankan `TestbedPipelineBenchmarkTest` pada hardware fisik ARM64 (**Airplane Mode ON**) dan mencatat perbandingan latensi + memori ARM64 vs Emulator x86_64 ke Bab IV.
   - Mengambil screenshot kamera dokumen fisik nyata & verifikasi 5 poin checklist tersisa (`L51`, `L52`, `L53`, `L62`, `L71`).
2. Jika HP **tidak** disambungkan ke PC: cukup Anda laporkan hasil uji manual 5 poin checklist tersisa di HP untuk dicatat jujur ke [`Checklist_Uji_HP_Infinix_Note30.md`](Checklist_Uji_HP_Infinix_Note30.md) & [`Test_Report.md`](Test_Report.md).

### Paket 4C — Audit UI Utilitas Legacy & Kebersihan Ringan *(Opsional)*
1. Memeriksa [`ImageToPdfScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/ImageToPdfScreen.kt) & [`MergePdfScreen.kt`](../../app/src/main/java/com/njagakneai/velapdf/ui/screen/MergePdfScreen.kt) untuk memastikan bebas clipping pada `font_scale 1.25x` / layar pendek.
2. Pilihan mengaktifkan ikon Riwayat pada TopAppBar (`/* TODO History */`) di `ImageToPdfScreen`, `MergePdfScreen`, & `PdfToImageScreen` agar konsisten dengan `DashboardScreen`.
3. Opsi menangani atau tetap mengisolasi temuan legacy ringan di [`Defect_Log.md`](Defect_Log.md) (`DEF-LEG-001` operator `!!` pada `PdfToImage` & `DEF-LEG-008` izin `READ_MEDIA_VIDEO`).

### Paket 4D — Jalur Otomasi Unggah APK GitHub Releases *(Opsional)*
1. **Opsi 1 (`gh` CLI Lokal):** Panduan memasang `GitHub.cli` & `gh auth login` satu kali di Windows agar AI Agent pada rilis berikutnya (`v1.6.0`) bisa langsung memanggil `gh release create ... VelaPDF-*.apk`.
2. **Opsi 2 (GitHub Actions `.github/workflows/release.yml`):** Menambahkan workflow CI tag `v*` *(memerlukan Anda menambahkan Secret `GOOGLE_SERVICES_JSON` di pengaturan repositori GitHub karena berkas lokal tidak boleh masuk ke branch `main`)*.

---

## 3. Verifikasi & Kriteria Selesai Tahap 04
1. **Kualitas Build:** `./gradlew testDebugUnitTest lintDebug assembleDebug` lulus **100% PASS** (`0` lint errors) pasca perubahan akhir di branch `feature/04_finalisasi_bab4_dan_audit_akhir`.
2. **Kelengkapan Bukti:** Seluruh tangkapan layar UI baru/diperbaiki (bila Paket 4B/4C dijalankan) tersimpan di `aturan/Docs/Evidence/SS/valid/`.
3. **Kesiapan Skripsi:** `laporan_bab4_hasil_pengujian.md`, instrumen SUS, & `aturan/simpan/tahap4.md` tersusun lengkap dan siap dicetak/disalin ke BAB IV Skripsi.

---

> [!CAUTION]
> **🛑 Menunggu Persetujuan Yogi Ario (@useripx):**  
> Jangan sentuh kode Tahap 04 sebelum disetujui. Silakan beri komentar paket mana yang ingin dijalankan (**4A**, **4B**, **4C**, **4D**) atau klik **`Proceed`** untuk mengeksekusi rencana Tahap 04 sesuai paket rekomendasi (**4A** + **audit ringan 4C** + penuntasan checklist **4B**).
