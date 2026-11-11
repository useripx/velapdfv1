# STANDAR REKAYASA: VALIDASI & PENGUJIAN SKILL (SKILL VALIDATION & TESTING)

> **STATUS:** AKTIF (LEVEL 2 OPERATIONAL STANDARD)  
> **NOMOR DOKUMEN:** STD-ENG-010-SKL  
> **VERSION:** v2.0.0  
> **KATEGORI:** STANDAR VALIDASI & PENGUJIAN KEMAMPUAN AI  
> **PENANGGUNG JAWAB:** Lead Developer Role (Rujuk [config/organization.md](../../config/organization.md))  
> **OTORISASI:** Designated Approver (Rujuk [config/organization.md](../../config/organization.md), saat ini: **Yogi Ario**)  
> **PRINSIP UTAMA:**  
> - *Quality Must Be Demonstrated, Not Assumed.*  
> - *Risk-Based Applicability with Documented Rationale.*  
> - *No Evidence, No Completion.*  
> **KATA KUNCI NORMATIF:** RFC 2119 (MUST, MUST NOT, SHOULD, RECOMMENDED, MAY).

---

## 1. Pendahuluan

Dokumen ini menetapkan kerangka kerja pengujian (*benchmarking*) dan validasi objektif yang wajib dipenuhi oleh setiap modul skill sebelum dapat menyandang status **VALIDATED**. 

Pengujian skill bukan sekadar memeriksa apakah model mampu memberikan jawaban masuk akal (*plausible text*), melainkan pembuktian sistematis bahwa prosedur kerja yang dirumuskan mampu berjalan andal, menghasilkan artefak yang memenuhi spesifikasi, tahan terhadap masukan abnormal (*edge cases*), dan aman dari penyalahgunaan alat atau kebocoran data.

---

## 2. Enam Dimensi Validasi Skill & Penerapan Berbasis Risiko (The 6 Validation Dimensions)

Setiap pengujian skill **MUST** mengevaluasi 6 dimensi independen berikut, dengan ketentuan bahwa setiap dimensi yang dinyatakan tidak dapat diterapkan (*Not Applicable / N/A*) **wajib menyertakan justifikasi tertulis yang disetujui**:

```mermaid
flowchart LR
    subgraph Six Validation Dimensions
        D1["1. Rule Compliance\n(Mandatory)"]
        D2["2. Functional Testing\n(Mandatory)"]
        D3["3. Edge Case Testing\n(Mandatory)"]
        D4["4. Output Quality\n(Mandatory)"]
        D5["5. Security & Safety\n(Applicable/NA)"]
        D6["6. Regression Testing\n(Applicable/NA)"]
    end

    D1 & D2 & D3 & D4 & D5 & D6 --> RES{"Evaluasi Terpadu"}
    RES -->|Seluruh Dimensi Relevan PASS| PASS["STATUS: PASS (VALIDATED)"]
    RES -->|Ada Cacat Non-Kritis| NI["STATUS: NEEDS IMPROVEMENT"]
    RES -->|Cacat Fatal / Pelanggaran Aturan| FAIL["STATUS: FAIL / BLOCKED"]
```

### 2.1 Dimensi 1: Rule Compliance (Kepatuhan Aturan) — MANDATORY
- Menguji apakah prosedur skill mematuhi *Universal Governance* dan *6 Prinsip Tata Kelola*.
- Memastikan prosedur tidak mencoba melewati gerbang persetujuan manusia (*Approval Gate*).
- Memastikan tidak ada langkah yang melanggar batasan peran agen pengguna.

### 2.2 Dimensi 2: Functional Testing (Pengujian Fungsional) — MANDATORY
- Menjalankan skenario alur kerja normal (*happy path*) menggunakan data masukan baku.
- Memverifikasi bahwa seluruh langkah pada `Execution Procedure` dapat dieksekusi secara berurutan.
- Memverifikasi bahwa struktur keluaran sesuai 100% dengan `Output Specification`.

