# SKILL RESMI: REQUIREMENT ANALYSIS & SPECIFICATION (SKL-ENG-REQ-001)

---
name: "Requirement Analysis & Specification"
id: "SKL-ENG-REQ-001"
version: "1.0.0"
status: "Validated" # Seed Validated Skill (Approved for Pilot/Testing)
category: "Software Engineering"
risk_level: "Medium"
owner: "AI Lead Developer (Rujuk config/organization.md)"
authorizer: "Designated Approver (Rujuk config/organization.md)"
last_updated: "2026-11-07"
---

# SKILL: REQUIREMENT ANALYSIS & SPECIFICATION

> **SKILL ID:** `SKL-ENG-REQ-001`  
> **VERSI:** `v1.0.0` (SemVer)  
> **STATUS:** **VALIDATED** (Tervalidasi Teknis & Siap Uji Operasional)  
> **KATEGORI:** Software Engineering  
> **TINGKAT RISIKO:** Medium  
> **PEMILIK (OWNER):** AI Lead Developer Role (Rujuk [config/organization.md](../../../config/organization.md))  
> **OTORISASI:** Designated Approver (Rujuk [config/organization.md](../../../config/organization.md))  
> **PRINSIP TATA KELOLA:**  
> - *No Understanding, No Execution.*  
> - *No Plan, No Code.*  
> - *No Validated Skill, No Production Use.*  

---

## 1. Deskripsi & Tujuan (Description & Purpose)

### 1.1 Deskripsi Singkat (Description)
Prosedur operasional terstruktur untuk menguraikan, menganalisis, dan mentransformasikan kebutuhan mentah pengguna (*raw requirements / feature requests*) yang ambigu menjadi spesifikasi kebutuhan rekayasa yang terstruktur, teruji, beridentitas unik (`REQ-F-xxx` / `REQ-NF-xxx`), dan dilengkapi kriteria penerimaan terukur (*Acceptance Criteria*).

### 1.2 Tujuan Operasional (Purpose)
1. **Menghilangkan Ambiguitas:** Mencegah agen AI berasumsi atau mengarang fitur tanpa konfirmasi pengguna.
2. **Menegakkan Ketertelusuran (Traceability):** Menjamin setiap baris kode dan kasus pengujian memiliki rujukan requirement yang sah (*Requirement Traceability Matrix / RTM*).
3. **Mewujudkan Gerbang DoR:** Menyediakan dasar spesifikasi yang lengkap untuk memenuhi *Definition of Ready (DoR)* sebelum penulisan kode dimulai.

---

## 2. Ruang Lingkup & Batasan (Scope & Boundaries)

| Kategori Lingkup | Ketentuan Operasional |
| :--- | :--- |
| **In-Scope (Tercakup)** | 1. Dekomposisi teks kebutuhan mentah menjadi butir kebutuhan terstruktur.<br>2. Identifikasi kebutuhan fungsional (`REQ-F-xxx`) dan non-fungsional (`REQ-NF-xxx`).<br>3. Perumusan kriteria penerimaan (*Acceptance Criteria*) berbasis kondisi biner terukur.<br>4. Pemetaan dependensi teknis antarmodul.<br>5. Identifikasi potensi risiko awal dan batasan sistem (*system constraints*). |
| **Out-of-Scope (Dilarang)** | 1. Menulis implementasi kode atau skrip program (pekerjaan fase implementasi terpisah).<br>2. Mengubah arah bisnis atau visi proyek secara sepihak tanpa persetujuan Designated Approver.<br>3. Menghapus batasan tata kelola keamanan atau regulasi Level 1 Governance. |

---

## 3. Peran Agen yang Berizin (Intended Agent Roles)

Skill ini **HANYA** diizinkan untuk dieksekusi oleh agen dengan peran:
- [x] **Developer:** Saat menganalisis brief tugas sebelum menyusun rencana kerja (*Implementation Plan*).
- [x] **Manager:** Saat memvalidasi lingkup rilis dan menyusun spesifikasi kebutuhan sistem.
- [x] **Reviewer:** Saat mengaudit kelengkapan dokumen SRS atau DoR tiket penugasan.
- [ ] **Researcher:** *(Hanya untuk referensi; tidak diizinkan membuat keputusan spesifikasi teknis).*

---

## 4. Prasyarat & Pengetahuan Konteks (Prerequisites)

Sebelum menjalankan skill ini, agen **MUST** memiliki akses ke:
1. Dokumen standar kebutuhan rekayasa: [Docs/Standards/Requirements_Engineering.md](../../../Docs/Standards/Requirements_Engineering.md).
2. Tata kelola umum: [Docs/universal-rule.md](../../../Docs/universal-rule.md).
3. Teks brief, deskripsi tiket, atau pesan kebutuhan dari pengguna.

---

## 5. Kebutuhan Alat Teknis & Dependensi (Required Tools & Dependencies)

- `File System Tools`: Untuk membaca dokumen referensi dan menulis dokumen spesifikasi.
- `Markdown Formatter`: Untuk menjamin format dokumen keluaran presisi dan rapi.
- `Workspace Search`: Untuk memeriksa apakah requirement serupa sudah pernah didefinisikan sebelumnya.

