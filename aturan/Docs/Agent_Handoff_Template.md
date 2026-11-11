# FORMULIR SERAH TERIMA PEKERJAAN ANTARAGEN (AGENT HANDOFF FORM)

> **STATUS:** TEMPLATE OPERASIONAL  
> **KODE DOKUMEN:** TMPL-HND-001  
> **PENGGUNAAN:** Dokumentasi resmi serah terima tugas antaragen (*11-Field Handoff Protocol*) untuk mencegah kehilangan konteks (*Zero Context Loss*)  
> **OTORISASI:** Yogi Ario (Project Owner)  

---

## 1. Metadata Protokol Serah Terima (Handoff Metadata)

| Parameter | Keterangan Data Serah Terima |
| :--- | :--- |
| **ID Dokumen Handoff** | `HND-[MODULE]-[YYYYMMDD]-[NUM]` |
| **Task ID Terkait** | `TSK-[MODULE]-[NUM]` |
| **Agen Pengirim (Sender Agent)** | `AGT-[CAT]-[ROLE]-[NUM]` (Nama Persona) |
| **Agen Penerima (Receiver Agent)**| `AGT-[CAT]-[ROLE]-[NUM]` (Nama Persona) |
| **Waktu Pelaksanaan Handoff** | [DD/MM/YYYY HH:mm WIB] |
| **Status Tugas Saat Handoff** | `[IN_PROGRESS / UNDER_REVIEW / COMPLETED]` |

---

## 2. Rincian Dokumen Protokol 11-Bidang (The 11 Mandatory Fields)

### 2.1 1. Task ID
`TSK-[MODULE]-[NUM]`: [Judul Singkat Tugas Operasional].

### 2.2 2. Sender Agent (Agen Pengirim)
- **ID & Nama:** `[AGT-xxx]` — [Nama Agen Pengirim]
- **Peran Fungsional:** [Misal: AI Lead Developer / Researcher]

### 2.3 3. Receiver Agent (Agen Penerima)
- **ID & Nama:** `[AGT-xxx]` — [Nama Agen Penerima]
- **Peran Fungsional:** [Misal: AI QA Engineer / Technical Reviewer]

### 2.4 4. Work Completed (Pekerjaan yang Telah Selesai)
Rincian pekerjaan yang telah berhasil dikerjakan oleh agen pengirim:
1. [Pekerjaan/Modul 1 yang tuntas].
2. [Pekerjaan/Modul 2 yang tuntas].
3. [Daftar berkas yang dibuat/diubah beserta jalur fisiknya].

### 2.5 5. Work Remaining (Pekerjaan yang Masih Tersisa)
Rincian tugas lanjutan yang wajib dituntaskan oleh penerima:
1. [Langkah lanjutan 1 — misal: Eksekusi pengujian otomatis & otomasi browser].
2. [Langkah lanjutan 2 — misal: Audit kesesuaian coding standard & review dead code].

### 2.6 6. Inputs & References (Masukan & Dokumen Acuan)
Tautan dokumen dan sumber daya yang wajib dirujuk agen penerima:
- Spesifikasi Kebutuhan: [`Docs/SRS_Template.md`](SRS_Template.md)
- Standar Teknis: [`Docs/Standards/...`](Standards/)
- Berkas Sumber Terkait: `[path/to/source_code.ext]`

### 2.7 7. Decisions Made (Keputusan Teknis yang Telah Diambil)
Keputusan arsitektur atau teknis penting yang disepakati selama pengerjaan:
- [Keputusan 1 beserta alasannya — mengacu pada Decision_Log.md].
- [Pola atau paradigma yang sengaja dipilih untuk tugas ini].

### 2.8 8. Known Issues & Flaws (Kendala & Kelemahan yang Diketahui)
Anomali, peringatan (*warnings*), atau keterbatasan yang perlu diwaspadai:
- [Catatan kendala 1 — misal: performa query pada dataset > 10.000 baris].
- [Peringatan dependency lokal].

### 2.9 9. Identified Risks (Potensi Risiko yang Teridentifikasi)
Risiko teknis atau dampak terhadap modul lain:
- [Potensi regresi pada modul terkait bila dilakukan perubahan lanjut].
- [Risiko kompatibilitas runtime].

### 2.10 10. Verifiable Evidence (Bukti Nyata yang Diverifikasi)
Bukti nyata bahwa pekerjaan pengirim telah memenuhi DoR/DoD lokal:
- Log terminal pengujian unit test lokal: `[Lampirkan cuplikan log]`.
- Tautan tangkapan layar validasi: `Docs/Evidence/SS/valid/[screenshot.png]`.
- Commit Git aktif: Commit Hash `[hash]` pada branch `feature/[nomor]_[nama]`.

### 2.11 11. Expected Next Action (Tindakan Nyata yang Diharapkan)
Instruksi spesifik yang wajib dilakukan agen penerima seketika:
- [Contoh: Lakukan audit independen terhadap 3 file target dan jalankan E2E test suite].

---

## 3. Lembar Verifikasi Kelengkapan Handoff (Receiver Verification)

Agen penerima **MUST** memeriksa kelengkapan data sebelum menerima penugasan:
- [ ] Seluruh 11 bidang informasi terisi lengkap dan tidak ambigu.
- [ ] Seluruh tautan berkas dan referensi dapat diakses secara fisik (*accessible*).
- [ ] Bukti pengujian awal (*evidence*) valid dan dapat diverifikasi ulang.
- [ ] Tidak ada pelanggaran terhadap *Universal Governance* atau batas peran.

### Status Penerimaan Handoff:
- [ ] **ACCEPTED:** Handoff lengkap dan valid. Pengerjaan dilanjutkan oleh agen penerima.
- [ ] **REJECTED (RETURNED):** Informasi tidak lengkap atau bukti verifikasi palsu. Dikembalikan ke agen pengirim untuk dilengkapi.
- **Catatan Evaluator Penerima:** `[Komentar atau alasan penolakan jika ada]`

---

## 4. Tanda Tangan Serah Terima

- **Diserahkan oleh (Sender):** `[Nama Agen]` | Tanggal: `[DD/MM/YYYY HH:mm]`
- **Diterima oleh (Receiver):** `[Nama Agen]` | Tanggal: `[DD/MM/YYYY HH:mm]`