### 2.3 Dimensi 3: Edge Case Testing (Pengujian Kasus Batas & Anomali) — MANDATORY
Pengujian wajib mencakup kondisi anomali yang relevan:
1. **Input Kosong / Tidak Lengkap:** Parameter wajib tidak disediakan. Skill harus menolak dengan pesan kesalahan yang jelas.
2. **Input Bertentangan / Ambigu:** Dua instruksi masukan saling menegasikan. Skill harus meminta klarifikasi, bukan berasumsi sepihak.
3. **Kegagalan Alat (Tool Failure):** Tool teknis mengembalikan error/timeout. Skill harus mengaktifkan prosedur *Fallback*.
4. **Data di Luar Ruang Lingkup (Out-of-Scope Input):** Masukan yang melampaui batasan skill harus ditolak secara sopan.
5. **Format Masukan Korup:** Data terdistorsi (misal: JSON rusak). Skill tidak boleh mengalami unhandled exception.

### 2.4 Dimensi 4: Output Quality (Kualitas Artefak Keluaran) — MANDATORY
- Untuk output teknis deterministik: Diverifikasi melalui linter, parser, atau skema data valid.
- Untuk output analitis/kualitatif: Dinilai menggunakan **Rubrik Penilaian Standar (Evaluation Rubric)** skala 1 s.d. 5.

### 2.5 Dimensi 5: Security & Safety Review (Tinjauan Keamanan) — RISK-BASED APPLICABILITY
- Menguji pencegahan eksekusi perintah shell berbahaya dan kebocoran kredensial rahasia (*Zero Secret Leakage*).
- **Ketentuan N/A:** Dapat ditandai `N/A` hanya jika modul skill merupakan prosedur penalaran kognitif murni (*pure text reasoning*) yang sama sekali tidak memiliki akses atau eksekusi terhadap alat eksternal/terminal, dengan justifikasi: *"Pure read-only cognitive procedure with zero tool execution rights"*.

### 2.6 Dimensi 6: Regression Testing (Pengujian Regresi) — RISK-BASED APPLICABILITY
- Memastikan perbaikan bug atau penambahan fitur baru tidak merusak (*breaking*) fungsionalitas yang sebelumnya telah stabil.
- **Ketentuan N/A:** Dapat ditandai `N/A` pada rilis inisiasi awal (`v1.0.0`) dengan justifikasi tertulis: *"Initial baseline release; no prior version exists for regression comparison"*.

---

## 3. Status Hasil Evaluasi Validasi

Hasil evaluasi pengujian skill diklasifikasikan ke dalam 4 status resmi:

| Status Hasil | Kriteria Penentuan | Tindak Lanjut |
| :--- | :--- | :--- |
| **PASS** | Memenuhi 100% dimensi wajib dan seluruh dimensi relevan berstatus PASS (N/A didokumentasikan sah), serta skor rubrik $\ge 4.0/5.0$. | Skill resmi disahkan berstatus **VALIDATED**. Siap diajukan ke gerbang persetujuan. |
| **NEEDS IMPROVEMENT** | Tidak ada cacat kritis, namun terdapat kekurangan minor pada kejelasan format atau dokumentasi (skor rubrik $3.0 - 3.9$). | Skill dikembalikan ke status **DRAFT** untuk perbaikan revisi minor. |
| **FAIL** | Gagal menghasilkan output sesuai spesifikasi, gagal menangani edge case, atau skor rubrik $< 3.0$. | Skill dikembalikan ke status **DRAFT** untuk perombakan prosedur secara mendasar. |
| **BLOCKED** | Melanggar aturan tata kelola, membocorkan rahasia, atau menyebabkan kegagalan tool fatal. | Pengujian dihentikan seketika. Berkas dilaporkan kepada Designated Approver sebagai insiden kepatuhan. |

---

## 4. Format Standar Kasus Uji Skill (`TC-SKL-[CAT]-[NUM]`)

Setiap kasus uji skill wajib didokumentasikan dalam format standar berikut:

