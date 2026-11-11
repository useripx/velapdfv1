# REGISTRI MASTER SKILL KARYAWAN AI (AI EMPLOYEE SKILL REGISTRY)

> **DOCUMENT ID:** DOC-REF-SKR-001  
> **VERSION:** v2.0.0  
> **STATUS:** AKTIF & TERPUSAT  
> **TERAKHIR DIPERBARUI:** 07/11/2026  
> **PENANGGUNG JAWAB:** Lead Developer Role (Rujuk [config/organization.md](../config/organization.md))  
> **OTORISASI:** Designated Approver (Rujuk [config/organization.md](../config/organization.md), saat ini: **Yogi Ario**)  
> **PRINSIP DASAR:**  
> - *Consistent Standards, Variable Intelligence.*  
> - *No Validated Skill, No Production Use.*  
> - *Validated $\neq$ Approved $\neq$ Production.*  

---

## 1. Ringkasan Status Katalog Skill

| Total Skill Terdaftar | Production (Aktif) | Validated (Pilot-Ready) | Testing / Under Review | Draft / Proposed | Deprecated / Archived |
| :---: | :---: | :---: | :---: | :---: | :---: |
| **1** | **0** | **1** | **0** | **0** | **0** |

> [!NOTE]
> Seluruh agen AI **MUST** merujuk pada tabel registri ini sebelum mengeksekusi suatu skill. Hanya skill berstatus **PRODUCTION** yang sah digunakan secara mandiri untuk tugas lingkungan produksi operasional penuh. Skill berstatus **VALIDATED** diizinkan untuk eksekusi uji percontohan (*pilot testing*) di bawah pengawasan.

---

## 2. Tabel Registri Master Skill

| Skill ID | Nama Skill | Kategori | Versi | Status | Peran Berizin | Risiko | Tgl Validasi | Status Approval | Lokasi Berkas |
| :--- | :--- | :--- | :---: | :---: | :--- | :---: | :---: | :---: | :--- |
| `SKL-ENG-REQ-001` | *Requirement Analysis & Specification* | Software Eng | `v1.0.0` | **VALIDATED** | Developer, Manager, Reviewer | Medium | 07/11/2026 | **APPROVED FOR PILOT** (Designated Approver) | [skills/software-engineering/requirement-analysis/SKILL.md](../skills/software-engineering/requirement-analysis/SKILL.md) |

---

## 3. Rincian Metrik & Dependensi Skill Aktif

### 3.1 `SKL-ENG-REQ-001`: Requirement Analysis & Specification
- **Deskripsi:** Prosedur operasional baku untuk menganalisis kebutuhan mentah dari pengguna atau dokumen brief, mengidentifikasi ambiguitas, menyusun kebutuhan fungsional (`REQ-F-xxx`), non-fungsional (`REQ-NF-xxx`), batasan sistem, dan kriteria penerimaan terukur (*Acceptance Criteria*).
- **Pemilik (Owner):** AI Lead Developer Role
- **Alat yang Dibutuhkan:** File Reader, Markdown Formatter, Text Analyzer.
- **Dependensi Antar-Skill:** Tidak ada (merupakan skill hulu / *root skill*).
- **Riwayat Validasi:** Lolos pengujian 6 dimensi pada 07/11/2026 dengan skor rubrik 4.8/5.0 (Dimensi 6 Regression N/A untuk v1.0.0 baseline).
- **Status Otorisasi:** Disetujui resmi oleh Designated Approver untuk pengujian percontohan operasional.

---

## 4. Panduan Pencarian & Penggunaan Skill bagi Agen AI

Saat menerima tugas, AI Agent **MUST** menjalankan langkah berikut:
1. Periksa kolom **Peran Berizin**. Jika peran Anda tidak tercantum, Anda **DILARANG** mengeksekusi skill ini.
2. Periksa kolom **Status**. Jika status bukan **PRODUCTION** atau **VALIDATED (Pilot)**, Anda **DILARANG** menggunakannya.
3. Buka tautan pada kolom **Lokasi Berkas** dan ikuti prosedur langkah demi langkah (*Execution Procedure*) secara presisi.
4. Jika skill yang dibutuhkan tidak tersedia di registri ini, gunakan prosedur kerja standar yang sah atau ajukan pembuatan skill baru melalui [Docs/Skill_Specification_Template.md](Skill_Specification_Template.md).

---

## 5. Rujukan Dokumen Terkait
- Arsitektur Sistem Skill: [Docs/Standards/Skill_Architecture.md](Standards/Skill_Architecture.md)
- Siklus Hidup & Hak Akses: [Docs/Standards/Skill_Lifecycle_Governance.md](Standards/Skill_Lifecycle_Governance.md)
- Standar Validasi: [Docs/Standards/Skill_Validation_Testing.md](Standards/Skill_Validation_Testing.md)
- Template Formulir Skill: [Docs/Skill_Specification_Template.md](Skill_Specification_Template.md)
- Konfigurasi Organisasi: [config/organization.md](../config/organization.md)
