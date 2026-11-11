# PROTOKOL KOMUNIKASI INTERNAL ANTARAGEN (AGENT COMMUNICATION PROTOCOL)

> **DOCUMENT ID:** STD-COMM-001  
> **VERSION:** v2.0.0  
> **STATUS:** AKTIF (LEVEL 2 OPERATIONAL STANDARD)  
> **PENGGUNAAN:** Format baku pesan terstruktur dalam koordinasi, delegasi, dan pelaporan multi-agen AI  
> **OTORISASI TERTINGGI:** Designated Approver (Rujuk [config/organization.md](../config/organization.md), saat ini: **Yogi Ario**)  
> **PRINSIP UTAMA:**  
> - *Every Action Has Accountability.*  
> - *No Evidence, No Completion Claim.*  
> - *Context Preservation & Token Efficiency.*

---

## 1. Pendahuluan

Dokumen ini menetapkan standar format pesan internal antaragen AI. Komunikasi antaragen **MUST** bersifat ringkas, lugas, profesional, bebas dari basa-basi yang memboroskan token (*token-efficient*), dan berorientasi penuh pada hasil serta pembuktian (*outcome & evidence-driven*).

Agen dilarang keras menyampaikan pesan yang hanya menyatakan tugas selesai tanpa membeberkan ringkasan hasil kerja dan tautan bukti nyata (*No Evidence, No Completion*).

---

## 2. Struktur Header Pesan Standar (Standard Message Header)

Setiap pertukaran pesan formal antaragen atau pelaporan ke sistem **MUST** diawali dengan header terstruktur berikut:

```markdown
---
msg_id: "MSG-[TYPE]-[YYYYMMDD]-[NUM]"
sender: "AGT-[CAT]-[ROLE]-[NUM]"
receiver: "AGT-[CAT]-[ROLE]-[NUM] / Designated Approver"
task_id: "TSK-[MODULE]-[NUM] / UREV-[NUM]"
msg_type: "[UPDATE | HANDOFF | CLARIFY | APPROVAL | BLOCKER | FEEDBACK | COMPLETE]"
task_state: "[NOT_STARTED | IN_ANALYSIS | PLANNING | PENDING_APPROVAL | IN_PROGRESS | IN_TESTING | IN_REVIEW | VERIFIED | COMPLETED | BLOCKED]"
priority: "[P0-Critical | P1-High | P2-Medium | P3-Low]"
timestamp: "YYYY-MM-DD HH:mm WIB"
---
```

---

## 3. Tujuh Format Pesan Baku (The 7 Message Types)

### 3.1 `MSG-UPDATE` (Laporan Progres Rutin)
Digunakan untuk melaporkan capaian subtask atau pemutakhiran berkala status pengerjaan:
```markdown
# [MSG-UPDATE] Update Progres: [Judul Tugas]
- **Status Tugas:** [IN_PROGRESS | IN_ANALYSIS | IN_TESTING | IN_REVIEW]
- **Capaian Selesai:** [Rincian 1-3 tindakan nyata yang telah diselesaikan].
- **Langkah Sedang Berjalan:** [Tindakan yang sedang diproses saat ini].
- **Estimasi Kendala / Risiko:** [Zero kendala / Kendala minor terkendali].
```

### 3.2 `MSG-HANDOFF` (Pemberitahuan Serah Terima Pekerjaan)
Digunakan saat menyerahkan tanggung jawab tugas ke agen lain (merujuk pada [Agent_Handoff_Template.md](Agent_Handoff_Template.md)):
```markdown
# [MSG-HANDOFF] Serah Terima Tugas: [Task ID]
- **Agen Pengirim:** [ID & Peran]
- **Agen Penerima:** [ID & Peran]
- **Ringkasan Pekerjaan Selesai:** [Rincian kode/dokumen yang telah rampung].
- **Tautan Berkas Handoff Lengkap:** `Docs/Agent_Handoff_Template.md` (ID: HND-xxx).
- **Tindakan yang Diharapkan:** [Audit review independen / Eksekusi QA test suite].
```

### 3.3 `MSG-CLARIFY` (Permohonan Klarifikasi Teknis)
Digunakan saat menghadapi instruksi ambigu, spesifikasi kontradiktif, atau parameter yang hilang:
```markdown
# [MSG-CLARIFY] Permohonan Klarifikasi: [Topik Masalah]
- **Konteks Masalah:** [Bagian instruksi yang ditemukan ambigu / bertentangan].
- **Analisis Dampak:** [Mengapa ambiguitas ini menghambat kelanjutan pengerjaan].
- **Opsi Solusi / Interpretasi:**
  1. *Opsi A:* [Deskripsi beserta kelebihan & kekurangan].
  2. *Opsi B:* [Deskripsi beserta kelebihan & kekurangan].
- **Rekomendasi Teknis:** [Pilihan yang direkomendasikan agen dan alasannya].
- **Pertanyaan Spesifik:** [Pertanyaan terfokus untuk dijawab].
```

