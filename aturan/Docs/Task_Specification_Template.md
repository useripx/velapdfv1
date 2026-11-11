# TEMPLATE SPESIFIKASI TUGAS OPERASIONAL (TASK SPECIFICATION TEMPLATE)

> **STATUS:** TEMPLATE OPERASIONAL  
> **KODE DOKUMEN:** TMPL-TSK-SPEC-001  
> **PENGGUNAAN:** Pendefinisian tugas terstruktur, penetapan kriteria penerimaan, dan pelacakan siklus hidup tugas multi-agen  
> **OTORISASI:** Yogi Ario (Project Owner)  

---

## 1. Metadata Tugas (Task Metadata)

| Parameter | Keterangan / Nilai |
| :--- | :--- |
| **Task ID** | `TSK-[MODULE]-[NUM]` *(Contoh: `TSK-AUTH-001`)* |
| **Judul Tugas (Task Title)** | [Tindakan dan target ringkas, misal: Implementasi Otentikasi JWT] |
| **Kategori Modul** | [AUTH \| CORE \| UI \| DB \| API \| QA \| DOCS] |
| **Tingkat Prioritas** | [P0 - Critical \| P1 - High \| P2 - Medium \| P3 - Low] |
| **Agen Penanggung Jawab** | `AGT-[CAT]-[ROLE]-[NUM]` (Nama Persona) |
| **Agen Penguji / Reviewer**| `AGT-QLT-[ROLE]-[NUM]` (Nama Persona) |
| **Status Saat Ini** | [CREATED \| ANALYZING \| PLANNED \| WAITING_APPROVAL \| APPROVED \| IN_PROGRESS \| UNDER_REVIEW \| BLOCKED \| COMPLETED \| CANCELLED] |
| **Batas Waktu (Deadline)** | [DD/MM/YYYY HH:mm WIB \| Fleksibel] |

---

## 2. Deskripsi & Tujuan Tugas (Objective & Description)

- **Tujuan Operasional (Objective):**  
  [Jelaskan hasil nyata yang ingin dicapai melalui tugas ini].
- **Deskripsi Rinci (Description):**  
  [Uraikan latar belakang masalah, alur kerja teknis yang diinginkan, dan konteks bisnis].
- **Kebutuhan Rujukan (REQ ID):** Menuntaskan kebutuhan `REQ-F-[MOD]-[NUM]` pada [`Docs/SRS_Template.md`](SRS_Template.md).

---

## 3. Spesifikasi Masukan & Keluaran (I/O Specification)

### 3.1 Masukan yang Disediakan (Inputs)
- **Dokumen Konteks:** [`Docs/Standards/...`](Standards/), [`Docs/Implementation_plan.md`](Implementation_plan.md).
- **Berkas / Sumber Daya Awal:** `[path/to/source/file.ext]`.
- **Parameter Teknis:** `[Nilai konfigurasi atau skema data yang disuplai]`.

### 3.2 Hasil yang Diharapkan (Expected Outputs)
- **Berkas Baru / Diubah:** `[path/to/output_file.ext]`.
- **Test Suite Terkait:** `[path/to/test_file.ext]` (Wajib membuktikan 100% PASS).
- **Dokumentasi Terbarui:** Pembaruan pada `Task_Status.md` dan pembuatan catatan flat changelog.

---

## 4. Dependensi & Batasan Operasional (Dependencies & Constraints)

- **Dependensi Pendahulu (Prerequisite Tasks):**  
  - [ ] `TSK-[MOD]-[PREV]` berstatus **COMPLETED**.
- **Batasan Arsitektur & Teknologi:**  
  [Batasan framework, library yang dilarang, atau aturan clean main policy].
- **Kebutuhan Persetujuan Khusus:**  
  [Apakah memerlukan otorisasi Yogi Ario sebelum mengubah skema DB atau menambah paket eksternal].

---

## 5. Kriteria Penerimaan Terukur (Acceptance Criteria)

Tugas ini **HANYA** boleh diubah statusnya menjadi **COMPLETED** jika seluruh kriteria biner berikut terbukti terpenuhi:
- [ ] **Kriteria 1:** [Deskripsi kondisi terukur yang dapat diuji].
- [ ] **Kriteria 2:** [Deskripsi kondisi terukur yang dapat diuji].
- [ ] **Kriteria 3:** Seluruh kasus pengujian otomatis berstatus **100% PASS (0 GAGAL / 0 ERROR)**.
- [ ] **Kriteria 4:** Bukti visual screenshot tersimpan di `Docs/Evidence/SS/valid/` (jika melibatkan UI/web).
- [ ] **Kriteria 5:** Tidak ada secret/kredensial yang terekspos ke dalam kode atau log terminal.

---

## 6. Riwayat Siklus Hidup Tugas (Task Lifecycle Audit Trail)

| Status | Tanggal / Jam | Pelaksana | Ringkasan Tindakan & Catatan Bukti |
| :--- | :---: | :--- | :--- |
| **CREATED** | DD/MM/YYYY HH:mm | AI Manager | Tugas didefinisikan berdasarkan brief rilis. |
| **PLANNED** | DD/MM/YYYY HH:mm | Lead Developer | Rencana implementasi teknis telah disusun. |
| **APPROVED** | DD/MM/YYYY HH:mm | Yogi Ario | Rencana disetujui resmi (*Approval Gate*). |
| **IN_PROGRESS** | DD/MM/YYYY HH:mm | Lead Developer | Pengerjaan kode dan pengujian lokal dimulai. |
| **UNDER_REVIEW** | DD/MM/YYYY HH:mm | QA Engineer | Evaluasi pengujian independen & audit kode. |
| **COMPLETED** | DD/MM/YYYY HH:mm | Lead Developer | 100% PASS terbukti; changelog dan bukti tercatat. |

---

## 7. Tautan Bukti Penyelesaian (Evidence References)

- **Tautan Commit Git:** Commit Hash `[hash]` pada branch `feature/[nomor]_[nama]`.
- **Tautan Log Terminal Pengujian:** `Docs/Test_Report.md` (Bagian TC-xxx).
- **Tautan Bukti Visual:** `Docs/Evidence/SS/valid/[screenshot.png]`.
- **Tautan Berkas Changelog:** `changelog/[nomor]changelog[jam][tanggal].md`.

---

## 8. Pengesahan Penyelesaian Tugas (Sign-Off Gate)

- **Agen Pelaksana:** `[Nama Agen]` | Status: Lolos Uji Mandiri
- **Agen Reviewer / QA:** `[Nama Agen]` | Status: Terverifikasi 100% PASS
- **Otorisasi Pemilik Proyek (Yogi Ario):** `[APPROVED & ACCEPTED]` | Tanggal: `[DD/MM/YYYY]`
