# DAFTAR PERIKSA KESEHATAN REPOSITORI (REPOSITORY HEALTH CHECKLIST)

> **DOCUMENT ID:** DOC-CHK-HLT-001  
> **TEMPLATE VERSION:** v1.0.0  
> **STATUS:** TEMPLATE CHECKLIST  
> **AUTHORITY LEVEL:** Level 5 — Health Audit Checklist  
> **OWNER:** Yogi Ario (Project Owner) & AI Engineering Team  
> **AUTHORITATIVE SOURCE (SSOT):** `Docs/Standards/Repository_Health_Standard.md`  

---

## 1. Informasi Audit Kesehatan

| Atribut Audit | Keterangan Isian Auditor |
| :--- | :--- |
| **Audit Session ID** | `HLT-AUD-[YYYYMMDD]-[NUM]` *(Contoh: `HLT-AUD-20261107-001`)* |
| **Tanggal Pelaksanaan** | [DD/MM/YYYY HH:MM WIB] |
| **Auditor / Lead AI** | [Nama Auditor / Agent ID] |
| **Pemicu Audit** | `[PRE-MERGE / PRE-RELEASE / PERIODIC-MAINTENANCE]` |
| **Cabang Git Target** | `[develop / vX / feature/...]` |
| **Hasil Keseluruhan** | `[100% HEALTHY (PASS) / DEFECT FOUND (FAIL)]` |

---

## 2. Tabel Pemeriksaan 10 Aspek Kesehatan Repositori

| No | Parameter Pemeriksaan | Metode Pengujian / Verifikasi | Kondisi Diharapkan | Status Evaluasi | Catatan Bukti Temuan |
| :-: | :--- | :--- | :--- | :---: | :--- |
| **1** | **Broken Links (Tautan Rusak)** | Pemindaian seluruh sintaks `[Teks](path)` di dokumen markdown. | 0 tautan rusak (semua path valid di disk). | `[PASS / FAIL]` | |
| **2** | **Missing References** | Pengecekan referensi dokumen SSOT, template, dan gambar. | Seluruh file rujukan tersedia faktual. | `[PASS / FAIL]` | |
| **3** | **Duplicate Documents** | Pemindaian kesamaan nama atau isi aturan yang tumpang tindih. | Tidak ada dokumen otoritas ganda. | `[PASS / FAIL]` | |
| **4** | **Inconsistent Terminology** | Pencocokan istilah teknis terhadap `Docs/Glossary.md`. | Istilah konsisten (misal: DoR, DoD, SSOT). | `[PASS / FAIL]` | |
| **5** | **Empty Files / Folders** | Pemeriksaan berkas 0 byte dan folder tanpa `.gitkeep`. | Tidak ada folder kosong terbengkalai. | `[PASS / FAIL]` | |
| **6** | **Stale Placeholders** | Pencarian teks `TODO`, `TBD`, `[ISI DI SINI]` pada doc approved. | 0 stale placeholder pada dokumen rilis. | `[PASS / FAIL]` | |
| **7** | **Metadata Hygiene** | Verifikasi blok metadata pada dokumen Level 1 & 2. | 100% dokumen master memiliki metadata. | `[PASS / FAIL]` | |
| **8** | **Outdated Instructions** | Pemeriksaan relevansi panduan pada `AGENTS.md` & `GEMINI.md`. | Selaras dengan arsitektur & wewenang terbaru. | `[PASS / FAIL]` | |
| **9** | **Untracked Important Files** | Audit `git status` terhadap berkas penting yang belum di-commit. | Seluruh dokumen penting tercatat di git. | `[PASS / FAIL]` | |
| **10** | **Clean Main Compliance** | Pemeriksaan isi branch `main` dari riwayat merge dan log. | Branch `main` 100% bersih dari test/chat/SS. | `[PASS / FAIL]` | |

---

## 3. Temuan Anomali & Tindakan Perbaikan (Defects & Remediation)

| No | Lokasi Berkas | Deskripsi Anomali / Cacat | Klasifikasi Cacat | Tindakan Perbaikan Langsung | Status |
| :-: | :--- | :--- | :---: | :--- | :---: |
| 1 | *Contoh: Docs/Contoh.md* | *Tautan ke file lama patah* | *Minor* | *Diperbarui mengarah ke file baru* | *FIXED* |

---

## 4. Kesimpulan & Rekomendasi Auditor

- **Status Integritas Repositori:** `[SEHAT & SIAP RILIS / PERLU PERBAIKAN SEBELUM MERGE]`
- **Rekomendasi Pemeliharaan:**
  - [Catatan rekomendasi auditor untuk pemeliharaan berkelanjutan]

---

## 5. Tanda Tangan Auditor

- **Auditor:** `[Nama Lead Dev / QA Engineer]`
- **Tanggal:** `[DD/MM/YYYY]`
- **Status Akhir:** `VERIFIED PASS`
