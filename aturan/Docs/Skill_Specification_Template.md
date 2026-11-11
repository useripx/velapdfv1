# TEMPLATE SPESIFIKASI KEBUTUHAN SKILL (SKILL SPECIFICATION TEMPLATE)

> **STATUS:** TEMPLATE OPERASIONAL  
> **KODE DOKUMEN:** TMPL-SKL-SPEC-001  
> **PENGGUNAAN:** Formulir pengajuan awal kebutuhan dan perancangan modul skill baru  
> **OTORISASI:** Yogi Ario (Project Owner)  

---

## 1. Metadata Pengajuan Skill

| Parameter | Keterangan / Nilai |
| :--- | :--- |
| **ID Pengajuan (Proposal ID)** | `PROP-SKL-[CAT]-[YYYYMMDD]-[NUM]` |
| **Nama Skill yang Diusulkan** | [Nama resmi kemampuan operasional] |
| **Kategori Skill** | [General \| Software Engineering \| Business \| Creative \| Domain-Specific] |
| **Agen Pengusul** | [Nama & Peran Agen Pengusul] |
| **Tanggal Pengajuan** | [DD/MM/YYYY] |
| **Peran Agen yang Dituju** | [Developer \| Manager \| Reviewer \| QA Agent \| Researcher \| Creator] |
| **Tingkat Prioritas** | [P1 - High \| P2 - Medium \| P3 - Low] |
| **Klasifikasi Risiko Awal** | [Low \| Medium \| High \| Critical] |

---

## 2. Latar Belakang & Kebutuhan Operasional (Operational Gap)

- **Masalah / Celah yang Dihadapi:**  
  [Jelaskan masalah, inefisiensi, atau variabilitas hasil yang saat ini terjadi karena agen bekerja tanpa SOP baku]
- **Tujuan Pembentukan Skill:**  
  [Jelaskan hasil terukur yang ingin dicapai melalui standarisasi prosedur kerja ini]
- **Nilai Tambah & Efisiensi:**  
  [Berapa perkiraan waktu yang dihemat, potensi pengurangan error, atau peningkatan konsistensi artefak]

---

## 3. Ruang Lingkup & Batasan (Scope & Boundaries)

| Kategori | Batasan Operasional |
| :--- | :--- |
| **In-Scope (Tercakup)** | 1. [Tugas spesifik yang ditangani]<br>2. [Kasus penggunaan yang didukung] |
| **Out-of-Scope (Dilarang / Di luar Lingkup)** | 1. [Tugas yang dilarang dieksekusi]<br>2. [Batasan sistem yang tidak boleh dilanggar] |
| **Batasan Teknis (Technical Constraints)**| [Batasan ukuran file, dependensi library, batas waktu eksekusi] |

---

## 4. Prasyarat & Kebutuhan Alat (Prerequisites & Tools)

- **Prasyarat Pengetahuan / Konteks:** [Dokumen atau basis data yang wajib dibaca agen sebelum mulai]
- **Daftar Alat Teknis (Required Tools):**
  - [ ] CLI Terminal (PowerShell / Bash)
  - [ ] File System Tools (Read / Write / Edit)
  - [ ] MCP Server: Chrome DevTools (Otomasi Browser)
  - [ ] Git Version Control
  - [ ] Lainnya: [Sebutkan]
- **Izin Khusus yang Diperlukan:** [Sebutkan bila memerlukan otorisasi eksternal atau akses jaringan]

---

## 5. Spesifikasi Masukan & Keluaran (I/O Specification)

### 5.1 Spesifikasi Masukan (Input Specification)
- **Tipe Masukan:** [Teks prompt \| Berkas Markdown \| JSON \| File Kode]
- **Parameter Wajib:**
  1. `parameter_1`: [Deskripsi & tipe data]
  2. `parameter_2`: [Deskripsi & tipe data]
- **Parameter Opsional:**
  1. `optional_param`: [Nilai default & deskripsi]

### 5.2 Spesifikasi Keluaran (Output Specification)
- **Format Artefak Keluaran:** [Dokumen Markdown \| JSON Schema \| Kode Terkompilasi]
- **Struktur Keluaran Baku:**
  ```markdown
  # [Judul Artefak]
  - Parameter 1: Nilai
  - Parameter 2: Nilai
  ```

---

## 6. Garis Besar Prosedur Eksekusi (Draft Procedure Outline)

1. **Langkah 1 (Persiapan):** [Tindakan persiapan & validasi masukan]
2. **Langkah 2 (Analisis / Pemrosesan):** [Logika pemrosesan utama]
3. **Langkah 3 (Verifikasi Internal):** [Pengecekan kualitas mandiri]
4. **Langkah 4 (Penyusunan Artefak):** [Pembuatan keluaran terstruktur]
5. **Langkah 5 (Pelaporan):** [Pencatatan bukti dan penyampaian hasil]

---

## 7. Analisis Risiko & Pertimbangan Keamanan (Security Review)

- **Potensi Tool Abuse:** [Bagaimana mencegah agen menjalankan perintah destruktif?]
- **Penanganan Prompt Injection:** [Bagaimana skill memproses teks masukan yang mencurigakan?]
- **Proteksi Kredensial & PII:** [Bagaimana menjamin tidak ada token/rahasia yang bocor ke output?]

---

## 8. Rencana Pengujian Awal (Validation Plan)

- **Skenario Happy Path:** [Input baku dan hasil yang diharapkan]
- **Skenario Edge Case 1:** [Input tidak lengkap / kosong]
- **Skenario Edge Case 2:** [Kegagalan tool teknis]
- **Metode Evaluasi Output:** [Uji otomatis deterministik \| Rubrik penilaian kualitatif]

---

## 9. Lembar Pengesahan Rencana (Sign-Off Gate)

- [ ] **Tinjauan Kepatuhan Tata Kelola (Reviewer Agent):** Lolos / Mematuhi Level 1 Governance.
- [ ] **Persetujuan Perancangan (AI Engineering Lead):** Draf disetujui untuk diimplementasikan ke `SKILL.md`.
- [ ] **Otorisasi Pemilik Proyek (Yogi Ario):** Tanggal: `[DD/MM/YYYY]` | Status: `[APPROVED / REJECTED]`
