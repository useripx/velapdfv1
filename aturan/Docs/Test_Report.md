# Test Report — [Isi Manual: Nama Fitur / Modul / Proyek]

> **STATUS GERBANG PENGUJIAN:** `[NOT_RUN | IN_PROGRESS | PASS | FAILED | BLOCKED]`  
> **PROYEK:** [Isi Manual: Nama Proyek]  
> **DESIGNATED APPROVER:** Rujuk [config/organization.md](../config/organization.md) (saat ini: Yogi Ario)  
> **PENGUJI:** [Isi Manual: Peran AI Agent / Pengembang]  
> **TANGGAL PENGUJIAN:** [Isi Manual: YYYY-MM-DD HH:mm WIB]  
> **BRANCH:** `[Isi Manual: feature/<nama-fitur> atau develop]`  
> **ACUAN STANDAR:** [Docs/Standards/QA_Framework.md](Standards/QA_Framework.md) & [Docs/Standards/Verification_Evidence.md](Standards/Verification_Evidence.md)  
> **PRINSIP UTAMA:** *No Verification, No Completion. No Evidence, No Completion. Quality Must Be Demonstrated, Not Assumed.*

---

## 1. Ringkasan Eksekusi Pengujian (Executive Test Summary)

| Metrik Kualitas | Hasil Aktual | Ambang Batas Gerbang (Threshold) | Status Gerbang |
| :--- | :---: | :---: | :---: |
| **Total Test Cases** | `[0]` | Minimal 1 per kebutuhan kritis | `[PENDING]` |
| **Passed (Lulus)** | `[0]` | Seluruh test mandatory wajib lulus | `[PENDING]` |
| **Failed (Gagal)** | `[0]` | Wajib 0 untuk promosi rilis | `[PENDING]` |
| **Blocked / Skipped**| `[0]` | Memerlukan justifikasi tertulis sah | `[PENDING]` |
| **Defects Terbuka / Ditutup** | `[0 / 0]` | Seluruh defect kritis wajib tuntas | `[PENDING]` |
| **Code Coverage** | `[0%]` | Target `>= 80%` logika bisnis (bila relevan) | `[PENDING]` |
| **Status Akhir Gerbang** | `[PENDING]` | Seluruh pengujian wajib lulus | `[GATE 4 PENDING]` |

---

## 2. Lingkungan & Alat Pengujian (Test Environment)

- **Sistem Operasi:** [Windows / Linux / macOS]
- **Runtime & Version:** [Isi Manual: Node.js / .NET / Python / PHP dll.]
- **Test Runner Framework:** [Isi Manual: Jest, Vitest, PyTest, NUnit, PHPUnit, CLI scripts]
- **Browser Automation (Opsional):** Chrome DevTools MCP (khusus antarmuka web)
- **Perintah Eksekusi:** `[Isi Manual: perintah terminal untuk menjalankan test]`
- **Basis Data Uji:** `[SQLite In-Memory / Docker Test DB / Mock Data]` *(Dilarang menggunakan DB Produksi)*

---

## 3. Rincian Kasus Uji (Test Case Breakdown & Traceability)

| Test Case ID | Req ID Terkait | Modul / Skenario | Data Uji (Input) | Hasil yang Diharapkan | Status | Referensi Bukti |
| :---: | :---: | :--- | :--- | :--- | :---: | :--- |
| `TC-[MOD]-001` | `REQ-F-[MOD]-001` | [Skenario pengujian 1] | [Input data sintetis] | [Hasil yang diharapkan] | `[NOT_RUN]` | [Tautan log / file] |
| `TC-[MOD]-002` | `REQ-F-[MOD]-002` | [Skenario pengujian 2] | [Input data sintetis] | [Hasil yang diharapkan] | `[NOT_RUN]` | [Tautan log / file] |

---

## 4. Bukti Konkret Pengujian (Concrete Evidence)

### 4.1 Bukti Log Eksekusi Terminal (Raw Console Output)
```text
[Tempelkan cuplikan output asli dari test runner terminal / CLI verifikasi di sini]
```

### 4.2 Bukti Faktual Multi-Modal (Visual / Diff / Response)
- **Bukti Valid / Berhasil:**
  * `[Tautan ke Docs/Evidence/SS/valid/ atau cuplikan git diff / response payload]`
- **Bukti Kendala / Error (Jika Ada Defect Ditemukan):**
  * `[Tautan ke Docs/Evidence/SS/error/ atau log error mentah]`

---

## 5. Ringkasan Pengelolaan Defect (Defect Retesting Summary)

| Defect ID | Judul Bug / Masalah | Severity | Status Awal | Status Retesting | Verifikasi Akhir |
| :---: | :--- | :---: | :---: | :---: | :---: |
| *Belum ada defect* | - | - | - | - | `[CLEAN]` |

---

## 6. Verifikasi Integritas Sistem & Kepatuhan Standar

- [ ] Test suite telah dijalankan di lingkungan lokal dan seluruh pengujian wajib menghasilkan status **PASS**.
- [ ] Tidak ada secret, token rahasia, atau kredensial nyata yang terekspos pada berkas bukti log maupun artefak.
- [ ] Berkas aset mentah tim UI/UX (`Material/`) terbukti utuh tanpa modifikasi.
- [ ] Seluruh perubahan terikat langsung dengan *Acceptance Criteria* dan kriteria pengujian.

---

> [!tip] PETUNJUK PENGGUNAAN TEMPLATE
> Sesuaikan isi dokumen ini setiap kali menjalankan siklus pengujian fitur atau modul baru. Hapus baris placeholder dan isi dengan data eksekusi riil.
