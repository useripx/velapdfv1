# MATRIKS SUMBER OTORITATIF TUNGGAL (SINGLE SOURCE OF TRUTH — SSOT)

> **DOCUMENT ID:** DOC-REF-SOT-001  
> **VERSION:** v1.0.0  
> **STATUS:** ACTIVE  
> **AUTHORITY LEVEL:** Level 5 — Reference & Governance Mapping  
> **OWNER:** Yogi Ario (Project Owner) & AI Engineering Team  
> **LAST UPDATED:** 07/11/2026  
> **AUTHORITATIVE SOURCE (SSOT):** `Docs/Standards/Documentation_Standards.md`  
> **RELATED DOCUMENTS:** `Docs/universal-rule.md`, `Docs/Governance_Hierarchy.md`, `Docs/Documentation_Hierarchy.md`  

---

## 1. Prinsip Single Source of Truth (SSOT)

Dalam ekosistem **Universal AI Engineering Standard**, setiap kategori aturan, kebijakan, dan standar teknis **HANYA MEMILIKI SATU SUMBER OTORITATIF TUNGGAL**.

Aturan operasional SSOT:
1. **Tidak Ada Standar Kembar:** Dilarang menyimpan atau mengaktifkan dua versi dokumen yang mengatur domain yang sama.
2. **Dokumen Turunan Bersifat Rujukan:** Dokumen pada level yang lebih rendah (misal: instruksi agen, modul spesifik, template) hanya boleh merujuk (*refer*) atau menyarikan (*summarize*) aturan dari SSOT tanpa mengubah esensi hukumnya.
3. **Resolusi Otomatis ke SSOT:** Jika terjadi pertentangan antara teks di dokumen turunan dengan dokumen SSOT, maka ketentuan dalam dokumen SSOT **berlaku mutlak**.

---

## 2. Tabel Pemetaan Master SSOT Repositori

