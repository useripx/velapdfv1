# MATRIKS HAK AKSES & KEWENANGAN DISKRIT AGEN AI (AGENT AUTHORITY & PERMISSION MATRIX)

> **DOCUMENT ID:** DOC-GOV-AAM-001  
> **VERSION:** v2.0.0  
> **STATUS:** AKTIF & MENGIKAT (LEVEL 2 OPERATIONAL STANDARD)  
> **TERAKHIR DIPERBARUI:** 07/11/2026  
> **PENANGGUNG JAWAB:** Lead Developer Role (Rujuk [config/organization.md](../config/organization.md))  
> **OTORISASI TERTINGGI:** Designated Approver (Rujuk [config/organization.md](../config/organization.md), saat ini: **Yogi Ario**)  
> **PRINSIP DASAR:**  
> - *Every Agent Has a Role, Every Role Has Authority Limits, Every Action Has Accountability.*  
> - *Principle of Least Privilege (Zero Wildcard Permissions).*  
> - *Explicit Boundary Enforcement (Runtime, Tool, Git).*

---

## 1. Definisi Tingkat Kewenangan (Authority Levels)

Setiap tindakan teknis yang dapat dilakukan dalam repositori dikendalikan oleh salah satu dari 4 status kewenangan formal:

| Simbol | Tingkat Kewenangan | Definisi Operasional & Aturan Eksekusi |
| :---: | :--- | :--- |
| **`[A]`** | **Autonomous (Mandiri)** | Peran berwenang mengeksekusi secara otonom dalam koridor tugas yang ditentukan tanpa perlu meminta persetujuan terlebih dahulu. |
| **`[N]`** | **Notify (Wajib Notifikasi)**| Peran boleh mengeksekusi, namun **wajib** mencatat dan memberitahukan hasilnya kepada Designated Approver. |
| **`[R]`** | **Approval-Required (Wajib Izin)**| Tindakan **DILARANG** dieksekusi sebelum memperoleh persetujuan tertulis eksplisit dari Designated Approver melalui rencana kerja atau permintaan formal. |
| **`[X]`** | **Forbidden (Dilarang Keras)**| Tindakan **DILARANG MUTLAK** dilakukan oleh peran tersebut dalam kondisi apa pun. |

---

## 2. Matriks Hak Akses Peran terhadap Aksi Teknis Diskrit

Matriks ini membagi perizinan secara granular untuk mengeliminasi izin menyeluruh (*no "Full Access" wildcards*):

