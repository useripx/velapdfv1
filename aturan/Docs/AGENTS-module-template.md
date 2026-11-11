# AGENTS.md — [Isi Manual: Nama Modul / Folder Source]

> **STATUS:** ATURAN TEKNIS MODULAR (SCOPED AGENT RULES)  
> **LOKASI PENEMPATAN:** Letakkan berkas ini di dalam folder source/modul terkait (misal: `src/modules/<nama-modul>/AGENTS.md` atau `backend/AGENTS.md`).  
> **HIERARKI TATA KELOLA:** Berkas ini adalah dokumen Level 3 (Project-Specific Rules) yang menginduk langsung ke Level 1 ([`Docs/universal-rule.md`](universal-rule.md) / [`Docs/Governance_Hierarchy.md`](Governance_Hierarchy.md)) dan Level 2 ([`Root AGENTS.md`](../AGENTS.md)). **Jika terjadi kontradiksi antara aturan modul ini dengan aturan universal, aturan Level 1 universal-rule.md dan keputusan Yogi Ario selalu menjadi acuan tertinggi.**  
> **PRINSIP UTAMA:** *High Cohesion, Loose Coupling. No Understanding, No Execution. No Plan, No Code. No Approval, No Code. No Verification, No Completion. No Evidence, No Completion. No Validated Skill, No Production Use.*

---

## 1. Identitas & Ruang Lingkup Modul

- **Nama Modul / Sub-Aplikasi:** `[Isi Manual: misal: auth-service, cart-module, GameplaySystem]`
- **Bahasa & Runtime:** `[Isi Manual: TypeScript / C# / PHP / Python / Dart]`
- **Tanggung Jawab Utama (Single Responsibility):**
  [Isi Manual: Jelaskan 1-2 kalimat fungsi spesifik modul ini bagi keseluruhan sistem.]
- **Batasan Ruang Lingkup:**
  - **In-Scope (Termasuk):** [Isi Manual: Fitur/logika yang menjadi hak modul ini]
  - **Out-of-Scope (Dilarang di Modul Ini):** [Isi Manual: Logika yang harus didelegasikan ke modul lain]

---

## 2. Enkapsulasi & Batasan Dependensi (Dependency Boundaries)

Untuk mencegah kode menjadi *spaghetti* dan saling ketergantungan liar (*tight coupling*):

1. **Modul yang Diizinkan Diimpor (Allowed Inbound/Outbound):**
   - `[Isi Manual: Daftar modul/library yang boleh diakses, misal: Shared/Types, Core/Database]`
2. **Larangan Impor Liar:**
   - Dilarang keras mengimpor berkas internal/privat dari modul tetangga secara langsung.
   - Komunikasi antar modul **wajib melalui kontrak publik (Public Interface / API Contract / Event Bus / DTO)**.
3. **Penyimpanan State:**
   - Modul ini mengelola state lokalnya sendiri: `[Isi Manual: misal: Redux Slice lokal / Zustand / Service State]`.
   - Dilarang memodifikasi state modul lain tanpa melalui aksi/method resmi.

---

## 3. Pola Arsitektur Internal Folder

Struktur direktori internal yang wajib dipatuhi di dalam folder ini:

```text
[Isi Manual: Nama Modul]/
├── AGENTS.md                      # Berkas aturan teknis lokal ini
├── api/                           # [Bila Ada] Kontrak publik / interface untuk modul luar
├── controllers/                   # [Bila Ada] Penangan request / input
├── services/                      # Logika bisnis utama modul
├── models/ / entities/            # Struktur data dan tipe internal
├── repositories/                  # [Bila Ada] Akses data / persistensi lokal
└── tests/                         # Suite pengujian unit khusus modul ini
```

---

## 4. Konvensi Penamaan & Standar Kode Lokal

- **Penamaan Berkas:** `[Isi Manual: kebab-case.ts / PascalCase.cs / snake_case.py]`
- **Penamaan Interface / Class:** `[Isi Manual: misal: IUserRepository / AuthService]`
- **Error Handling:** 
  - Seluruh error harus dibungkus menggunakan tipe custom error lokal: `[Isi Manual: misal: ModuleCustomException]`.
  - Jangan membiarkan unhandled rejection bocor ke modul lain.

---

## 5. Pengujian Mandiri Lokal (Local Test Suite)

Sebelum mengajukan perubahan modul ini ke tahap integrasi proyek global:

- **Perintah Uji Lokal:** `[Isi Manual: misal: npm test -- src/modules/<nama-modul> / dotnet test --filter <nama-modul>]`
- **Target Kelulusan:** `100% PASS (0 GAGAL / 0 ERROR)`
- **Cakupan Pengujian:** Minimal menguji kasus nominal (happy path) dan penanganan input salah (edge cases).

---

## 6. Checklist Sebelum Menyelesaikan Tugas di Modul Ini

- [ ] Perubahan kode tidak melanggar batasan tanggung jawab modul.
- [ ] Tidak ada impor langsung ke file privat modul lain.
- [ ] Pengujian unit lokal modul telah dieksekusi dan membuktikan **100% PASS**.
- [ ] Rencana perubahan tetap dilaporkan dan disetujui melalui *Approval Gate* Yogi Ario di tingkat proyek.

---

> [!tip] PETUNJUK PENGGUNAAN
> Salin berkas ini ke dalam folder source baru saat membuat modul independen atau sub-aplikasi monorepo. Sesuaikan bagian `[Isi Manual]` dengan domain teknis modul Anda. Hapus catatan tip ini setelah disesuaikan.

