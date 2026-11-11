# Requirements Engineering Standard

> **STATUS:** STANDAR REKAYASA TEKNIS (PILLAR 1 SPECIFICATION)  
> **OTORITAS TERTINGGI:** Yogi Ario (PROJECT OWNER / LEAD DEVELOPER)  
> **TARGET PENGGUNA:** Seluruh Pengembang & AI Agent  
> **ACUAN UTAMA:** [Docs/Standards/Engineering_Standards.md](Engineering_Standards.md) & [Docs/universal-rule.md](../universal-rule.md)  
> **PRINSIP TATA KELOLA:** *No Understanding, No Execution. No Plan, No Code.*

---

## 1. Pendahuluan

Tujuan utama dari standar rekayasa kebutuhan (*Requirements Engineering*) adalah memastikan bahwa setiap fitur, perubahan logika, atau perbaikan bug berawal dari pemahaman yang presisi terhadap kebutuhan pengguna dan batasan sistem. AI Agent **MUST NOT** mengasumsikan kebutuhan yang tidak jelas atau mengeksekusi kode berdasarkan dugaan sepihak (*No Understanding, No Execution*).

---

## 2. Taksonomi & Konvensi Penomoran Kebutuhan (Requirement ID)

Setiap kebutuhan perangkat lunak **MUST** memiliki identitas unik (*Requirement ID*) yang konsisten di seluruh siklus hidup proyek:

### 2.1 Pola Format Penomoran:
$$\text{REQ-}[\text{TYPE}]-[\text{MODULE}]-[\text{NUMBER}]$$

- `TYPE`:
  * `F`: Kebutuhan Fungsional (*Functional Requirement*) — perilaku atau fitur yang harus dapat dilakukan oleh sistem.
  * `NF`: Kebutuhan Non-Fungsional (*Non-Functional Requirement*) — karakteristik kualitas, performa, keamanan, reliabilitas, atau ketersediaan.
  * `C`: Batasan Sistem (*Constraint Requirement*) — batasan teknologi, regulasi, lisensi, atau platform.
- `MODULE`: 3-4 huruf kapital singkatan nama domain/modul (contoh: `AUTH`, `CART`, `DASH`, `PAYM`, `CORE`).
- `NUMBER`: 3 digit angka berurutan dimulai dari `001`.

*Contoh ID Kebutuhan:*
- `REQ-F-AUTH-001`: Pengguna dapat mendaftarkan akun baru menggunakan alamat email dan kata sandi.
- `REQ-NF-AUTH-001`: Waktu respons autentikasi token JWT tidak boleh melebihi 200 ms pada beban normal.
- `REQ-C-CORE-001`: Aplikasi harus dapat berjalan secara offline murni tanpa ketergantungan koneksi internet (*Offline-First*).

---

## 3. Struktur Spesifikasi Kebutuhan

Setiap spesifikasi kebutuhan dalam dokumen proyek **MUST** memuat elemen-elemen berikut:

1. **Requirement ID & Judul:** Identitas unik dan deskripsi ringkas.
2. **Prioritas (MoSCoW Method):**
   - **Must Have (P0):** Wajib ada untuk peluncuran MVP.
   - **Should Have (P1):** Sangat penting namun memiliki alternatif sementara.
   - **Could Have (P2):** Diinginkan jika sumber daya dan waktu mencukupi.
   - **Won't Have (P3):** Disepakati untuk tidak dikerjakan pada rilis saat ini.
3. **Deskripsi Kebutuhan (User Story / Functional Description):**  
   Format standar:  
   *Sebagai [peran pengguna], saya ingin [tindakan yang dilakukan], sehingga [manfaat yang diperoleh].*
4. **Kriteria Penerimaan Terukur (Measurable Acceptance Criteria):**  
   Kriteria objektif yang menentukan apakah kebutuhan tersebut telah terpenuhi secara nyata. Menggunakan format skenario **Given-When-Then** atau checklist spesifik:
   - *Given (Kondisi Awal):* Pengguna berada di halaman registrasi dengan form kosong.
   - *When (Aksi):* Pengguna mengisi email valid dan password minimal 8 karakter lalu menekan tombol "Daftar".
   - *Then (Hasil yang Diharapkan):* Akun berhasil dibuat, sistem mengembalikan status HTTP 201, dan pengguna diarahkan ke dasbor utama.
5. **Asumsi & Ketergantungan (Assumptions & Dependencies):**  
   Kondisi eksternal yang diasumsikan benar (misal: engine database sudah terpasang, API pihak ketiga aktif).
6. **Risiko Awal (Initial Risks):**  
   Potensi kendala teknis dan dampaknya terhadap jadwal atau performa.

---

## 4. Matriks Ketertelusuran Kebutuhan (Requirements Traceability Matrix - RTM)

Ketertelusuran (*traceability*) adalah kemampuan melacak perjalanan sebuah kebutuhan dari perumusan, rencana implementasi, modifikasi berkas kode, hingga pembuktian kasus uji:

```text
[Requirement ID] ──► [Task ID / Implementation Plan] ──► [File Target] ──► [Test Case ID / Evidence]
```

### Tabel RTM Standar:
| Req ID | Deskripsi Singkat | Prioritas | Berkas Implementasi Terkait | Test Case ID | Status Verifikasi |
| :--- | :--- | :---: | :--- | :--- | :---: |
| `REQ-F-AUTH-001` | Registrasi Akun Baru | Must Have | `src/controllers/AuthController.ts`<br>`src/services/AuthService.ts` | `TC-AUTH-001` | **PASS (100%)** |
| `REQ-NF-AUTH-001`| Latensi Auth < 200ms | Should Have | `src/middleware/jwt.ts` | `TC-PERF-001` | **PASS (100%)** |

---

## 5. Protokol Manajemen Perubahan Kebutuhan (Requirements Change Management)

Kebutuhan perangkat lunak dapat berkembang seiring berjalannya proyek. Namun, perubahan kebutuhan **MUST NOT** diakomodasi secara diam-diam (*silent creep*):

1. **Identifikasi Perubahan:**  
   Jika ada instruksi baru atau masukan dari Yogi Ario yang mengubah atau menambah kebutuhan:
2. **Analisis Dampak (Impact Analysis):**  
   AI Agent **MUST** menganalisis berkas kode yang terdampak, dependensi yang berubah, potensi regresi, serta estimasi penyesuaian jadwal.
3. **Penyajian Proposal Addendum:**  
   AI Agent menyajikan perubahan dalam draf revisi *Implementation Plan* kepada Yogi Ario.
4. **Otorisasi Resmi:**  
   Perubahan kebutuhan hanya boleh diimplementasikan setelah Yogi Ario menyetujui addendum tersebut secara tertulis (*No Approval, No Code*).
