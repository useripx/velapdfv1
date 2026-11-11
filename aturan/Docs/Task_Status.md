# Status Tugas & Fase Proyek — VelaPDF

> **PROYEK:** VelaPDF (`com.njagakneai.velapdf` — `v1.3 Beta Revision 3`)  
> **PEMILIK PROYEK:** Yogi Ario Pratama (`@useripx`)  
> **PERSONA PENGEMBANG / PENGAWAS:** Carl (`AGT-PRD-DEV-001`)  
> **REPO TARGET:** `https://github.com/useripx/velapdfv1.git`  
> **TERAKHIR DIPERBARUI:** 11/11/2026 (`10:02 AM WIB`)

---

## 1. Ringkasan Status Fase Pengembangan

| Kode Fase | Nama Fase / Cakupan Utama | Branch Target | Status | Bukti Verifikasi Utama |
| :--- | :--- | :--- | :---: | :--- |
| **Tahap 00** | Handover Bootstrap, Pembersihan Sampah/Stub, Penyelarasan `aturan/`, Salin `alatuji/`, `batasan_masalah.md`, Perbaikan Baseline Lint (`DEF-LEG-009`), & Inisialisasi Git Bersih (`main` + `develop`) | `develop` & `main` | **COMPLETED (100% PASS)** | `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` -> `BUILD SUCCESSFUL` (`0` errors, `1` test PASS) |
| **Tahap 01** | Rekonstruksi Pipeline **Offline OCR + Rule-Based Inference (Bahasa Indonesia)**: `PdfRenderer` & multi-page OCR (`MlKitRepository`), Ontologi & Aturan Buku/Dokumen Resmi, Tombol **"Ringkasan"** pasca-scan/edit, Menu **"ASK Vela"**, Room Migration `v1 -> v2`, Unit Test Lengkap | `feature/01_ocr_dan_ask_vela` -> `develop` | **WAITING APPROVAL** | Menunggu persetujuan *Chained Implementation Plan Tahap 1* & konfirmasi 3 poin UX |
| **Tahap 02** | Pengujian Komparatif Akademik (`SK-01`..`SK-05` + Sampel Buku IoT) & Uji Stabilitas / Memori ($\le 50\text{ MB}$) *(Rencana Lanjutan)* | `feature/02_evaluasi_testbed` -> `develop` | **PLANNED** | Memakai testbed [`aturan/alatuji/`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/aturan/alatuji) |

---

## 2. Daftar Pekerjaan Aktif Saat Ini

- **Selesai di Tahap 00:**
  - Penghapusan permanen `app/gs.json`, `hs_err_pid*.log`, `replay_pid*.log`, `velapdf_template/`, dan 5 stub Kotlin mati.
  - Pengamanan [`.gitignore`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/.gitignore) untuk `app/google-services.json` (tetap ada di disk lokal, tidak masuk ke GitHub).
  - Pembuatan [`batasan_masalah.md`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/aturan/Docs/batasan_masalah.md) & salinan testbed [`aturan/alatuji/`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/aturan/alatuji).
  - Penyelesaian `DEF-LEG-004` (5 stub mati) & `DEF-LEG-009` (2 error lint pada [`HistoryScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/HistoryScreen.kt#L98) dan [`AndroidManifest.xml`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/AndroidManifest.xml#L5)).
  - Inisialisasi branch `main` bersih (tanpa `aturan/`) dan `develop` (dengan `aturan/` & `alatuji/`).
- **Berikutnya (Menunggu Otorisasi Yogi Ario):**
  - Konfirmasi 3 pilihan UX dari [`batasan_masalah.md`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/aturan/Docs/batasan_masalah.md#L44-L49) + persetujuan rencana teknis **Tahap 01** pada [`Implementation_plan.md`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/aturan/Docs/Implementation_plan.md).
