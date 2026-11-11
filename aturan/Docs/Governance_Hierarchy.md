# Governance Hierarchy Specification

> **DOCUMENT ID:** GOV-HIE-001  
> **VERSION:** v1.1.0 (Comprehensive Revision)  
> **STATUS:** DOKUMEN SPESIFIKASI TATA KELOLA (GOVERNANCE SPECIFICATION)  
> **OTORITAS TERTINGGI:** Project Owner (Dikonfigurasi dalam `config/organization.md`, default: Yogi Ario)  
> **TARGET PENGGUNA:** Seluruh Pengembang dan AI Agent  
> **ACUAN UTAMA:** [Docs/universal-rule.md](universal-rule.md) (Level 1 Universal Governance)  
> **NAVIGASI LENGKAP:** [Docs/Documentation_Hierarchy.md](Documentation_Hierarchy.md) & [Docs/Single_Source_of_Truth_Matrix.md](Single_Source_of_Truth_Matrix.md)  

---

## 1. Pendahuluan

Dokumen ini mendefinisikan arsitektur dan hierarki tata kelola (*Governance Hierarchy*) yang diterapkan pada seluruh repositori dan proyek di bawah ekosistem tata kelola organisasi. Tujuan utama hierarki ini adalah memastikan konsistensi standar rekayasa (*engineering standards*), menghilangkan konflik aturan, memperjelas batasan kewenangan agen, serta memberikan alur pewarisan aturan yang tegas dari prinsip universal hingga artefak bukti di lapangan.

---

## 2. Struktur Hierarki Tata Kelola Komprehensif (6-Level Governance Model)

Sistem tata kelola dibagi menjadi 6 tingkatan otoritas formal:

```mermaid
graph TD
    subgraph Level 1 — Universal Governance
        L1["Docs/universal-rule.md<br/><b>Otoritas Tertinggi Lintas Proyek & Agen</b><br/>• 6 Prinsip Tata Kelola<br/>• Zero-Code Rule & Approval Gate<br/>• Zero Secret Leakage & Clean main Policy"]
    end

    subgraph Level 2 — Professional Standards
        L2["Docs/Standards/*.md<br/><b>Standar Mutu Rekayasa, QA, Skill, Agen & Doc</b><br/>• 7 Pilar Rekayasa Perangkat Lunak<br/>• QA Framework & Risk-Based Testing<br/>• Skill & Agent Architecture Standards"]
    end

    subgraph Level 3 — Agent Instructions
        L3["AGENTS.md, GEMINI.md, agents/*.md<br/><b>Petunjuk Operasional Lingkungan Agen & Profil</b><br/>• Clean & Surgical Coding Guidelines<br/>• Matriks Wewenang & Batasan Peran"]
    end

    subgraph Level 4 — Skills & Procedures
        L4["skills/README.md, skills/**/SKILL.md<br/><b>SOP Kemampuan Operasional Tervalidasi</b><br/>• 18 Atribut SKILL.md<br/>• Pengujian Mutu 6 Dimensi"]
    end

    subgraph Level 5 — Templates & References
        L5["Docs/*.md, Template_Registry.md, Glossary.md<br/><b>Kamus Istilah, Formulir Baku & Registri</b><br/>• Single Source of Truth Matrix<br/>• 28 Template Operasional Terstandarisasi"]
    end

    subgraph Level 6 — Project-Specific Implementation & Evidence
        L6["Docs/Implementation_plan/, changelog/, Evidence/<br/><b>Catatan Empiris & Bukti Lapangan</b><br/>• Implementation Plans Sekuensial<br/>• Log Keputusan (ADR) & Bukti Pengujian"]
    end

    L1 -->|Mengikat Mutlak| L2
    L2 -->|Mengarahkan Disiplin| L3
    L3 -->|Mengeksekusi Prosedur| L4
    L4 -->|Menggunakan Format| L5
    L5 -->|Menghasilkan Bukti| L6
```

---

## 3. Rincian dan Peran Setiap Tingkat

### Level 1 — Universal Governance (`Docs/universal-rule.md`, `Approval_Governance.md`)
- **Ruang Lingkup:** Berlaku mutlak lintas proyek, repositori, framework, dan jenis AI Agent.
- **Karakteristik:** Standar makro yang statis, stabil, dan menjadi landasan etika kerja profesional.
- **Isi Pokok:**
  1. Enam prinsip tata kelola (*No Understanding, No Plan, No Approval, No Verification, No Evidence, No Validated Skill*).
  2. Pembedaan definisi kunci: *Verification, Evidence, Validation, Approval, Production Authorization*.
  3. Standar kepatuhan RFC 2119 (*MUST, MUST NOT, SHOULD, SHOULD NOT, MAY*).
  4. Protokol gerbang persetujuan Project Owner (*Zero-Code Rule*).
  5. Keamanan kredensial (*Zero Secret Leakage*) dan perlindungan konfigurasi `.env`.
  6. Kebijakan integritas branch `main` (*Clean Main Policy*).

