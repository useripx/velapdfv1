# STANDAR REKAYASA: BASIS DATA & ANTARMUKA API (DATABASE & API STANDARDS)

> **DOCUMENT ID:** STD-ENG-006-DBA  
> **VERSION:** v2.0.0  
> **STATUS:** STANDAR REKAYASA TEKNIS (PILLAR 5 SPECIFICATION)  
> **PENANGGUNG JAWAB:** Lead Developer Role (Rujuk [config/organization.md](../../config/organization.md))  
> **OTORISASI:** Designated Approver (Rujuk [config/organization.md](../../config/organization.md), saat ini: **Yogi Ario**)  
> **TARGET PENGGUNA:** Seluruh Pengembang & AI Agent  
> **ACUAN UTAMA:** [Docs/Standards/Engineering_Standards.md](Engineering_Standards.md) & [Docs/universal-rule.md](../universal-rule.md)  
> **DOKUMEN KONTRAK TERKAIT:** [Docs/API_Contract.md](../API_Contract.md) & [Docs/Database_Design.md](../Database_Design.md)

---

## 1. Pendahuluan

Dokumen ini menetapkan standar teknis untuk perancangan basis data (*Database Engineering*) dan komunikasi antarmuka pemrograman aplikasi (*API Engineering*). Tujuannya adalah memastikan integritas data, konsistensi format respons, performa query yang optimal, dan kemudahan integrasi lintas platform (web, mobile, service-to-service).

Standar ini bersifat **vendor-neutral** dan **framework-agnostic**.

---

## 2. Standar Rekayasa Basis Data (Database Standards)

### 2.1 Konvensi Penamaan (Database Naming Conventions)
- **Nama Tabel:** Menggunakan `snake_case` bentuk jamak (*plural*).  
  *Contoh:* `users`, `orders`, `order_items`, `payment_transactions`.
- **Kolom Kunci Utama (Primary Key):** Wajib dinamai `id` (menggunakan UUID v4 atau BIGINT auto-increment).
- **Kolom Kunci Asing (Foreign Key):** Menggunakan pola `<nama_tabel_tunggal>_id`.  
  *Contoh:* `user_id`, `order_id`.
- **Kolom Tanggal & Waktu:** Menggunakan tipe data ber-timezone (UTC):  
  `created_at`, `updated_at`, `deleted_at` (jika menggunakan soft delete).
- **Kolom Boolean / Status:** Menggunakan awalan predikat:  
  `is_active`, `is_verified`, `has_discount`.

### 2.2 Integritas Data & Indeksasi (Data Integrity & Indexing)
1. **Integritas Relasional:** Seluruh relasi antar-tabel **MUST** memiliki batasan *Foreign Key Constraint* yang jelas (`ON DELETE CASCADE` atau `ON DELETE RESTRICT` sesuai aturan bisnis).
2. **Indeks Wajib:**
   - Kolom yang sering dijadikan klausa pencarian (`WHERE`, `JOIN`, `ORDER BY`).
   - Seluruh kolom *Foreign Key* **wajib diindeks**.
   - Kolom unik (seperti `email`, `username`, `slug`) wajib memiliki *Unique Constraint/Index*.
3. **Pencegahan Over-Indexing:** Hindari membuat indeks pada kolom dengan kardinalitas sangat rendah (misal kolom status dengan hanya 2 nilai) kecuali diperlukan dalam *composite index*.

### 2.3 Manajemen Migrasi Skema (Migration Management)
1. **Version-Controlled Migrations:**  
   Perubahan skema database **DILARANG KERAS** dilakukan manual langsung di console/GUI produksi. Seluruh perubahan **MUST** menggunakan skrip migrasi resmi yang dikelola VCS.
2. **Format Berkas Migrasi:**  
   Menggunakan penomoran urut ber-timestamp:  
   `YYYYMMDDHHMMSS_create_users_table.sql` atau `<nomor>_add_status_to_orders.sql`.
3. **Skenario Up (Maju) dan Down (Rollback):**  
   Setiap berkas migrasi **MUST** menyediakan skrip pembalikan (*down / rollback script*) yang telah diuji dapat memulihkan skema ke kondisi semula tanpa merusak integritas data lain.

