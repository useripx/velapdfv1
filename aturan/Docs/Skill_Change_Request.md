# FORMULIR PERMOHONAN PERUBAHAN SKILL (SKILL CHANGE REQUEST)

> **STATUS:** TEMPLATE OPERASIONAL  
> **KODE DOKUMEN:** CR-SKL-001  
> **PENGGUNAAN:** Prosedur formal untuk mengajukan modifikasi, pembaruan, atau perbaikan pada skill berstatus PRODUCTION  
> **OTORISASI:** Yogi Ario (Project Owner)  

---

## 1. Metadata Permohonan Perubahan (Change Request Metadata)

| Parameter | Keterangan / Nilai |
| :--- | :--- |
| **ID Permohonan (CR ID)** | `CR-SKL-[CAT]-[YYYYMMDD]-[NUM]` |
| **Target Skill ID** | `SKL-[CAT]-[CODE]-[NUM]` |
| **Versi Saat Ini** | `v[MAJOR].[MINOR].[PATCH]` |
| **Usulan Versi Baru** | `v[MAJOR].[MINOR].[PATCH]` |
| **Jenis Perubahan Versi** | [MAJOR (Breaking) \| MINOR (Feature) \| PATCH (Bugfix/Docs)] |
| **Agen Pemohon** | [Nama & Peran Agen Pemohon] |
| **Tanggal Pengajuan** | [DD/MM/YYYY] |
| **Tingkat Urgensi** | [Normal \| Mendesak / Hotfix] |

---

## 2. Latar Belakang & Alasan Perubahan (Rationale for Change)

- **Masalah atau Kebutuhan Baru:**  
  [Jelaskan mengapa perubahan ini diperlukan: apakah ada bug, pembaruan API/tool, atau efisiensi prosedur]
- **Tautan Cacat Terkait (Bila ada):** `DEF-[MODULE]-[NUM]` pada [`Defect_Log.md`](Defect_Log.md).
- **Dampak Bila Tidak Diubah:**  
  [Jelaskan risiko operasional bila skill dibiarkan menggunakan versi lama]

---

## 3. Rincian Usulan Perubahan Prosedural (Proposed Modifications)

| Bagian `SKILL.md` yang Diubah | Kondisi Saat Ini (Before) | Usulan Perubahan (After) | Alasan Perubahan |
| :--- | :--- | :--- | :--- |
| **Required Tools** | [Alat lama] | [Alat baru] | [Alasan integrasi] |
| **Input / Output Spec** | [Format lama] | [Format baru] | [Penyempurnaan struktur] |
| **Execution Procedure** | [Langkah lama] | [Langkah baru] | [Penyederhanaan SOP] |
| **Constraints / Safety** | [Batasan lama] | [Batasan baru] | [Peningkatan keamanan] |

---

## 4. Analisis Dampak & Kompatibilitas Mundur (Impact Assessment)

- **Apakah Perubahan Bersifat Breaking (Merusak Alur Lama)?:** `[YA / TIDAK]`
  - Jika **YA**, jelaskan rencana migrasi untuk alur kerja atau agen yang sedang menggunakan versi lama:
    `[Rencana migrasi dan masa tenggang]`
- **Dependensi yang Terdampak:**  
  [Daftar skill lain atau alur kerja yang bergantung pada skill ini]
- **Kebutuhan Revalidasi:**  
  [Apakah memerlukan pengujian penuh 6 dimensi atau cukup uji regresi fungsional?]

---

## 5. Rencana Pengujian Regresi & Validasi Ulang

- [ ] Pengujian kasus uji eksisting (`TC-SKL-xxx`) untuk memastikan zero regression.
- [ ] Pengujian skenario baru khusus untuk memvalidasi fitur/perbaikan yang ditambahkan.
- [ ] Verifikasi kepatuhan terhadap Level 1 Governance dan redaksi data rahasia.

---

## 6. Lembar Persetujuan Perubahan (Change Approval Gate)

> 🛑 **Sesuai prinsip tata kelola, skill production TIDAK BOLEH dimodifikasi secara langsung tanpa persetujuan tertulis dari Project Owner.**

- **Tinjauan Teknis (QA Lead / Reviewer):** `[RECOMMENDED / REJECTED]` | Catatan: `[...]`
- **Otorisasi Resmi (Yogi Ario):** `[APPROVED / REJECTED]` | Tanggal: `[DD/MM/YYYY]`