---

## 6. Spesifikasi Data Masukan (Input Specification)

| Parameter | Tipe Data | Wajib / Opsional | Deskripsi & Batasan |
| :--- | :---: | :---: | :--- |
| `raw_requirement_text` | String | **Wajib** | Teks deskripsi kebutuhan atau tiket pengguna (min. 10 kata). |
| `project_context` | String / Path | **Wajib** | Nama modul, arsitektur dasar, atau konteks sistem aktif. |
| `target_module` | String | **Wajib** | Nama modul teknis yang dituju (misal: `AUTH`, `PAYMENT`, `QA`). |
| `business_priority` | Enum | Opsional | `P1-High` \| `P2-Medium` \| `P3-Low` (Default: `P2-Medium`). |

---

## 7. Spesifikasi Artefak Keluaran (Output Specification)

Skill ini menghasilkan artefak spesifikasi kebutuhan dengan format baku yang siap dimasukkan ke `Docs/SRS_Template.md` atau `Docs/Implementation_plan/`:

```markdown
### REQ-[TYPE]-[MODULE]-[NUM]: [Judul Kebutuhan]
- **Tipe Kebutuhan:** [Fungsional (REQ-F) | Non-Fungsional (REQ-NF)]
- **Prioritas:** [P1-High | P2-Medium | P3-Low]
- **Deskripsi Operasional:** [Penjelasan presisi apa yang harus dilakukan sistem]
- **Kondisi Masukan (Preconditions):** [Keadaan awal sistem]
- **Alur Kerja Utama (Main Flow):**
  1. [Langkah 1]
  2. [Langkah 2]
- **Kondisi Keluaran (Postconditions):** [Keadaan akhir sistem]
- **Kriteria Penerimaan (Acceptance Criteria):**
  - [ ] Kriteria 1 (Terukur & dapat diuji)
  - [ ] Kriteria 2 (Biner PASS / FAIL)
- **Batasan & Dependensi:** [Dependensi modul / batas teknis]
```

---

## 8. Prosedur Eksekusi Langkah demi Langkah (Execution Procedure SOP)

Setiap kali menjalankan analisis kebutuhan, agen **MUST** mematuhi 6 langkah sekuensial berikut:

1. **Langkah 1 — Validasi Masukan & Identifikasi Ambiguitas:**  
   Periksa `raw_requirement_text`. Cari kata-kata ambigu (misal: *"cepat"*, *"user-friendly"*, *"aman"*, *"secukupnya"*). Jika ditemukan ambiguitas kritis, **JANGAN BERASUMSI**; rumuskan pertanyaan klarifikasi kepada pengguna.
2. **Langkah 2 — Ekstraksi Kebutuhan Fungsional (`REQ-F`):**  
   Uraikan seluruh kemampuan operasional spesifik yang wajib disediakan sistem. Berikan ID terstruktur: `REQ-F-[MODULE]-[NUM]` (misal: `REQ-F-AUTH-001`).
3. **Langkah 3 — Ekstraksi Kebutuhan Non-Fungsional (`REQ-NF`):**  
   Identifikasi batasan performa (latency, throughput), keamanan (redaksi secret, enkripsi), ketersediaan, dan kompatibilitas. Berikan ID: `REQ-NF-[MODULE]-[NUM]`.
4. **Langkah 4 — Perumusan Kriteria Penerimaan (Acceptance Criteria):**  
   Susun kriteria penerimaan berbasis biner (*PASS* atau *FAIL*). Setiap kriteria harus dapat diverifikasi melalui pengujian nyata. Gunakan kata kunci normatif RFC 2119 (MUST, MUST NOT, SHOULD).
5. **Langkah 5 — Pemetaan Dependensi & Risiko:**  
   Petakan dependensi ke modul lain, skema basis data, atau API eksternal. Identifikasi potensi risiko teknis dan rencana mitigasinya.
6. **Langkah 6 — Kompilasi Dokumen & Permohonan Validasi:**  
   Sajikan hasil analisis ke dalam format spesifikasi standar. Ajukan kepada pengguna untuk divalidasi sebelum menyusun kode implementasi (*No Plan, No Code*).

---

## 9. Batasan Operasional & Prosedur Fallback (Constraints & Fallback)

- **Batasan Ketat:**  
  Agen dilarang menambahkan requirement fungsional di luar apa yang disepakati pengguna tanpa menandainya secara eksplisit sebagai *"Usulan Tambahan (Proposed Extension)"*.
- **Prosedur Fallback Jika Kebutuhan Terlalu Minim:**  
  Jika `raw_requirement_text` sangat singkat atau minim informasi (misal: *"buatkan login"*):
  1. Jangan langsung membuat kode login.
  2. Sajikan draf spesifikasi berbasis *best practices* standar industri.
  3. Minta konfirmasi Designated Approver apakah asumsi standar tersebut disetujui.

---

