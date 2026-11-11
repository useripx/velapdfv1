<!-- LOGO / BRANDING PROYEK (Tanyakan kepada Designated Approver apakah ingin dicantumkan) -->
<!-- <p align="center"><img src="path/to/logo.png" alt="Project Logo" width="120"></p> -->

# [Isi Manual: Nama Proyek]

<!-- BADGE STATUS REPOSITORI GITHUB (Wajib) -->
[![Forks](https://img.shields.io/github/forks/[User]/[Repo]?style=flat-square)](https://github.com/[User]/[Repo]/network/members)
[![Repo Size](https://img.shields.io/github/repo-size/[User]/[Repo]?style=flat-square)](https://github.com/[User]/[Repo])
[![Stars](https://img.shields.io/github/stars/[User]/[Repo]?style=flat-square)](https://github.com/[User]/[Repo]/stargazers)

> **DESIGNATED APPROVER / PROJECT OWNER:** Rujuk [config/organization.md](./config/organization.md) (saat ini: Yogi Ario)  
> **DESKRIPSI SINGKAT:** [Isi Manual: Deskripsi 1-2 kalimat mengenai tujuan aplikasi/game/sistem ini.]  
> **ENAM PRINSIP TATA KELOLA:**
> 1. *No Understanding, No Execution.*
> 2. *No Plan, No Code.*
> 3. *No Approval, No Code.*
> 4. *No Verification, No Completion.*
> 5. *No Evidence, No Completion.*
> 6. *No Validated Skill, No Production Use.*

---

## 🚀 Panduan Memulai Cepat (Quickstart Android Kotlin)

### 1. Prasyarat Lingkungan
- Android Studio (versi kompatibel dengan AGP proyek) & JDK (17 / sesuai `gradle-wrapper.properties`)
- Android SDK (`minSdk` & `targetSdk` sesuai konfigurasi `build.gradle*`)
- Git

### 2. Persiapan Lingkungan & Sinkronisasi Gradle
```bash
# Pastikan local.properties mengarah ke Android SDK lokal (JANGAN di-commit)
# Contoh isi local.properties: sdk.dir=C:\\Users\\<User>\\AppData\\Local\\Android\\Sdk

# Unduh dependensi & verifikasi build debug awal
./gradlew assembleDebug
```

### 3. Menjalankan Aplikasi pada Emulator / Perangkat
```bash
# Instalasi APK Debug ke emulator/perangkat yang terhubung ADB
./gradlew installDebug
```

### 4. Menjalankan Test Suite & Lint (Mandatory Quality Gate)
```bash
# Jalankan JVM Unit Test
./gradlew testDebugUnitTest

# Jalankan Android Lint Static Check
./gradlew lintDebug
```

---

## 🏛 Aturan & Tata Kelola Proyek

Setiap pengembang maupun AI Agent yang bekerja pada repositori ini **wajib membaca dan mematuhi**:
1. [`AGENTS.md`](./AGENTS.md) — Aturan kerja operasional AI Agent Level 3 (Android Kotlin MVVM & Handover).
2. [`GEMINI.md`](./GEMINI.md) — Indeks ringkas aturan kerja agen Gemini / LLM Level 3.
3. [`Docs/Rules/Android_Kotlin_MVVM_Rules.md`](./Docs/Rules/Android_Kotlin_MVVM_Rules.md) — Aturan teknis Kotlin, MVVM, Gradle Lock, Coroutines & Android Security.
4. [`Docs/Rules/Legacy_Handover_Protocol.md`](./Docs/Rules/Legacy_Handover_Protocol.md) — Protokol wajib Fase 0 audit codebase pengembang sebelumnya & larangan *mass refactoring*.
5. [`Docs/universal-rule.md`](./Docs/universal-rule.md) — Kerangka standar tata kelola tertinggi Level 1 Universal Governance (8 Pilar Kerja & 6 Prinsip Tata Kelola).
6. [`config/organization.md`](./config/organization.md) — Konfigurasi identitas organisasi, persona agen, dan otoritas persetujuan.
7. [`Docs/Governance_Hierarchy.md`](./Docs/Governance_Hierarchy.md) — Spesifikasi hierarki tata kelola Level 1, 2, dan 3.
8. [`Docs/Documentation_Hierarchy.md`](./Docs/Documentation_Hierarchy.md) — Panduan navigasi hierarki dokumentasi Level 1 s.d. Level 6.
9. [`Docs/Single_Source_of_Truth_Matrix.md`](./Docs/Single_Source_of_Truth_Matrix.md) — Matriks sumber otoritatif tunggal (SSOT) seluruh domain aturan.
10. [`Docs/Template_Registry.md`](./Docs/Template_Registry.md) — Registri master seluruh template operasional (25+ template).
11. [`Docs/Glossary.md`](./Docs/Glossary.md) — Glosarium resmi istilah baku rekayasa, QA, dan AI governance.
12. [`Docs/Approval_Governance.md`](./Docs/Approval_Governance.md) — Spesifikasi batasan otonom dan gerbang persetujuan Designated Approver.
13. [`Docs/Standards/Engineering_Standards.md`](./Docs/Standards/Engineering_Standards.md) — 7 Pilar Standar Rekayasa Perangkat Lunak Profesional, QA System, Skill System, Agent System & Documentation Standards.

---

## 📁 Struktur Direktori Proyek

```text
[Isi Manual: Nama Proyek Android]/
├── .gitignore                     # Perlindungan aset (.gradle, local.properties, *.jks, .env, build)
├── .env.example                   # Template konfigurasi tanpa kunci rahasia
├── local.properties               # [JANGAN DI-COMMIT] Konfigurasi SDK & key lokal
├── build.gradle(.kts)             # Konfigurasi Gradle proyek
├── settings.gradle(.kts)          # Konfigurasi modul Gradle
├── README.md                      # Dokumentasi pengantar repositori
├── AGENTS.md                      # Indeks aturan kerja operasional AI Agent Level 3
├── GEMINI.md                      # Indeks ringkas AI Agent Level 3
│
├── config/                        # Konfigurasi organisasi & identitas proyek
│   └── organization.md            # Entitas organisasi, Designated Approver & persona agen
│
├── Docs/                          # Seluruh dokumentasi formal proyek
│   ├── Rules/                     # Aturan Level 3 spesifik platform & handover
│   │   ├── Android_Kotlin_MVVM_Rules.md
│   │   └── Legacy_Handover_Protocol.md
│   ├── chat/                      # Arsip riwayat percakapan sesi AI berisiko tinggi / milestone
│   ├── universal-rule.md          # Dokumen aturan master tata kelola Level 1
│   ├── Governance_Hierarchy.md    # Spesifikasi hierarki Level 1, 2, dan 3
│   ├── Approval_Governance.md     # Spesifikasi gerbang persetujuan Designated Approver
│   ├── Project_Analysis.md        # Template audit Fase 0 codebase pengembang lama & kondisi proyek
│   ├── SRS_Template.md            # Template spesifikasi kebutuhan perangkat lunak (Step 1 Workflow)
│   ├── Task_Status.md             # Pelacak progres fase dan checklist tugas
│   ├── Decision_Log.md            # Catatan riwayat keputusan arsitektur (ADR)
│   ├── Test_Report.md             # Laporan hasil verifikasi pengujian
│   ├── Defect_Log.md              # Pelacak defect & bug terdeteksi (Severity vs Priority)
│   ├── Verification_Checklist.md  # Checklist verifikasi 5 Quality Gates sebelum rilis
│   ├── API_Contract.md            # Kontrak endpoint & arsitektur data (bila ada API)
│   ├── Database_Design.md         # Perancangan skema database & kamus data (bila ada DB)
│   ├── Implementation_plan.md     # Rencana induk implementasi aktif
│   ├── Implementation_plan/       # Arsip runut dokumen rencana kerja yang telah fix
│   │   └── <nomor>_tahap<nomor>_Implementation_plan_<namafitur>.md
│   ├── Standards/                 # Standar Rekayasa, QA, Skill, Agent & Doc Governance
│   │   ├── Engineering_Standards.md      # Master Framework, 12-Step Workflow, DoR/DoD, Risk Matrix
│   │   ├── Requirements_Engineering.md   # Spesifikasi REQ-F/NF, acceptance criteria, RTM
│   │   ├── Architecture_Design.md        # Modularitas, data flow, ADR trade-off
│   │   ├── Coding_Standards.md           # Clean & Surgical coding, error handling, logging
│   │   ├── Git_Version_Control.md        # Branching, clean main, Conventional Commits, flat changelog
│   │   ├── Database_API_Standards.md     # Skema DB, migrasi, kontrak REST API
│   │   ├── Security_Engineering.md       # Zero Secret Leakage, OWASP Top 10, RBAC
│   │   ├── Deployment_Maintenance.md     # Environment, checklist rilis, incident recovery
│   │   ├── QA_Framework.md               # Master QA Framework & 15-Step Workflow
│   │   ├── Testing_Strategy.md           # Multi-Tier & Risk-Based Testing Strategy
│   │   ├── Test_Case_Management.md       # Format Kasus Uji TC-[MODULE]-[NUM] & RTM
│   │   ├── Defect_Management.md          # Format Defect DEF-[MODULE]-[NUM] & 8-Step Lifecycle
│   │   ├── Verification_Evidence.md      # Standar Bukti Nyata & Multi-Modal Verification
│   │   ├── Skill_Architecture.md         # Master Arsitektur Skill AI, 18 atribut SKILL.md
│   │   ├── Skill_Lifecycle_Governance.md # 9 Tahap Siklus Hidup & Hak Akses Peran
│   │   ├── Skill_Validation_Testing.md   # Validasi 6 Dimensi & Rubrik Mutu
│   │   ├── Agent_Architecture_Governance.md # Arsitektur Peran Agen & Role Separation
│   │   ├── Agent_Orchestration_Handoff.md   # Model Koordinasi & Handoff 11 Atribut
│   │   ├── Tool_Governance_Execution.md     # Tata Kelola Kategori Alat & Least Privilege
│   │   ├── Agent_Permission_Enforcement_Spec.md # Spesifikasi Penegakan Izin Teknis Agen
│   │   ├── Error_Handling_Recovery.md       # Kategori Error & Bounded Idempotent Recovery
│   │   ├── Documentation_Standards.md       # Master Tata Kelola Dokumentasi, Hierarki 6 Tingkat & RFC 2119
│   │   ├── Documentation_Change_Management.md # Manajemen Perubahan Dokumen, SemVer Standar & Rollback
│   │   └── Repository_Health_Standard.md    # Standar Audit Kesehatan Repositori & Broken Link Check
│   ├── Documentation_Hierarchy.md # Panduan visual hierarki Level 1 s.d. Level 6
│   ├── Single_Source_of_Truth_Matrix.md # Matriks pemetaan sumber otoritatif tunggal (SSOT)
│   ├── Template_Registry.md       # Registri master seluruh template operasional (25+ template)
│   ├── Traceability_Matrix.md     # Matriks ketertelusuran REQ -> TASK -> TEST -> DEF -> EVID
│   ├── Glossary.md                # Glosarium resmi istilah baku tata kelola, rekayasa & QA
│   ├── Documentation_Change_Request.md # Formulir permohonan perubahan standar master
│   ├── Repository_Health_Checklist.md # Lembar kerja audit kesehatan repositori
│   ├── Skill_Registry.md          # Registri master katalog skill aktif seluruh proyek
│   ├── Agent_Authority_Matrix.md  # Matriks hak akses diskrit & batas kewenangan agen
│   ├── Agent_Handoff_Template.md  # Formulir resmi protokol serah terima tugas (11 atribut)
│   ├── Agent_Incident_Log.md      # Log pelacak insiden kegagalan operasional agen
│   └── Evidence/                  # Direktori bukti verifikasi dan tangkapan layar pengujian
│       └── SS/
│           ├── valid/             # Bukti hasil pengujian valid / lulus
│           └── error/             # Bukti kendala / error pengujian
│
├── agents/                        # [PROFIL OPERASIONAL AGEN] Spesifikasi resmi peran agen AI
│   ├── README.md                  # Panduan repositori profil agen
│   ├── lead-developer.md          # Profil persona Lead Developer (AGT-PRD-DEV-001)
│   ├── ai-manager.md              # Profil persona AI Project Manager (AGT-MGT-MGR-001)
│   ├── qa-agent.md                # Profil persona AI QA Engineer (AGT-QLT-QAE-001)
│   └── reviewer-agent.md          # Profil persona AI Technical Reviewer (AGT-QLT-REV-001)
│
├── skills/                        # [MODUL SKILL AI] Prosedur operasional baku AI Agent (SOP)
│   ├── README.md                  # Panduan repositori skill
│   ├── template/
│   │   └── SKILL.md               # Master template rujukan SKILL.md (18 atribut)
│   └── software-engineering/
│       └── requirement-analysis/
│           └── SKILL.md           # Seed Validated Skill (SKL-ENG-REQ-001)
│
├── changelog/                     # [FLAT] Catatan perubahan bertimestamp berurut kronologis
│   ├── 00changelog-template.md    # Template rujukan catatan perubahan
│   └── <nomor-urut>changelog<jam><ampm><ddmmyyyy>.md # Pola urut kronologis 2-digit
│
├── Material/                      # [READ-ONLY] Aset mentah dari tim UI/UX / Desainer
│
└── app/                           # Modul utama Android Kotlin MVVM
    └── src/
        ├── main/                  # Kode sumber & res Android eksisting
        ├── test/                  # JVM Unit Tests
        └── androidTest/           # Instrumented UI Tests
```

---

## 🌿 Alur Kerja Git & Rilis

- **`main`:** Cabang produksi stabil. Menampung kode aplikasi rilis dan aset produksi terverifikasi. Berkas transien non-produksi (seperti tangkapan layar evidensi sementara, log percakapan mentah, dan file scratch lokal) dilarang masuk ke cabang ini. Merge ke `main` membutuhkan Otorisasi Produksi eksplisit oleh Designated Approver.
- **`develop`:** Cabang integrasi utama. Seluruh fitur dan perbaikan yang telah lulus verifikasi digabungkan ke sini.
- **`feature/<nomor>_<nama_fitur>`:** Cabang pengerjaan fitur mandiri bertahap (contoh: `feature/01_auth_pengguna`).
- **`fix/<nama_bug_atau_yang_diperbaiki>`:** Cabang perbaikan bug khusus (contoh: `fix/salah_hitung_diskon`).

---

> [!tip] PETUNJUK KHUSUS
> Sesuaikan isi berkas ini dengan identitas proyek Anda saat menyalin ke repositori baru. Hapus catatan tip ini setelah dokumen disesuaikan.
