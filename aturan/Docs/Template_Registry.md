# REGISTRI MASTER TEMPLATE OPERASIONAL (TEMPLATE REGISTRY)

> **DOCUMENT ID:** DOC-REG-TPL-001  
> **VERSION:** v2.0.0  
> **STATUS:** AKTIF & TERPUSAT  
> **AUTHORITY LEVEL:** Level 5 — Operational Master Catalog  
> **DESIGNATED APPROVER:** Rujuk [config/organization.md](../config/organization.md) (saat ini: Yogi Ario)  
> **PENANGGUNG JAWAB:** AI Engineering Team  
> **LAST UPDATED:** 07/11/2026  
> **AUTHORITATIVE SOURCE (SSOT):** [Docs/Standards/Documentation_Standards.md](Standards/Documentation_Standards.md)  
> **RELATED DOCUMENTS:** [Docs/Documentation_Hierarchy.md](Documentation_Hierarchy.md), [Docs/Single_Source_of_Truth_Matrix.md](Single_Source_of_Truth_Matrix.md), [Docs/Glossary.md](Glossary.md), [Docs/Traceability_Matrix.md](Traceability_Matrix.md)

---

## 1. Pendahuluan

Katalog ini mencatat dan memetakan seluruh **Template Operasional Resmi** (Level 5) yang tersedia dalam paket template *Universal AI Engineering Standard*. 

Setiap template dirancang untuk dapat digunakan lintas proyek (*reusable & project-agnostic*), memiliki instruksi pengisian yang jelas, memisahkan bidang wajib dan opsional, serta selaras dengan 6 Prinsip Tata Kelola.

---

## 2. Matriks Katalog Master Template Operasional