### Level 2 — Professional Standards (`Docs/Standards/*.md`)
- **Ruang Lingkup:** Berlaku untuk disiplin teknis perancangan, koding, pengujian, tata kelola skill, dan tata kelola agen.
- **Karakteristik:** Standar teknis yang mendefinisikan kriteria mutu profesional.
- **Isi Pokok:**
  1. 7 Pilar Standar Rekayasa Perangkat Lunak & 12-Step Engineering Workflow.
  2. QA Framework terpadu (membedakan Universal QA dan Software QA).
  3. Skill Architecture (18 atribut) dan tata kelola 9 status siklus hidup.
  4. Agent Architecture, matriks wewenang 10 aksi diskrit, orkestrasi & handoff 11 bidang data.
  5. Standar dokumentasi, SemVer standar, dan standar audit kesehatan repositori.

### Level 3 — Agent Instructions (`AGENTS.md`, `GEMINI.md`, `agents/*.md`)
- **Ruang Lingkup:** Berlaku untuk interaksi agen AI pada workspace proyek aktif.
- **Karakteristik:** Panduan operasional yang menjembatani aturan universal dengan kebiasaan teknis AI runtime.
- **Isi Pokok:**
  1. Identitas proyek spesifik, framework, bahasa pemrograman, dan sasaran MVP.
  2. Implementasi praktis dari Zero-Code Approval Gate dan Chained Implementation Plan.
  3. Empat standar presisi penulisan kode (*Think Before Coding, Simplicity First, Surgical Changes, Goal-Driven Execution*).
  4. Profil operasional agen terdaftar di folder `agents/` (Lead Dev, AI Manager, QA, Reviewer).

### Level 4 — Skills & Procedures (`skills/`, `Skill_Registry.md`)
- **Ruang Lingkup:** Berlaku untuk prosedur operasional standar (SOP) kemampuan kerja AI.
- **Karakteristik:** Modul kemampuan teruji yang dapat digunakan berulang kali (*reusable SOPs*).
- **Isi Pokok:**
  1. Berkas operasional `SKILL.md` (18 atribut wajib).
  2. Kerangka pengujian mutu 6 dimensi berbasis risiko (*applicability & N/A beralasan*).
  3. Registri katalog master seluruh modul skill aktif proyek.

### Level 5 — Templates & References (`Docs/`, `Template_Registry.md`, `Glossary.md`)
- **Ruang Lingkup:** Berlaku untuk seluruh instrumen pencatatan, formulir baku, dan acuan istilah.
- **Karakteristik:** Format dokumen terstruktur dengan instruksi pengisian yang jelas.
- **Isi Pokok:**
  1. Matriks Sumber Otoritatif Tunggal (`Single_Source_of_Truth_Matrix.md`).
  2. Registri master template operasional (`Template_Registry.md`).
  3. Glosarium istilah baku resmi (`Glossary.md`).
  4. Formulir baku (SRS, ADR, Handoff, Incident Log, Defect Log, Change Request).

### Level 6 — Project Implementation & Evidence (`Docs/Implementation_plan/`, `changelog/`, `Docs/Evidence/`)
- **Ruang Lingkup:** Catatan empiris dan bukti nyata pelaksanaan pekerjaan spesifik.
- **Karakteristik:** Berkas yang mencatat fakta lapangan secara kronologis dan dapat diaudit.
- **Isi Pokok:**
  1. Rencana implementasi sekuensial fase.
  2. Laporan audit verifikasi gerbang mutu.
  3. Bukti verifikasi konkret (log uji, diff, hash, tangkapan layar di `Docs/Evidence/SS/`).
  4. Flat changelog bertimestamp berurut 2-digit kronologis.

---

## 4. Aturan Prevalensi dan Resolusi Konflik (Conflict Resolution)

Dalam hal ditemukan kontradiksi atau ketidakselarasan antar dokumen pada tingkat hierarki yang berbeda:

1. **Prinsip Prevalensi Hierarkis (Strict Hierarchy Priority):**  
   $$\text{Level 1} > \text{Level 2} > \text{Level 3} > \text{Level 4} > \text{Level 5} > \text{Level 6}$$
   Dokumen pada level yang lebih rendah **DILARANG KERAS** mengesampingkan atau membuat kelonggaran terhadap ketentuan level yang lebih tinggi tanpa izin tertulis dari Project Owner.

2. **Prosedur Penanganan Konflik (STOP & REPORT):**
   - AI Agent **MUST NOT** mengambil asumsi atau menyelesaikan konflik aturan secara sepihak.
   - AI Agent **MUST** menghentikan sementara pekerjaan pada bagian yang terdampak (*stop and hold*).
   - AI Agent **MUST** segera menyajikan laporan kontradiksi kepada Project Owner yang memuat:
     * File dan baris yang bertentangan.
     * Analisis implikasi teknis masing-masing opsi.
     * Rekomendasi penyelarasan sesuai Level 1 Universal Governance.
   - AI Agent hanya melanjutkan pekerjaan setelah Project Owner memberikan keputusan tertulis.

---

## 5. Tata Kelola Pembaruan Dokumen (Change Management)

1. Perubahan pada **Level 1 (Universal Governance)** berdampak pada seluruh repositori dan **MUST** disetujui secara formal oleh Project Owner melalui *Documentation Change Request* resmi.
2. Perubahan pada **Level 2 (Professional Standards)** dikendalikan melalui SemVer standar dan evaluasi dampak teknis.
3. Perubahan pada **Level 3 (Agent Instructions)** disesuaikan saat inisialisasi proyek baru atau saat pembaruan peran agen.
4. Perubahan pada **Level 4 (Skills)** wajib melalui siklus hidup skill dan validasi ulang berbasis dampak (*impact-based regression*).