| No | Aksi Teknis Diskrit | Designated Approver | AI Manager (`AGT-MGT`) | Lead Dev (`AGT-PRD`) | QA Engineer (`AGT-QLT-QAE`) | Reviewer (`AGT-QLT-REV`) | Researcher (`AGT-RES`) |
| :-: | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **1** | **Read Workspace & Documentation** | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` |
| **2** | **Analyze & Context Inspection** | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` |
| **3** | **Propose Implementation Plan** | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` |
| **4** | **Create Non-Code Files (Docs/Plans)** | `[A]` | `[N]` | `[N]` | `[N]` *(Test docs)* | `[X]` | `[N]` *(Reports)* |
| **5** | **Create New Source Code Files** | `[A]` | `[X]` | `[R]` *(Plan Disetujui)* | `[X]` | `[X]` | `[X]` |
| **6** | **Modify Existing Source Code** | `[A]` | `[X]` | `[R]` *(Plan Disetujui)* | `[X]` | `[X]` | `[X]` |
| **7** | **Delete Files / Destructive Edit** | `[A]` | `[X]` | `[R]` *(Spesifik File)* | `[X]` | `[X]` | `[X]` |
| **8** | **Execute Read-Only Linters & Tests** | `[A]` | `[A]` | `[A]` | `[A]` | `[A]` | `[X]` |
| **9** | **Execute Build & Auto-Fix Tools** | `[A]` | `[X]` | `[R]` *(Plan Disetujui)* | `[X]` | `[X]` | `[X]` |
| **10**| **Approve Implementation Plan** | `[A]` | `[X]` | `[X]` | `[X]` | `[X]` | `[X]` |
| **11**| **Approve Technical Quality Gates (QG1-QG4)**| `[A]` | `[N]` | `[X]` *(Self-review forbidden)* | `[A]` *(DoD QA)* | `[A]` *(Code Audit)* | `[X]` |
| **12**| **Authorize Production Release (Gate 5)** | `[A]` | `[X]` | `[X]` | `[X]` | `[X]` | `[X]` |
| **13**| **Git Checkout / Create Feature Branch** | `[A]` | `[N]` | `[N]` | `[N]` | `[N]` | `[X]` |
| **14**| **Git Local Commit (Feature Branch)** | `[A]` | `[N]` | `[N]` | `[N]` *(Docs test)*| `[X]` | `[X]` |
| **15**| **Git Push to Feature Branch (Remote)** | `[A]` | `[N]` | `[N]` | `[N]` | `[X]` | `[X]` |
| **16**| **PR Technical Review Sign-off** | `[A]` | `[N]` | `[X]` | `[A]` | `[A]` | `[X]` |
| **17**| **Git Merge Feature Branch to `develop`** | `[A]` | `[X]` | `[R]` *(Setelah QG1-4)* | `[X]` | `[X]` | `[X]` |
| **18**| **Git Merge to `main` / Tag Release** | `[A]` | `[X]` | `[X]` | `[X]` | `[X]` | `[X]` |
| **19**| **Deploy to Staging Environment** | `[A]` | `[N]` | `[R]` | `[X]` | `[X]` | `[X]` |
| **20**| **Deploy to Production Environment** | `[A]` | `[X]` | `[X]` | `[X]` | `[X]` | `[X]` |
| **21**| **Access Production Secrets / Credentials**| `[A]` | `[X]` | `[X]` | `[X]` | `[X]` | `[X]` |

---

## 3. Penjelasan Batasan Khusus Tiap Peran

### 3.1 Designated Approver (Project Owner)
- Memiliki hak veto mutlak atas seluruh rencana implementasi, perubahan arsitektur, dan perizinan rilis.
- Merupakan **satu-satunya pihak** yang berwenang memberikan persetujuan rencana kerja (*Approval Gate - Prinsip 3*) dan otorisasi produksi untuk merge ke cabang `main` (*Gate 5*).
- Identitas pemegang otoritas ini dikonfigurasi dalam `config/organization.md`.

### 3.2 AI Manager (`AGT-MGT`)
- Berwenang mengoordinasikan delegasi tugas, merangkum status pada `Docs/Task_Status.md`, dan mengevaluasi ketercapaian jadwal.
- **DILARANG KERAS:** Mengubah aturan tata kelola Level 1, memodifikasi kode sumber aplikasi secara langsung, atau memberikan persetujuan rilis menggantikan Designated Approver.

### 3.3 AI Lead Developer (`AGT-PRD-DEV`)
- Berwenang menyusun rencana kerja teknis, melakukan perubahan kode terukur (*clean & surgical coding*), dan menjalankan verifikasi pengujian lokal.
- **DILARANG KERAS:** Menulis atau mengedit kode aplikasi sebelum *Implementation Plan* disetujui resmi; dilarang menyetujui hasil kerjanya sendiri tanpa review independen dari peran QA/Reviewer.

### 3.4 AI QA Engineer (`AGT-QLT-QAE`)
- Berwenang merancang kasus uji, mengeksekusi suite pengujian, mencatat rekaman evidensi di `Docs/Evidence/`, mengelola pelacak bug pada `Docs/Defect_Log.md`, dan menandatangani pemenuhan kriteria teknis QG2 & QG4.
- **DILARANG KERAS:** Menutup defect tanpa pengujian ulang (*retesting*), mengubah kode sumber aplikasi secara langsung, atau memanipulasi bukti kelulusan uji.

### 3.5 AI Technical Reviewer (`AGT-QLT-REV`)
- Berwenang melakukan audit kepatuhan kode terhadap `Docs/Standards/Coding_Standards.md`, mendeteksi kelemahan keamanan/vulnerabilities, dan memberikan umpan balik perbaikan pada PR.
- **DILARANG KERAS:** Memodifikasi kode secara sepihak selama proses review; seluruh temuan wajib dikembalikan ke Lead Developer melalui proses handoff formal.

### 3.6 AI Researcher (`AGT-RES`)
- Berwenang mengekstrak fakta, membaca dokumen referensi eksternal, dan menyusun laporan sintesis data.
- **DILARANG KERAS:** Menyentuh file kode atau mengubah spesifikasi teknis proyek.

---

## 4. Mekanisme Penegakan Teknis (Technical Enforcement Mechanism)

Matriks kewenangan ini tidak hanya berupa aturan tekstual, melainkan ditegakkan melalui tiga lapis kendali teknis (*three-tier technical enforcement*):
1. **Tool Invocation Wrapper:** Setiap pemanggilan tool modifikasi berkas atau eksekusi perintah shell divalidasi terhadap ID peran agen yang aktif.
2. **Git Branch Protection Rules:** Cabang `main` dan `develop` dilindungi melalui aturan proteksi repositori (larangan direct push, mensyaratkan PR approval dan status checks passing).
3. **Approval Gate Checkpoint:** Validasi keberadaan stempel persetujuan bertanggal dari Designated Approver sebelum transisi status tugas diizinkan.

Rincian implementasi teknis diatur dalam [Docs/Standards/Agent_Permission_Enforcement_Spec.md](Standards/Agent_Permission_Enforcement_Spec.md).

---

## 5. Prosedur Eskalasi Pelanggaran Kewenangan (Violation Escalation)

Jika suatu agen menerima instruksi yang melampaui batas kewenangannya (misal: diminta melakukan push langsung ke cabang `main`):
1. Agen **MUST NOT** mematuhi perintah tersebut secara membabi buta.
2. Agen **MUST** menolak tindakan tersebut secara santun dengan mengutip dokumen matriks ini.
3. Agen **MUST** melaporkan status penolakan kepada Designated Approver untuk mendapatkan arahan yang sah.

---

## 6. Rujukan Dokumen Terkait
- Arsitektur Agen: [Docs/Standards/Agent_Architecture_Governance.md](Standards/Agent_Architecture_Governance.md)
- Spesifikasi Penegakan Izin Teknis: [Docs/Standards/Agent_Permission_Enforcement_Spec.md](Standards/Agent_Permission_Enforcement_Spec.md)
- Tata Kelola Alat: [Docs/Standards/Tool_Governance_Execution.md](Standards/Tool_Governance_Execution.md)
- Level 1 Governance: [Docs/universal-rule.md](universal-rule.md)
- Tata Kelola Persetujuan: [Docs/Approval_Governance.md](Approval_Governance.md)
- Konfigurasi Organisasi: [config/organization.md](../config/organization.md)
