# Software Requirements Specification (SRS) — [Isi Manual: Nama Proyek]

> **STATUS DOKUMEN:** DRAF SPESIFIKASI KEBUTUHAN (ACTIVE SRS SPECIFICATION)  
> **LANGKAH WORKFLOW:** Step 1 of 12 Engineering Workflow  
> **PEMILIK PROYEK:** Yogi Ario  
> **PENYUSUN:** [Isi Manual: Nama AI Agent / Pengembang]  
> **TANGGAL:** [Isi Manual: DD/MM/YYYY]  
> **VERSI SRS:** v1.0.0  
> **ACUAN STANDAR:** [Docs/Standards/Requirements_Engineering.md](Standards/Requirements_Engineering.md)

---

## 1. Pendahuluan

### 1.1 Tujuan Sistem
[Isi Manual: Deskripsikan tujuan utama sistem perangkat lunak yang dibangun, cakupan operasional, dan manfaat yang ingin dicapai.]

### 1.2 Ruang Lingkup Proyek (Scope of Work)
- **In-Scope (Termasuk):**
  - [Isi Manual: Fitur dan kapabilitas yang wajib dibangun]
- **Out-of-Scope (Tidak Termasuk):**
  - [Isi Manual: Fitur atau integrasi yang secara eksplisit ditunda ke fase berikutnya]

---

## 2. Kebutuhan Fungsional (Functional Requirements)

Setiap kebutuhan fungsional menggunakan konvensi ID: `REQ-F-[MODUL]-[NOMOR]` dan memuat kriteria penerimaan terukur (*Acceptance Criteria*).

### 2.1 Modul: [Isi Manual: Nama Modul 1, misal: Autentikasi]

#### [REQ-F-AUTH-001] — [Isi Manual: Pendaftaran Akun Pengguna Baru]
- **Prioritas:** `[Must Have / Should Have / Could Have]`
- **Deskripsi Kebutuhan:**  
  Sebagai *pengguna baru*, saya ingin *mendaftarkan akun menggunakan email dan password*, sehingga *saya memiliki akses pribadi ke dalam aplikasi*.
- **Kriteria Penerimaan (Acceptance Criteria):**
  - **Skenario 1 (Sukses):**
    * *Given:* Pengguna berada di halaman registrasi dengan form kosong.
    * *When:* Pengguna mengisi email valid yang belum terdaftar dan password minimal 8 karakter, lalu menekan tombol "Daftar".
    * *Then:* Sistem membuat akun baru di database dengan password ter-hash, mengembalikan status HTTP 201, dan mengarahkan pengguna ke dasbor.
  - **Skenario 2 (Gagal - Email Duplikat):**
    * *Given:* Pengguna memasukkan email yang sudah terdaftar sebelumnya.
    * *When:* Pengguna menekan tombol "Daftar".
    * *Then:* Sistem menolak pendaftaran, mengembalikan status HTTP 422, dan menampilkan pesan error: *"Email sudah digunakan"*.

---

## 3. Kebutuhan Non-Fungsional (Non-Functional Requirements)

Menggunakan konvensi ID: `REQ-NF-[KATEGORI]-[NOMOR]`.

| ID Kebutuhan | Kategori | Deskripsi Kebutuhan & Target Metrik | Kriteria Keberhasilan Terukur |
| :--- | :--- | :--- | :--- |
| **`REQ-NF-PERF-001`** | Performa | Waktu respons API | Latensi respons endpoint 95% request (p95) < 300 ms pada beban normal. |
| **`REQ-NF-SECU-001`** | Keamanan | Enkripsi kredensial pengguna | Seluruh password disimpan menggunakan algoritma Argon2id atau Bcrypt (cost >= 10). |
| **`REQ-NF-AVAI-001`** | Ketersediaan | Waktu aktif sistem (Uptime) | Sistem memiliki ketersediaan minimal 99.5% per bulan. |

---

## 4. Batasan Sistem (Constraints)

Menggunakan konvensi ID: `REQ-C-[NOMOR]`.

- **[REQ-C-001]:** [Isi Manual: misal Aplikasi harus dapat berjalan secara offline murni tanpa server eksternal].
- **[REQ-C-002]:** [Isi Manual: misal Aplikasi harus kompatibel dengan browser Google Chrome >= 100].

---

## 5. Matriks Ketertelusuran Kebutuhan (Requirements Traceability Matrix)

| Requirement ID | Modul Terkait | Berkas Kode Implementasi | Test Case ID | Status Verifikasi |
| :--- | :---: | :--- | :--- | :---: |
| `REQ-F-AUTH-001` | Auth | `[Isi Manual: src/controllers/AuthController.ts]` | `[Isi Manual: TC-AUTH-001]` | `[PENDING / PASS]` |
| `REQ-NF-PERF-001`| Core | `[Isi Manual: src/middleware/cache.ts]` | `[Isi Manual: TC-PERF-001]` | `[PENDING / PASS]` |

---

> [!tip] PETUNJUK KHUSUS
> Gunakan template ini untuk merumuskan seluruh kebutuhan proyek bersama Yogi Ario sebelum menyusun Implementation Plan. Hapus catatan tip ini setelah dokumen disesuaikan.
