# STANDAR KESEHATAN & PEMELIHARAAN REPOSITORI

> **DOCUMENT ID:** STD-DOC-HLT-001  
> **VERSION:** v1.0.0  
> **STATUS:** ACTIVE  
> **AUTHORITY LEVEL:** Level 2 — Professional Engineering Standard  
> **OWNER:** Yogi Ario (Project Owner) & AI Engineering Team  
> **LAST UPDATED:** 07/11/2026  
> **AUTHORITATIVE SOURCE (SSOT):** `Docs/Standards/Repository_Health_Standard.md`  
> **RELATED DOCUMENTS:** `Docs/universal-rule.md`, `Docs/Standards/Documentation_Standards.md`, `Docs/Repository_Health_Checklist.md`, `Docs/Standards/Git_Version_Control.md`  

---

## 1. Tujuan & Ruang Lingkup

Standar ini menetapkan indikator, protokol audit, kriteria ambang batas (*thresholds*), dan tindakan korektif untuk memastikan bahwa repositori **Universal AI Engineering Standard** selalu berada dalam kondisi sehat, bersih, terawat, dan bebas dari cacat struktural.

Kesehatan repositori menjamin bahwa baik manusia maupun AI Agent dapat bernavigasi dan membaca konteks kerja secara cepat tanpa tersandung oleh tautan rusak, berkas duplikat, atau metadata yang usang.

---

## 2. Indikator Kunci Kesehatan Repositori (Key Health Indicators)

Audit kesehatan repositori memantau 7 indikator utama:

| Indikator Kesehatan | Definisi & Target Kepatuhan | Ambang Toleransi |
| :--- | :--- | :---: |
| **1. Broken Links (Tautan Rusak)** | Seluruh tautan Markdown internal mengarah ke file dan baris yang valid ada di disk. | **0 Tautan Rusak (Zero Broken Link)** |
| **2. Missing References** | Setiap dokumen rujukan (SSOT, template, standar pendukung) benar-benar tersedia. | **0 Missing Reference** |
| **3. Orphaned Files (Berkas Yatim)** | Berkas tanpa keterkaitan atau tautan masuk dari indeks utama/README. | **0 Berkas Tanpa Indeks** |
| **4. Duplicate Documents** | Tidak ada dua berkas aktif yang mendefinisikan aturan sama secara paralel. | **0 Duplikasi Otoritas** |
| **5. Stale Placeholders** | Tidak ada teks placeholder yang belum tuntas di dokumen approved (`TODO`, `TBD`, `[ISI DI SINI]`). | **0 Stale Placeholder** |
| **6. Metadata Hygiene** | Seluruh dokumen master Level 1–2 memiliki blok metadata lengkap dan valid. | **100% Sesuai Standar** |
| **7. Clean Main Isolation** | Branch `main` tidak memuat berkas test, draft, obrolan sementara, atau screenshot evidensi. | **100% Bersih & Terisolasi** |

---

## 3. Frekuensi & Pemicu Audit Kesehatan

Audit kesehatan wajib dijalankan pada 3 momentum:
1. **Per-Feature Pre-Merge Audit:** Setiap cabang fitur wajib diaudit kesehatannya sebelum digabungkan ke cabang `develop`.
2. **Pre-Release Audit:** Sebelum pembuatan tag rilis versi atau sinkronisasi ke branch snapshot (`v1`, `v2`, dst.), audit kesehatan penuh wajib dilakukan.
3. **Periodic Maintenance Audit:** Peninjauan rutin bulanan terhadap seluruh berkas dan keterbaruan tautan.

---

## 4. Prosedur Audit & Remediasi

### 4.1 Prosedur Audit Tautan & Referensi
1. Verifikasi seluruh sintaks tautan Markdown `[Teks](path/ke/berkas.md)`.
2. Pastikan file target berada pada lokasi relatif yang benar.
3. Jika file dipindahkan atau diganti nama, perbarui seluruh tautan masuk (*incoming links*) di seluruh repositori secara sinkron (*atomic update*).

### 4.2 Prosedur Penanganan Dokumen Usang / Yatim
1. Jika dokumen tidak lagi memiliki fungsi operasional, jangan menghapusnya secara sembarangan.
2. Terapkan prosedur deprecasi (beri status `DEPRECATED`), dan jika telah melewati masa retensi, pindahkan referensinya ke arsip sejarah.
3. Dilarang meninggalkan folder kosong tanpa berkas `.gitkeep` apabila folder tersebut memang dicadangkan untuk keperluan terstruktur di masa depan.

### 4.3 Prosedur Penegakan Clean Main Policy
1. Periksa riwayat git branch `main`.
2. Pastikan tidak ada file log chat harian (`Docs/chat/`), tangkapan layar bukti (`Docs/Evidence/`), berkas draf, atau skrip uji coba yang masuk ke branch `main`.
3. File-file tersebut hanya diperbolehkan tersimpan di branch kerja dan dokumentasi internal (`develop`, `vX`, dan `feature/...`).