### 3.4 `MSG-APPROVAL` (Permohonan Persetujuan Rencana / Perubahan)
Digunakan untuk menembus *Approval Gate (Prinsip 3)* sebelum mengeksekusi kode atau perubahan arsitektur:
```markdown
# [MSG-APPROVAL] Pengajuan Persetujuan Rencana: [Judul Rencana]
- **Task ID:** [TSK-xxx / UREV-xxx]
- **Tautan Dokumen Rencana:** [`Docs/Implementation_plan.md`](Implementation_plan.md)
- **Target File Whitelist:** [Daftar lengkap berkas yang akan dibuat/diubah/dihapus].
- **Target Branch & Lingkungan:** [feature/xxx, local/develop].
- **Analisis Trade-Off & Arsitektur:** [Rangkuman pilihan solusi teknis].
- **Rencana Verifikasi & Acceptance Criteria:** [Test suite, linter, dan bukti yang akan dihasilkan].
- **Pernyataan Hold:** Sesuai prinsip *No Approval, No Code*, eksekusi kode DITAHAN sampai memperoleh persetujuan eksplisit Designated Approver.
```

### 3.5 `MSG-BLOCKER` (Laporan Kendala Fatal)
Digunakan seketika saat terjadi kegagalan fatal yang menghentikan progres tugas (mengacu pada [Standards/Error_Handling_Recovery.md](Standards/Error_Handling_Recovery.md)):
```markdown
# [MSG-BLOCKER] Laporan Kendala Fatal: [Judul Blocker]
- **Kategori Kegagalan:** [Agent | Model | Provider | Tool | Network | Context | Validation | Permission | Workflow]
- **Deskripsi Insiden:** [Uraian apa yang gagal dan pesan error spesifik].
- **Upaya Pemulihan yang Telah Dilakukan:** [Rincian percobaan ulang (bounded retry) / fallback].
- **Tautan Log Insiden:** [`Docs/Agent_Incident_Log.md`](Agent_Incident_Log.md) (ID: INC-xxx).
- **Keputusan / Otorisasi yang Dibutuhkan:** [Langkah otorisasi yang dimohonkan kepada Designated Approver].
- **Status Tugas Saat Ini:** **BLOCKED**.
```

### 3.6 `MSG-FEEDBACK` (Umpan Balik Audit & Review Kualitas)
Digunakan oleh Reviewer Agent atau QA Agent untuk menyampaikan temuan audit atau bug:
```markdown
# [MSG-FEEDBACK] Umpan Balik Review / Temuan Defect: [Task ID]
- **Tingkat Keparahan Temuan:** [Critical | High | Medium | Low].
- **Ringkasan Temuan:** [Uraian baris kode, fungsi, atau dokumen yang bermasalah].
- **Pelanggaran Standar:** [Rujukan standar yang dilanggar, misal: Coding_Standards.md].
- **Tautan Tiket Defect:** [`Docs/Defect_Log.md`](Defect_Log.md) (ID: DEF-xxx).
- **Tindakan Koreksi Wajib:** [Langkah perbaikan spesifik yang wajib dilakukan Developer].
```

### 3.7 `MSG-COMPLETE` (Laporan Tuntasnya Tugas & Verifikasi)
Digunakan saat tugas telah terverifikasi memenuhi seluruh kriteria penerimaan dan pengujian wajib yang relevan:
```markdown
# [MSG-COMPLETE] Laporan Penyelesaian Tugas: [Task ID]
- **Judul Tugas:** [Judul Resmi Tugas].
- **Status Akhir:** **COMPLETED (VERIFIED)**.
- **Ringkasan Hasil Pekerjaan:** [Rangkuman fitur atau artefak yang berhasil dibangun].
- **Bukti Verifikasi (Evidence-Backed):**
  - Status Pengujian: [Seluruh pengujian wajib dan relevan PASS; pengecualian tercatat & dijustifikasi].
  - Tautan Laporan Pengujian: [`Docs/Test_Report.md`](Test_Report.md) / CLI Test Log.
  - Tautan Bukti Faktual: `Docs/Evidence/` (CLI Log / Hash / Screenshot jika relevan).
  - Tautan Commit Git: Commit `[hash]` pada branch `[branch]`.
  - Tautan Changelog: `changelog/[nomor]changelog[jam][tanggal].md`.
```

---

## 4. Ketentuan Keamanan & Redaksi Pesan

1. **Zero Secret Leakage:** Agen **DILARANG KERAS** menyisipkan API key, kata sandi, token otentikasi, atau data pribadi pengguna (PII) ke dalam isi pesan apa pun.
2. **Penyensoran Kredensial:** Gunakan tag `[REDACTED_SECRET]` atau `[REDACTED_PII]` bila pesan memuat kutipan konfigurasi atau log teknis.

---

## 5. Rujukan Terkait
- Orkestrasi & Handoff: [Docs/Standards/Agent_Orchestration_Handoff.md](Standards/Agent_Orchestration_Handoff.md)
- Template Serah Terima: [Docs/Agent_Handoff_Template.md](Agent_Handoff_Template.md)
- Matriks Hak Akses: [Docs/Agent_Authority_Matrix.md](Agent_Authority_Matrix.md)
- Standar Penanganan Cacat: [Docs/Standards/Error_Handling_Recovery.md](Standards/Error_Handling_Recovery.md)
- Konfigurasi Organisasi: [config/organization.md](../config/organization.md)
