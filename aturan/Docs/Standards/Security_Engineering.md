# Security Engineering Standards

> **STATUS:** STANDAR REKAYASA TEKNIS (PILLAR 6 SPECIFICATION)  
> **OTORITAS TERTINGGI:** Yogi Ario (PROJECT OWNER / LEAD DEVELOPER)  
> **TARGET PENGGUNA:** Seluruh Pengembang & AI Agent  
> **ACUAN UTAMA:** [Docs/Standards/Engineering_Standards.md](Engineering_Standards.md) & [Docs/universal-rule.md](../universal-rule.md)  
> **PRINSIP TATA KELOLA:** *Zero Secret Leakage. Secure by Design.*

---

## 1. Pendahuluan & Filosofi Keamanan

Keamanan sistem informasi bukan merupakan fase tambahan di akhir proyek, melainkan kewajiban fundamental yang terintegrasi di setiap baris kode sejak menit pertama (*Shift-Left Security*).

Prinsip Utama:
> **"Jangan pernah mengasumsikan sistem aman hanya karena aplikasi dapat berjalan normal tanpa crash."**

Dokumen ini mengadopsi prinsip industri terbaik, termasuk pedoman **OWASP Top 10**, untuk melindungi perangkat lunak dari kerentanan eksploitasi, kebocoran data, dan penyalahgunaan hak akses.

---

## 2. Tata Kelola Kunci Rahasia & Kredensial (Zero Secret Leakage)

Kunci API, token rahasia, password database, dan sertifikat privat adalah aset paling sensitif:

1. **Perlindungan `.gitignore` Sejak Inisiasi:**  
   Berkas konfigurasi lingkungan (`.env`, `.env.local`, `.env.*.local`), kunci SSH, berkas sertifikat (`*.pem`, `*.key`), dan berkas credentials (`secrets.json`, service account JSON) **MUST** tercantum dalam `.gitignore` sebelum commit pertama dilakukan.
2. **LARANGAN MUTLAK HARDCODED SECRETS:**  
   AI Agent dan pengembang **DILARANG KERAS** menuliskan kunci rahasia secara langsung di dalam kode program, skrip pengujian, template mock data, atau commit message:
   ```typescript
   // ❌ DILARANG KERAS:
   const STRIPE_SECRET = "<HARDCODED_SECRET_KEY_EXAMPLE>";

   // ✅ WAJIB: Gunakan variabel lingkungan yang aman:
   const STRIPE_SECRET = process.env.STRIPE_SECRET_KEY;
   if (!STRIPE_SECRET) {
     throw new Error("Missing required environment variable: STRIPE_SECRET_KEY");
   }
   ```
3. **Template Konfigurasi Bersih (`.env.example`):**  
   Setiap repositori **MUST** menyediakan berkas `.env.example` yang mencantumkan nama seluruh variabel yang dibutuhkan dengan nilai *placeholder* dummy (misal `STRIPE_SECRET_KEY=your_stripe_secret_here`).
4. **Tindakan Jika Terjadi Kebocoran Kredensial:**  
   Jika kunci rahasia tidak sengaja ter-commit ke Git:
   - Anggap kunci tersebut **telah terkompromi (COMPROMISED)**.
   - Segera lakukan rotasi/pembatalan kunci (*revoke & regenerate key*) pada penyedia layanan terkait.
   - Hapus berkas dari riwayat Git menggunakan pembersihan riwayat yang aman.

---

## 3. Autentikasi & Otorisasi Berbasis Peran (Auth & RBAC)

1. **Penyimpanan Kata Sandi (Password Hashing):**  
   Kata sandi **MUST NOT** disimpan dalam format plain text atau algoritma hash usang (seperti MD5 atau SHA-1). Wajib menggunakan algoritma hashing adaptif dengan salt yang kuat: **Argon2id** atau **Bcrypt** (cost factor minimal 10).
2. **Autentikasi Token Aman (JWT Best Practices):**
   - Gunakan algoritma tanda tangan asimetris kuat (RS256 atau EdDSA) atau simetris yang aman (HS256 dengan secret minimal 256-bit).
   - Tetapkan masa kedaluwarsa (*expiration time*) token akses secara ketat (misal 15-60 menit), didampingi *refresh token* dengan rotasi berkala.
3. **Prinsip Hak Akses Terendah (Principle of Least Privilege):**  
   Pengguna atau layanan hanya diberikan hak akses minimum yang mutlak diperlukan untuk fungsinya.
4. **Otorisasi Berbasis Peran (Role-Based Access Control / RBAC):**  
   Setiap endpoint yang menyajikan atau memodifikasi data **MUST** memverifikasi hak akses pengguna sebelum mengeksekusi logika bisnis.

---

## 4. Pencegahan Kerentanan Umum (OWASP Top 10 Mitigation)

### 4.1 Pencegahan SQL Injection (SQLi)
- Seluruh interaksi query database **MUST** menggunakan *Parameterized Queries* atau *Object-Relational Mapping (ORM)* terpercaya.
- **DILARANG KERAS** menggabungkan string (*string concatenation*) input pengguna langsung ke dalam klausa SQL.

### 4.2 Pencegahan Cross-Site Scripting (XSS)
- Seluruh data yang berasal dari luar sistem yang akan dirender ke dalam antarmuka web **MUST** di-*escape* secara otomatis melalui template engine yang aman.
- Gunakan header keamanan browser standar:
  * `Content-Security-Policy (CSP)`
  * `X-Content-Type-Options: nosniff`
  * `X-Frame-Options: DENY`

### 4.3 Pencegahan Cross-Site Request Forgery (CSRF)
- Untuk endpoint berbasis sesi browser (*stateful cookie-based session*), gunakan token CSRF unik yang divalidasi pada setiap mutasi data (POST, PUT, DELETE) serta pasang atribut `SameSite=Lax` atau `SameSite=Strict` pada cookies.

### 4.4 Pencegahan Broken Object Level Authorization (BOLA / IDOR)
- Saat pengguna meminta detail data berdasarkan ID (misal: `/api/v1/orders/123`), sistem **MUST** memvalidasi bahwa order `#123` tersebut benar-benar milik pengguna yang sedang terotentikasi, bukan milik pengguna lain.

---

## 5. Audit Keamanan Dependensi (Dependency Security)

1. **Pemeriksaan Kerentanan Rutin:**  
   Jalankan alat audit dependensi otomatis secara berkala (`npm audit`, `pip-audit`, `composer audit`, `cargo audit`).
2. **Pembaruan Paket Keamanan:**  
   Dependensi yang teridentifikasi memiliki kerentanan dengan tingkat *High* atau *Critical* pada basis data CVE **wajib segera diperbarui** setelah dilakukan analisis dampak regresi.
3. **Kunci Versi Dependensi (Lockfiles):**  
   Seluruh berkas lockfile (`package-lock.json`, `composer.lock`, `poetry.lock`, dll.) **MUST** diikutsertakan dalam repositori Git untuk menjamin determinisme versi instalasi.

---

## 6. Perlindungan Aset & Folder Desain Mentah (Read-Only)

- Folder prototipe mentah (`Material/`, `design/`, `mockups/`) yang disediakan tim UI/UX berstatus **READ-ONLY**.
- AI Agent **DILARANG KERAS** memodifikasi, menimpa, atau menghapus aset desain mentah asli. Aset yang digunakan dalam produksi disalin dan dioptimasi ke folder aset produksi resmi.