| No | Kategori Aturan / Domain | Dokumen Sumber Otoritatif (Authoritative SSOT) | Tingkat Otoritas | Dokumen Turunan / Rujukan Pendukung |
| :-: | :--- | :--- | :---: | :--- |
| **1** | **Universal Governance & Prinsip Utama** | [`Docs/universal-rule.md`](universal-rule.md) | Level 1 | `README.md`, `Governance_Hierarchy.md`, `AGENTS.md`, `GEMINI.md` |
| **2** | **Kebijakan Persetujuan & Batas Otoritas** | [`Docs/Approval_Governance.md`](Approval_Governance.md) | Level 1 | `Agent_Authority_Matrix.md`, `Verification_Checklist.md` |
| **3** | **7 Pilar Standar Rekayasa Perangkat Lunak** | [`Docs/Standards/Engineering_Standards.md`](Standards/Engineering_Standards.md) | Level 2 | 7 berkas pilar spesifik di `Docs/Standards/`, `SRS_Template.md`, `Architecture_Design.md` |
| **4** | **Rekayasa Kebutuhan (Requirements)** | [`Docs/Standards/Requirements_Engineering.md`](Standards/Requirements_Engineering.md) | Level 2 | `Project_Analysis.md`, `SRS_Template.md` |
| **5** | **Arsitektur & Desain Sistem** | [`Docs/Standards/Architecture_Design.md`](Standards/Architecture_Design.md) | Level 2 | `Decision_Log.md` (ADR), `Database_Design.md`, `API_Contract.md` |
| **6** | **Standar Penulisan Kode (Coding)** | [`Docs/Standards/Coding_Standards.md`](Standards/Coding_Standards.md) | Level 2 | `AGENTS.md` (Bagian 2 & 3), `GEMINI.md` |
| **7** | **Alur Kerja Git & Version Control** | [`Docs/Standards/Git_Version_Control.md`](Standards/Git_Version_Control.md) | Level 2 | `changelog/*.md`, `universal-rule.md` (Pilar 4) |
| **8** | **Basis Data & Kontrak API** | [`Docs/Standards/Database_API_Standards.md`](Standards/Database_API_Standards.md) | Level 2 | `Database_Design.md`, `API_Contract.md` |
| **9** | **Keamanan Rekayasa (Security)** | [`Docs/Standards/Security_Engineering.md`](Standards/Security_Engineering.md) | Level 2 | `universal-rule.md` (Zero Secret Leakage), `Tool_Governance_Execution.md` |
| **10** | **Deployment & Pemeliharaan** | [`Docs/Standards/Deployment_Maintenance.md`](Standards/Deployment_Maintenance.md) | Level 2 | `universal-rule.md` (Clean Main Policy) |
| **11** | **Kerangka Jaminan Kualitas (QA Framework)**| [`Docs/Standards/QA_Framework.md`](Standards/QA_Framework.md) | Level 2 | `Testing_Strategy.md`, `Test_Case_Management.md`, `Defect_Management.md`, `Test_Report.md` |
| **12** | **Strategi & 10 Tingkatan Pengujian** | [`Docs/Standards/Testing_Strategy.md`](Standards/Testing_Strategy.md) | Level 2 | `Test_Case_Management.md`, `Test_Report.md`, `Verification_Evidence.md` |
| **13** | **Manajemen Cacat (Defect Management)** | [`Docs/Standards/Defect_Management.md`](Standards/Defect_Management.md) | Level 2 | `Defect_Log.md` |
| **14** | **Bukti & Evidensi Verifikasi** | [`Docs/Standards/Verification_Evidence.md`](Standards/Verification_Evidence.md) | Level 2 | `Verification_Checklist.md`, `Docs/Evidence/` |
| **15** | **Arsitektur Skill AI (AI Employee Skill)** | [`Docs/Standards/Skill_Architecture.md`](Standards/Skill_Architecture.md) | Level 2 | `skills/README.md`, `skills/**/SKILL.md`, `Skill_Specification_Template.md` |
| **16** | **Siklus Hidup & Izin Skill** | [`Docs/Standards/Skill_Lifecycle_Governance.md`](Standards/Skill_Lifecycle_Governance.md) | Level 2 | `Skill_Registry.md`, `Skill_Change_Request.md`, `Skill_Deprecation_Report.md` |
| **17** | **Validasi & Pengujian Skill (6 Dimensi)** | [`Docs/Standards/Skill_Validation_Testing.md`](Standards/Skill_Validation_Testing.md) | Level 2 | `Skill_Validation_Checklist.md`, `Skill_Evaluation_Report.md` |
| **18** | **Tata Kelola & Arsitektur Agen AI** | [`Docs/Standards/Agent_Architecture_Governance.md`](Standards/Agent_Architecture_Governance.md) | Level 2 | `agents/README.md`, `agents/*.md`, `Agent_Definition_Template.md` |
| **19** | **Orkestrasi & Protokol Serah Terima (Handoff)** | [`Docs/Standards/Agent_Orchestration_Handoff.md`](Standards/Agent_Orchestration_Handoff.md) | Level 2 | `Agent_Handoff_Template.md`, `Task_Specification_Template.md`, `Task_Status.md` |
| **20** | **Tata Kelola Alat Teknis (Tool Governance)** | [`Docs/Standards/Tool_Governance_Execution.md`](Standards/Tool_Governance_Execution.md) | Level 2 | `universal-rule.md`, `Security_Engineering.md` |
| **21** | **Penanganan Error & Pemulihan (Recovery)** | [`Docs/Standards/Error_Handling_Recovery.md`](Standards/Error_Handling_Recovery.md) | Level 2 | `Agent_Incident_Log.md`, `Communication_Protocol.md` |
| **22** | **Standar Tata Kelola Dokumentasi** | [`Docs/Standards/Documentation_Standards.md`](Standards/Documentation_Standards.md) | Level 2 | `Documentation_Hierarchy.md`, `Glossary.md`, `Template_Registry.md` |
| **23** | **Manajemen Perubahan & Versi Standar** | [`Docs/Standards/Documentation_Change_Management.md`](Standards/Documentation_Change_Management.md) | Level 2 | `Documentation_Change_Request.md`, `changelog/*.md` |
| **24** | **Kesehatan & Kebersihan Repositori** | [`Docs/Standards/Repository_Health_Standard.md`](Standards/Repository_Health_Standard.md) | Level 2 | `Repository_Health_Checklist.md` |

---

## 3. Prosedur Pemeliharaan & Sinkronisasi SSOT

1. **Kutipan Wajib Menyertakan Tautan SSOT:** Dokumen pendukung yang mengutip kebijakan atau standar tertentu wajib mencantumkan tautan langsung ke berkas SSOT-nya.
2. **Larangan Salin-Tempel (No Duplicate Copy-Paste):** Hindari menyalin teks panjang dari SSOT ke dokumen turunan; buat ringkasan ringkas (*summary*) dan sertakan tautan ke dokumen otoritatif.
3. **Penyelarasan Saat Pembaruan:** Apabila dokumen SSOT diperbarui melalui proses *Change Control*, seluruh dokumen turunan yang relevan wajib ditinjau dan diselaraskan pada commit yang sama atau rangkaian commit yang berurutan.