| ID Template | Nama Berkas Template | Kategori Siklus | Pengguna / Peran | Deskripsi Fungsi Operasional |
| :--- | :--- | :--- | :--- | :--- |
| **TPL-CFG-001** | [`config/organization.md`](../config/organization.md) | Configuration | Project Owner / Lead Dev | Konfigurasi identitas organisasi, persona agen, dan Designated Approver. |
| **TPL-REQ-001** | [`Docs/Project_Analysis.md`](Project_Analysis.md) | Requirement | Project Manager / Researcher | Analisis kelayakan, estimasi kompleksitas, dan batasan awal proyek. |
| **TPL-REQ-002** | [`Docs/SRS_Template.md`](SRS_Template.md) | Requirement | Lead Dev / System Analyst | Spesifikasi kebutuhan perangkat lunak (fungsional REQ-F & non-fungsional REQ-NF). |
| **TPL-ARC-001** | [`Docs/Standards/Architecture_Design.md`](Standards/Architecture_Design.md) | Architecture | Lead Dev / Architect | Spesifikasi arsitektur modular tingkat tinggi dan data flow sistem. |
| **TPL-ARC-002** | [`Docs/Database_Design.md`](Database_Design.md) | Database | Backend Dev / DBA | Rancangan skema tabel, indeks, relasi, dan kamus data. |
| **TPL-ARC-003** | [`Docs/API_Contract.md`](API_Contract.md) | API Design | Backend & Frontend Dev | Kontrak spesifikasi endpoint, request payload, response schema, dan error code. |
| **TPL-GOV-001** | [`Docs/Decision_Log.md`](Decision_Log.md) | Governance | Lead Dev / Approver | Catatan keputusan arsitektural resmi (*Architecture Decision Record - ADR*). |
| **TPL-GOV-002** | [`Docs/Implementation_plan.md`](Implementation_plan.md) | Execution | Lead AI Developer | Rencana kerja bertahap (*step-by-step*) & gerbang persetujuan. |
| **TPL-GOV-003** | [`Docs/Approval_Governance.md`](Approval_Governance.md) | Governance | Designated Approver | Spesifikasi batasan otonom, whitelist file lingkup, dan pemicu persetujuan ulang. |
| **TPL-TSK-001** | [`Docs/Task_Specification_Template.md`](Task_Specification_Template.md) | Task Management | AI Manager / Coordinator | Spesifikasi tugas operasional terukur (`TSK-[MOD]-[NUM]`) & DoR/DoD. |
| **TPL-TSK-002** | [`Docs/Task_Status.md`](Task_Status.md) | Task Management | AI Manager / Lead Dev | Papan pelacak kemajuan siklus hidup 10 status tugas proyek aktif. |
| **TPL-TRC-001** | [`Docs/Traceability_Matrix.md`](Traceability_Matrix.md) | Traceability | QA / Lead Dev | Matriks silsilah ketertelusuran terpadu (REQ → TASK → DEC → TEST → DEF → INC → SKL → EVID). |
| **TPL-AGT-001** | [`Docs/Agent_Handoff_Template.md`](Agent_Handoff_Template.md) | Coordination | Seluruh Agen Pelaksana | Formulir serah terima tugas 11 bidang informasi wajib tanpa *context loss*. |
| **TPL-AGT-002** | [`Docs/Agent_Authority_Matrix.md`](Agent_Authority_Matrix.md) | Governance | Designated Approver / Mgr | Matriks batas kewenangan diskrit (Read, Modify, Commit, Push, Merge, Deploy, Approve). |
| **TPL-AGT-003** | [`Docs/Agent_Definition_Template.md`](Agent_Definition_Template.md) | Agent Design | Lead Dev / Architect | Formulir baku pendefinisian profil agen AI (13 atribut profil wajib). |
| **TPL-AGT-004** | [`Docs/Agent_Performance_Report.md`](Agent_Performance_Report.md) | Accountability | AI Manager / Reviewer | Laporan evaluasi kuantitatif kinerja agen berbasis 8 metrik capaian. |
| **TPL-AGT-005** | [`Docs/Agent_Incident_Log.md`](Agent_Incident_Log.md) | Resilience | Lead Dev / QA Engineer | Log pelacak insiden kegagalan operasional (`INC-[CAT]-[NUM]`) & pemulihan. |
| **TPL-AGT-006** | [`Docs/Standards/Agent_Permission_Enforcement_Spec.md`](Standards/Agent_Permission_Enforcement_Spec.md) | Enforcement | Platform Architect | Spesifikasi teknis runtime interception dan pencegahan eksekusi destruktif. |
| **TPL-COM-001** | [`Docs/Communication_Protocol.md`](Communication_Protocol.md) | Communication | Seluruh Agen Pelaksana | Format pesan internal terstruktur (7 tipe pesan baku: `MSG-UPDATE`, dll.). |
| **TPL-SKL-001** | [`skills/template/SKILL.md`](../skills/template/SKILL.md) | Skill Design | AI Skill Engineer | Master template rujukan pembuatan SOP kemampuan operasional (18 atribut). |
| **TPL-SKL-002** | [`Docs/Skill_Specification_Template.md`](Skill_Specification_Template.md) | Skill Design | Skill Designer | Formulir perancangan awal spesifikasi kebutuhan modul skill baru. |
| **TPL-SKL-003** | [`Docs/Skill_Validation_Checklist.md`](Skill_Validation_Checklist.md) | Skill Testing | AI QA / Reviewer | Daftar periksa audit 6 dimensi validasi skill sebelum promosi status. |
| **TPL-SKL-004** | [`Docs/Skill_Evaluation_Report.md`](Skill_Evaluation_Report.md) | Skill Testing | AI QA Engineer | Laporan bukti pelaksanaan pengujian & benchmarking performa skill. |
| **TPL-SKL-005** | [`Docs/Skill_Change_Request.md`](Skill_Change_Request.md) | Skill Maintenance | Skill Maintainer | Formulir permohonan modifikasi/pembaruan modul skill. |
| **TPL-SKL-006** | [`Docs/Skill_Deprecation_Report.md`](Skill_Deprecation_Report.md) | Skill Maintenance | Skill Maintainer | Laporan penghentian operasional dan pengarsipan modul skill yang usang. |
| **TPL-SKL-007** | [`Docs/Skill_Registry.md`](Skill_Registry.md) | Skill Catalog | Lead Dev / Approver | Registri master katalog skill aktif dan status validasi seluruh proyek. |
| **TPL-QAS-001** | [`Docs/Test_Report.md`](Test_Report.md) | QA & Testing | AI QA Engineer | Laporan hasil pengujian multi-tier, status asersi, dan bukti kelulusan gate. |
| **TPL-QAS-002** | [`Docs/Defect_Log.md`](Defect_Log.md) | QA & Testing | AI QA / Reviewer | Log pencatatan dan pelacakan cacat perangkat lunak (`DEF-[MOD]-[NUM]`). |
| **TPL-QAS-003** | [`Docs/Verification_Checklist.md`](Verification_Checklist.md) | Quality Gate | QA / Lead Dev | Daftar periksa kriteria penerimaan 5 Gerbang Kualitas sebelum rilis. |
| **TPL-MOD-001** | [`Docs/AGENTS-module-template.md`](AGENTS-module-template.md) | Modular Governance | Sub-module Lead | Template instruksi koding khusus untuk sub-direktori atau modul spesifik. |
| **TPL-CHG-001** | [`Docs/Documentation_Change_Request.md`](Documentation_Change_Request.md) | Maintenance | AI Engineering Team | Formulir formal permohonan revisi standar atau kebijakan yang telah disahkan. |
| **TPL-HLT-001** | [`Docs/Repository_Health_Checklist.md`](Repository_Health_Checklist.md) | Maintenance | Lead Dev / Maintainer | Lembar kerja audit berkala kebersihan, link, dan kesehatan repositori. |
| **TPL-LOG-001** | [`changelog/00changelog-template.md`](../changelog/00changelog-template.md) | Release Record | Lead AI Developer | Template flat changelog rilis kronologis berurut 2-digit. |

---

## 3. Aturan Penggunaan Template

1. **Integritas Format:** Pengguna dilarang menghapus bagian wajib (*mandatory sections*) pada template.
2. **Kustomisasi Berizin:** Bagian opsional (*optional sections*) dapat disesuaikan atau dihilangkan apabila tidak relevan dengan konteks proyek, dengan mencantumkan keterangan `N/A (Not Applicable)` beserta justifikasi sah.
3. **Penyimpanan Berkas Hasil Isian:** Berkas isian template disimpan pada direktori kerja proyek sesuai peruntukannya (misal: dokumen arsitektur di `Docs/`, rencana kerja di `Docs/Implementation_plan/`, pengujian di `Docs/Test_Report.md`).