### 2.4 Transaksi Basis Data (ACID Transactions)
Untuk operasi yang memodifikasi lebih dari satu tabel dalam satu alur bisnis:
- AI Agent **MUST** membungkus operasi tersebut di dalam satu blok transaksi database (*Database Transaction*).
- Jika salah satu query gagal, seluruh operasi **MUST di-rollback seketika** untuk mencegah inkonsistensi data parsial.

---

## 3. Standar Rekayasa API (Framework-Agnostic API Standards)

### 3.1 Format Komunikasi & URL Path
- **Protokol:** Seluruh komunikasi API **MUST** menggunakan HTTPS dan pertukaran payload JSON (`Content-Type: application/json`).
- **Pola Penamaan Endpoint:** Menggunakan huruf kecil `kebab-case` bentuk jamak:
  * `GET    /api/v1/users` (Daftar entitas)
  * `POST   /api/v1/users` (Buat entitas baru)
  * `GET    /api/v1/users/{id}` (Detail entitas spesifik)
  * `PUT    /api/v1/users/{id}` (Pembaruan entitas penuh)
  * `PATCH  /api/v1/users/{id}` (Pembaruan entitas parsial)
  * `DELETE /api/v1/users/{id}` (Hapus entitas)
- **Versioning Wajib:** URL API **MUST** menyertakan versi mayor pada path (`/api/v1/`, `/api/v2/`).

### 3.2 Struktur Pembungkus Respons Seragam (Standard Response Envelope)

#### Format Respons Sukses:
```json
{
  "success": true,
  "status_code": 200,
  "message": "Resource retrieved successfully",
  "data": {
    "id": "usr_9a8b7c",
    "name": "Alex Mercer",
    "email": "user@example.com",
    "created_at": "2026-11-07T05:00:00Z"
  },
  "meta": {
    "page": 1,
    "limit": 20,
    "total_records": 1
  }
}
```

#### Format Respons Error:
```json
{
  "success": false,
  "status_code": 422,
  "message": "Validation failed for input data",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": [
      {
        "field": "email",
        "message": "The email address is already in use"
      },
      {
        "field": "password",
        "message": "Password must be at least 8 characters"
      }
    ]
  }
}
```

### 3.3 Standar Kode Status HTTP (HTTP Status Codes)
- `200 OK`: Permintaan berhasil (untuk GET, PUT, PATCH).
- `201 Created`: Sumber daya baru berhasil dibuat (untuk POST).
- `204 No Content`: Berhasil tanpa konten kembalian (untuk DELETE sukses).
- `400 Bad Request`: Format payload JSON rusak atau sintaks tidak valid.
- `401 Unauthorized`: Klien belum terautentikasi (token hilang/kedaluwarsa).
- `403 Forbidden`: Klien terautentikasi tetapi tidak memiliki hak akses (RBAC).
- `404 Not Found`: Endpoint atau ID sumber daya tidak ditemukan.
- `422 Unprocessable Entity`: Validasi input bisnis gagal.
- `429 Too Many Requests`: Klien melampaui batas kuota rate limit.
- `500 Internal Server Error`: Kesalahan server internal tak terduga.

### 3.4 Standar Keamanan Autentikasi API
- Autentikasi token **MUST** dikirimkan melalui HTTP Request Header:  
  `Authorization: Bearer <access_token>`
- **DILARANG KERAS** mengirimkan token autentikasi, kunci API, atau password melalui query parameter URL.
- API publik **MUST** dilindungi dengan mekanisme pembatasan frekuensi (*Rate Limiting*) untuk mencegah DoS dan *brute force*.

---

## 4. Rujukan Terkait
- Kontrak API: [Docs/API_Contract.md](../API_Contract.md)
- Perancangan Basis Data: [Docs/Database_Design.md](../Database_Design.md)
- Standar Keamanan: [Security_Engineering.md](Security_Engineering.md)
- Konfigurasi Organisasi: [config/organization.md](../../config/organization.md)
