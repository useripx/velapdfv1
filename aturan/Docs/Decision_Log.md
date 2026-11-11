# Decision Log (ADR) — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** CATATAN KEPUTUSAN ARSITEKTUR AKTIF (ACTIVE ARCHITECTURE DECISION RECORD)  
> **DESIGNATED APPROVER:** Yogi Ario (`@useripx` — rujuk [`config/organization.md`](../config/organization.md))  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TERAKHIR DIPERBARUI:** 11/11/2026

---

## Indeks Rekaman Keputusan

| ID Keputusan | Tanggal | Topik / Fitur | Keputusan Singkat | Status |
| :---: | :---: | :--- | :--- | :---: |
| **`ADR-001`** | 2026-11-11 | Arsitektur & Fokus Fitur | Pertahankan **MVVM + Clean Architecture** (100% Compose, Hilt, Room `2.8.5`, DataStore); hapus permanen modul KHS | **APPROVED** |
| **`ADR-002`** | 2026-11-11 | Strategi Git, `velapdfv1.git` & Keamanan | Folder `aturan/` **mentok di `develop`** (`main` bersih 100%); `app/google-services.json` di-`.gitignore`; riwayat `velapdfv1.git` bersih | **APPROVED** |
| **`ADR-003`** | 2026-11-11 | Lingkup OCR & AI Offline (`ASK Vela`) | Fokus **Ringkasan & Kesimpulan Bahasa Indonesia** via *Rule-Based Inference (Forward Chaining)* tanpa Q&A interaktif/Cloud LLM | **APPROVED** |

---

## Rincian Rekaman Keputusan Aktif

### `[ADR-001]` — Pertahankan Stack Eksisting (MVVM + Clean Architecture + Compose + Hilt) & Penghapusan Permanen KHS

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-11
- **Pengusul:** Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Dokumentasi aturan awal (`aturan/`) hanya menyebut MVVM umum dan template lama, sementara codebase VelaPDF memakai **MVVM + Clean Architecture** (`data/`, `domain/`, `di/`, `ui/`, `utils/`) dengan Compose BOM `2026.09.00`, Hilt `2.60.1`, Room `2.8.5`, Kotlin `2.4.20`, AGP `9.4.1`, dan Gradle `9.8.0`. Di samping itu, sisa fitur `KHS` (`PreviewKhsScreen`, `KhsExporter`) telah dihapus di working tree sebelum serah terima.

#### 2. Keputusan & Alasan
- Menjaga 100% arsitektur, versi SDK (`minSdk 26`, `targetSdk 36`, `compileSdk 37`), dan stack Compose/Hilt tanpa perombakan.
- Menyelaraskan dokumen di `aturan/` (`AGENTS.md`, `GEMINI.md`, `Android_Kotlin_MVVM_Rules.md`) agar mengikuti codebase aktual.
- Menghapus modul KHS secara permanen agar pengembangan fokus pada utilitas PDF serta OCR & Analisis Dokumen AI offline.

---

### `[ADR-002]` — Strategi Clean `main` Strict (`aturan/` Mentok di `develop`) & Keamanan `google-services.json`

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-11
- **Pengusul:** Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Repositori lama masih menunjuk `PanjaluPDF.git` dengan 183 perubahan menggantung, crash logs JVM, duplikat `app/gs.json`, serta konfigurasi Firebase `app/google-services.json` di history lama. Pemilik proyek meminta pindah ke repo baru `https://github.com/useripx/velapdfv1.git` dengan kondisi `main` super bersih dan `aturan/` mentok di `develop`.

#### 2. Keputusan & Alasan
- Mengalihkan remote ke `https://github.com/useripx/velapdfv1.git`.
- Menghapus `app/gs.json`, log crash root, `velapdf_template/`, serta 5 stub Kotlin kosong tanpa referensi.
- Mengeluarkan `app/google-services.json` dari index Git sambil mempertahankannya di disk lokal & melindunginya di `.gitignore`.
- Memisahkan fungsi branch: `main` hanya berisi kode proyek Android VelaPDF bersih (**0% `aturan/`**), sedangkan `develop` menyimpan kode integrasi beserta folder `aturan/` (`Docs/`, `alatuji/`, `changelog/`, `config/`).

---

### `[ADR-003]` — Ruang Lingkup Analisis Dokumen AI (`ASK Vela` & `Ringkasan` Bahasa Indonesia Luring)

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-11
- **Pengusul:** Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Terdapat kebutuhan penegasan batas fungsionalitas AI offline agar sesuai dengan arah penelitian (Sempro) dan arsitektur *Rule-Based Inference*.

#### 2. Keputusan & Alasan
- **ASK Vela** dan aksi **Ringkasan** pasca-scan beroperasi **100% offline** via Google ML Kit OCR + NLP + Ontologi JSON + *Forward Chaining*.
- Keluaran berupa **ringkasan poin inti & kesimpulan dalam Bahasa Indonesia saja**, tanpa fitur tanya jawab interaktif (*Q&A*), terdokumentasi resmi di [`batasan_masalah.md`](batasan_masalah.md).