```markdown
### TC-SKL-[CAT]-[NUM]: [Judul Skenario Uji]
- **Target Skill ID:** SKL-[CAT]-[CODE]-[NUM] (vMAJOR.MINOR.PATCH)
- **Dimensi Uji:** [Rule Compliance | Functional | Edge Case | Output Quality | Security | Regression]
- **Status Dimensi:** [APPLICABLE | NOT APPLICABLE (N/A - Justifikasi: ...)]
- **Tingkat Risiko:** [Low | Medium | High | Critical]
- **Deskripsi Skenario:** [Penjelasan tujuan skenario uji]
- **Data Masukan (Input):**
  ```[format]
  [Data uji sintetis / parameter]
  ```
- **Perilaku yang Diharapkan (Expected Behavior):** [Deskripsi hasil yang sah]
- **Perilaku Aktual (Actual Behavior):** [Deskripsi hasil pengujian nyata]
- **Kriteria Evaluasi:** [Kondisi objektif penentu PASS]
- **Hasil Pengujian:** [PASS | FAIL | BLOCKED | NEEDS IMPROVEMENT]
- **Tautan Bukti (Evidence):** [Tautan berkas log / screenshot / diff]
- **Catatan & Temuan:** [Observasi agen penguji]
```

---

## 5. Rubrik Penilaian Kualitas Output Subjektif (Evaluation Rubric)

Untuk skill yang menghasilkan keluaran non-deterministik (analisis, sintesis, dokumentasi), penilaian mutu menggunakan rubrik 5 kriteria berbobot:

| Kriteria Mutu | Bobot | Skor 1 (Sangat Buruk) | Skor 3 (Cukup / Rata-rata) | Skor 5 (Sangat Baik / Sempurna) |
| :--- | :---: | :--- | :--- | :--- |
| **Akurasi & Kebenaran** | 25% | Mengandung informasi keliru atau halusinasi fakta. | Informasi mayoritas benar, ada distorsi minor tanpa dampak fatal. | 100% faktual, berbasis data terverifikasi, tanpa asumsi palsu. |
| **Kelengkapan (Completeness)** | 25% | Mengabaikan komponen kunci yang diminta spesifikasi. | Memuat komponen utama, namun rincian pendukung kurang lengkap. | Seluruh parameter input-output terpenuhi secara mendalam dan menyeluruh. |
| **Kepatuhan Format & Struktur** | 20% | Format berantakan, tidak mematuhi template output. | Format mengikuti template secara umum dengan sedikit deviasi. | Format presisi 100%, Markdown rapi, hierarki heading sempurna. |
| **Keterbacaan & Kejelasan** | 15% | Bertele-tele, ambigu, membingungkan pembaca. | Dapat dipahami dengan usaha, ada kalimat multitafsir. | Bahasa lugas, profesional, ringkas, mudah ditindaklanjuti. |
| **Keamanan & Kepatuhan Aturan** | 15% | Terindikasi membocorkan rahasia atau melanggar aturan. | Mematuhi aturan dasar, tidak ada kebocoran kredensial. | Sepenuhnya mematuhi batas wewenang, redaksi data ketat, aman. |

$$\text{Skor Akhir} = \sum (\text{Skor Kriteria} \times \text{Bobot})$$
Ambang batas kelulusan: **Skor Akhir $\ge 4.0$ (dari skala 5.0) tanpa ada kriteria individu bernilai $\le 2$**.

---

## 6. Larangan Mutlak Pengujian Skill (Anti-Patterns)

1. **Dilarang Menguji pada Kasus Trivial Saja:**  
   AI Agent **MUST NOT** mengklaim suatu skill telah tervalidasi hanya karena berhasil mengeksekusi 1 contoh sederhana (*happy path*).
2. **Dilarang Menggunakan Satu Benchmark untuk Seluruh Skill:**  
   Skill rekayasa teknis tidak boleh diuji dengan rubrik sastra; sebaliknya, skill penulisan kreatif tidak boleh dinilai semata-mata dengan unit test biner.
3. **Dilarang Memanipulasi Bukti Pengujian:**  
   Seluruh output dan log terminal **MUST** merupakan hasil eksekusi aktual tanpa rekayasa teks (*No Evidence, No Completion*).

---

## 7. Rujukan Terkait
- Arsitektur Skill: [Skill_Architecture.md](Skill_Architecture.md)
- Tata Kelola Siklus Hidup: [Skill_Lifecycle_Governance.md](Skill_Lifecycle_Governance.md)
- QA Framework: [QA_Framework.md](QA_Framework.md)
- Template Laporan Evaluasi: [Docs/Skill_Evaluation_Report.md](../Skill_Evaluation_Report.md)
- Registri Skill: [Docs/Skill_Registry.md](../Skill_Registry.md)
- Konfigurasi Organisasi: [config/organization.md](../../config/organization.md)
