# LAPORAN PENGHENTIAN SKILL (SKILL DEPRECATION & ARCHIVE REPORT)

> **STATUS:** TEMPLATE OPERASIONAL  
> **KODE DOKUMEN:** RPT-SKL-DEP-001  
> **PENGGUNAAN:** Prosedur formal penghentian (*deprecation*) dan pengarsipan modul skill yang usang atau tidak aman  
> **OTORISASI:** Yogi Ario (Project Owner)  

---

## 1. Metadata Penghentian Skill (Deprecation Metadata)

| Parameter | Keterangan / Nilai |
| :--- | :--- |
| **ID Penghentian (Deprecation ID)**| `DEP-SKL-[CAT]-[YYYYMMDD]-[NUM]` |
| **Target Skill ID** | `SKL-[CAT]-[CODE]-[NUM]` |
| **Nama Skill** | [Nama resmi modul skill] |
| **Versi Terakhir Aktif** | `v[MAJOR].[MINOR].[PATCH]` |
| **Agen Pengusul Penghentian** | [Nama & Peran Agen Pengusul] |
| **Tanggal Pengajuan** | [DD/MM/YYYY] |
| **Target Tanggal Nonaktif (Sunset)**| [DD/MM/YYYY] |

---

## 2. Alasan Penghentian (Rationale for Deprecation)

Pilih salah satu atau lebih alasan berikut:
- [ ] **Tergantikan oleh Skill Baru yang Lebih Unggul:** Muncul arsitektur skill baru dengan performa dan cakupan lebih baik.
- [ ] **Alat / Dependensi Eksternal Usang:** Tool teknis, CLI, atau MCP Server yang menjadi prasyarat tidak lagi didukung.
- [ ] **Celah Keamanan Fatal:** Ditemukan kerentanan keamanan yang tidak dapat dimitigasi secara efisien tanpa perombakan total.
- [ ] **Perubahan Regulasi / Standar Tata Kelola:** Prosedur skill tidak lagi relevan dengan arah arsitektur sistem.

**Penjelasan Rinci:**  
[Deskripsikan latar belakang dan fakta objektif yang mendasari keputusan penghentian ini]

---

## 3. Identifikasi Skill Pengganti & Panduan Migrasi (Successor & Migration)

- **ID Skill Pengganti:** `SKL-[CAT]-[CODE]-[NEW]` (Versi: `v[...]`)
- **Nama Skill Pengganti:** [Nama modul skill pengganti]
- **Panduan Singkat Migrasi:**  
  [Jelaskan bagaimana alur kerja dan agen yang selama ini menggunakan skill lama dapat beralih ke skill baru tanpa hambatan]
- **Perbedaan Skema Input-Output Utama:**  
  [Rangkum perbedaan parameter yang wajib diperhatikan oleh pengguna]

---

## 4. Analisis Dampak & Alur Kerja Terdampak (Impact Analysis)

| Alur Kerja / Agen Terdampak | Tingkat Ketergantungan | Rencana Penyesuaian |
| :--- | :---: | :--- |
| [Nama Alur Kerja / Fitur] | Tinggi / Sedang / Rendah | [Langkah transisi ke skill pengganti] |
| [Peran Agen Pengguna] | Sering / Jarang | [Sosialisasi dan pembaruan direktif agen] |

---

## 5. Garis Waktu Penghentian Bertahap (Sunsetting Timeline)

1. **Tahap 1 — Pemberitahuan Deprecation (Hari H):** Status pada [`Skill_Registry.md`](Skill_Registry.md) diubah menjadi **DEPRECATED**. Agen dilarang menggunakan skill ini untuk inisiatif baru.
2. **Tahap 2 — Masa Transisi & Migrasi (H+1 s.d. Sunset Date):** Alur kerja eksisting secara bertahap dialihkan ke skill pengganti.
3. **Tahap 3 — Pengarsipan Permanen (Sunset Date):** Status diubah menjadi **ARCHIVED**. Berkas `SKILL.md` dipindahkan atau dinonaktifkan dari direktori aktif.

---

## 6. Lembar Otorisasi Penghentian (Deprecation Sign-Off)

> 🛑 **Penghentian suatu skill production wajib disetujui secara resmi oleh Project Owner.**

- **Tinjauan Teknis (QA Lead / Reviewer):** `[RECOMMENDED / REJECTED]` | Catatan: `[...]`
- **Otorisasi Resmi (Yogi Ario):** `[APPROVED / REJECTED]` | Tanggal: `[DD/MM/YYYY]`
