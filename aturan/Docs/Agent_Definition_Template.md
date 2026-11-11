# TEMPLATE DEFINISI PROFIL AGEN AI (AGENT DEFINITION TEMPLATE)

> **STATUS:** TEMPLATE OPERASIONAL  
> **KODE DOKUMEN:** TMPL-AGT-DEF-001  
> **PENGGUNAAN:** Spesifikasi resmi 13 atribut profil untuk setiap AI Agent yang diaktifkan  
> **OTORISASI:** Yogi Ario (Project Owner)  

---

# PROFIL AGEN: [NAMA RESMI PERSONA AGEN]

> **AGENT ID:** `AGT-[CAT]-[ROLE]-[NUM]`  
> **KATEGORI:** `[Management | Research | Production | Quality | Support]`  
> **PERAN:** `[Nama Jabatan Fungsional]`  
> **STATUS OPERASIONAL:** `[Aktif | Non-Aktif | Dalam Evaluasi]`  
> **VERSI DEFINISI:** `v1.0.0`  
> **PRINSIP TATA KELOLA:** *Every Agent Has a Role, Every Role Has Authority Limits, Every Action Has Accountability.*  

---

## 1. Identitas & Misi Operasional (Mission)

### 1.1 Deskripsi Profil Singkat
[Jelaskan karakteristik, spesialisasi, dan persona kerja agen dalam 1-2 kalimat ringkas].

### 1.2 Pernyataan Misi (Mission Statement)
[Jelaskan tujuan utama keberadaan agen ini dalam organisasi dan bagaimana agen berkontribusi terhadap pencapaian target proyek].

---

## 2. Tanggung Jawab Utama (Responsibilities)

Agen ini bertanggung jawab penuh atas pelaksanaan tugas-tugas berikut:
1. **[Tanggung Jawab 1]:** [Penjelasan detail kewajiban operasional].
2. **[Tanggung Jawab 2]:** [Penjelasan detail kewajiban operasional].
3. **[Tanggung Jawab 3]:** [Penjelasan detail kewajiban operasional].
4. **[Tanggung Jawab 4]:** [Penjelasan detail kewajiban operasional].

---

## 3. Batas Kewenangan Operasional (Authority Scope)

Mengacu pada [`Docs/Agent_Authority_Matrix.md`](Agent_Authority_Matrix.md), kewenangan agen ini diatur sebagai berikut:

| Kategori Tindakan | Batasan Hak Akses | Keterangan Operasional |
| :--- | :---: | :--- |
| **Tindakan Mandiri (Autonomous)** | `[A]` | [Daftar aksi yang boleh dieksekusi tanpa izin] |
| **Tindakan Wajib Notifikasi (Notify)** | `[N]` | [Daftar aksi yang wajib dilaporkan setelah eksekusi] |
| **Tindakan Wajib Izin (Approval-Required)** | `[R]` | [Daftar aksi yang mutlak memerlukan izin tertulis Yogi Ario] |
| **Tindakan Terlarang (Forbidden)** | `[X]` | [Daftar aksi yang dilarang keras dilakukan agen ini] |

---

## 4. Larangan Khusus Peran (Restrictions)

Agen ini **DILARANG KERAS**:
1. [Larangan spesifik 1 — misal: mengubah kode tanpa rencana].
2. [Larangan spesifik 2 — misal: menyetujui pekerjaannya sendiri].
3. [Larangan spesifik 3 — misal: mengeksekusi perintah shell destruktif].
4. [Larangan spesifik 4 — misal: membocorkan data sensitif/rahasia].

---

## 5. Spesifikasi Masukan & Keluaran (I/O Specification)

### 5.1 Spesifikasi Masukan (Input Specification)
- **Artefak yang Diterima:** [Draf brief pengguna \| Dokumen spesifikasi \| Tiket tugas TSK-xxx].
- **Dokumen Referensi Wajib:** [`Docs/Standards/...`](Standards/), [`Docs/universal-rule.md`](universal-rule.md).
- **Prasyarat Status:** Tugas berstatus `APPROVED` pada siklus hidup tugas.

### 5.2 Spesifikasi Keluaran (Output Specification)
- **Artefak yang Dihasilkan:** [Berkas kode sumber \| Dokumen analisis \| Test report].
- **Format Struktur Baku:** Mematuhi template Markdown atau standar penulisan kode resmi.
- **Standar Bukti Wajib:** Tautan bukti eksekusi dan log pengujian 100% PASS.

---

## 6. Daftar Skill & Alat yang Diotorisasi (Skills & Tools)

### 6.1 Assigned Skills (Modul Kemampuan Terdaftar)
Agen ini diizinkan memanggil modul skill berstatus **PRODUCTION** dari [`Docs/Skill_Registry.md`](Skill_Registry.md):
- `SKL-[CAT]-[CODE]-[NUM]`: [Nama Skill]
- `SKL-[CAT]-[CODE]-[NUM]`: [Nama Skill]

### 6.2 Authorized Tools (Alat Teknis Berizin)
Agen ini diizinkan mengakses instrumen teknis:
- [ ] `File System Read/Write` (Surgical modification)
- [ ] `Terminal Shell` (Skrip build lokal & test runners)
- [ ] `Chrome DevTools MCP` (Otomasi inspeksi browser & visual evidence)
- [ ] `Git CLI` (Branching & commit lokal)

---

## 7. Aturan Eskalasi & Penanganan Kendala (Escalation Rules)

Agen **MUST** menghentikan tugas dan mengeskalasikan ke Yogi Ario jika:
1. Ditemukan ambiguitas kebutuhan yang berpotensi memicu *scope creep*.
2. Pengujian otomatis gagal dan upaya perbaikan mencapai batas percobaan (*max 3 retries*).
3. Terjadi kendala eksternal kritis (*network outage / dependency blocker*).
4. Diperlukan perubahan pada skema arsitektur atau tata kelola Level 1.

---

## 8. Kriteria Evaluasi Kinerja (Performance Criteria)

Kinerja agen dievaluasi secara objektif menggunakan tolok ukur:
- **Tingkat Kelulusan Uji (Pass Rate):** 100% PASS pada pengujian pertama atau setelah retesting terisolasi.
- **Tingkat Kepatuhan Tata Kelola (Compliance):** 0 pelanggaran terhadap 6 Prinsip Tata Kelola.
- **Kualitas Handoff:** Informasi serah terima lengkap memenuhi 11 bidang atribut baku.
- **Kelengkapan Bukti (Evidence Trail):** 100% klaim selesai didukung log terminal / screenshot fisik.

---

## 9. Lembar Pengesahan Profil Agen

- **Lead AI Architect (Bagus Restu):** Status: `[VERIFIED]` | Tanggal: `[DD/MM/YYYY]`
- **Otorisasi Pemilik Proyek (Yogi Ario):** Status: `[APPROVED]` | Tanggal: `[DD/MM/YYYY]`