## 10. Persyaratan Keamanan & Proteksi (Safety & Security Requirements)

- **Sanitasi Kredensial:** Teks kebutuhan mentah dilarang memuat password riil, token rahasia, atau data pribadi pengguna.
- **Tinjauan Risiko Keamanan Otomatis:** Setiap requirement yang bersentuhan dengan data autentikasi atau finansial wajib otomatis memiliki kebutuhan non-fungsional keamanan (`REQ-NF-SEC-xxx`).

---

## 11. Kriteria Kualitas & Rubrik Kelulusan (Quality Criteria)

Artefak spesifikasi kebutuhan dinyatakan LULUS (*PASS*) jika:
1. Memperoleh skor minimal **4.5 / 5.0** pada rubrik evaluasi mutu kebutuhan:
   - *Akurasi & Relevansi terhadap brief:* $\ge 4.5$
   - *Ketiadaan Kata Ambigu:* $\ge 4.0$
   - *Keterukuran Kriteria Penerimaan:* $\ge 4.5$
   - *Kepatuhan Format Standar:* $5.0$
2. Seluruh ID requirement berurutan dan unik.

---

## 12. Metode Validasi & Kasus Pengujian (Validation Method)

- **Status Validasi:** **PASS (VALIDATED - 6 DIMENSIONS APPLIED)**
- **Tanggal Uji:** 07/11/2026
- **Dimensi Validasi & Kasus Uji Terverifikasi:**
  - `Dimensi 1 (Rule Compliance)`: **PASS** (Mematuhi gerbang persetujuan dan zero-code rule).
  - `Dimensi 2 (Functional Testing)`: **PASS** (`TC-SKL-REQ-001` — Analisis Checkout E-Commerce berhasil lengkap).
  - `Dimensi 3 (Edge Cases)`: **PASS** (`TC-SKL-REQ-002` — Ambiguous Performance Request meminta klarifikasi; `TC-SKL-REQ-003` — Empty Input Rejection menolak anggun).
  - `Dimensi 4 (Output Quality)`: **PASS** (Skor Rubrik 4.8 / 5.0).
  - `Dimensi 5 (Security Review)`: **PASS** (`TC-SKL-REQ-004` — Sanitasi secret otomatis).
  - `Dimensi 6 (Regression Testing)`: **N/A** (Justifikasi: Initial seed release v1.0.0; belum ada versi rilis terdahulu).
- **Tautan Laporan Evaluasi:** [Docs/Skill_Evaluation_Report.md](../../../Docs/Skill_Evaluation_Report.md).

---

## 13. Persyaratan Bukti Eksekusi (Evidence Requirements)

Setiap eksekusi skill ini wajib menyertakan bukti nyata:
- [ ] Tautan artefak spesifikasi kebutuhan pada sistem file repositori (`Docs/SRS_Template.md` atau `Docs/Implementation_plan/`).
- [ ] Bukti checklist ambiguitas terisi lengkap (Zero unchecked ambiguities).
- [ ] Catatan konfirmasi / tanda terima dari pengguna atau peran pengembang.

---

## 14. Keterbatasan yang Diketahui (Known Limitations)

1. **Domain Logika Spesifik Sangat Kompleks:** Pada domain bisnis khusus (misal: perpajakan rumit, kalkulasi aktuaria), skill ini memerlukan dokumen acuan hukum atau formula spesifik dari analis bisnis manusia.
2. **Keterbatasan Ekstraksi Diagram Mentah:** Masukan berupa diagram arsitektur sketsa tangan memerlukan transkripsi tekstual terlebih dahulu sebelum dapat dianalisis.
3. **Ukuran Dokumen Raksasa:** Dokumen brief yang melebihi 50 halaman harus dibagi per-modul sebelum dievaluasi guna mencegah degradasi atensi LLM.

---

## 15. Penanganan Insiden & Hambatan (Incident & Blocker Handling)

- Jika terjadi ambiguitas kebutuhan yang kontradiktif dan pengguna tidak memberikan jawaban, agen **MUST** mengubah status tugas menjadi **BLOCKED**.
- Catat insiden pada `Docs/Agent_Incident_Log.md` dengan ID `INC-CTX-[NUM]`.

---

## 16. Pemicu Validasi Ulang (Revalidation Triggers)

Skill ini wajib divalidasi ulang jika terjadi:
1. Pembaruan format baku spesifikasi kebutuhan pada `Docs/Standards/Requirements_Engineering.md`.
2. Perubahan pada struktur taksonomi ID kebutuhan (`REQ-F` / `REQ-NF`).
3. Ditemukan kegagalan pemahaman kebutuhan pada fase pengujian sistem.

---

## 17. Riwayat Perubahan Modul (Changelog)

| Versi | Tanggal | Agen Pengubah | Ringkasan Perubahan | Status |
| :---: | :---: | :--- | :--- | :---: |
| `v1.0.0` | 2026-11-07 | AI Lead Developer | Inisiasi seed validated skill untuk analisis kebutuhan sistem dengan 18 atribut lengkap. | Validated (Pilot-Ready) |
