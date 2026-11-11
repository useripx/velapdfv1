# API Contract — [Isi Manual: Nama Proyek]

> **STATUS DOKUMEN:** KONTRAK SPESIFIKASI ENDPOINT API (VENDOR-NEUTRAL)  
> **DESIGNATED APPROVER:** Rujuk [config/organization.md](../config/organization.md) (saat ini: Yogi Ario)  
> **PENANGGUNG JAWAB TEKNIS:** [Isi Manual: Peran AI Agent / Pengembang]  
> **CATATAN:** Jika proyek ini adalah aplikasi offline murni (*standalone offline*), berkas ini dapat ditandai `N/A` atau dihapus sesuai instruksi tata kelola universal.  
> **ACUAN STANDAR:** [Docs/Standards/Database_API_Standards.md](Standards/Database_API_Standards.md)

---

## 1. Konfigurasi Lingkungan & Base URL

| Lingkungan | Base URL | Status |
| :--- | :--- | :---: |
| **Lokal (Development)** | `http://localhost:[Isi Manual: Port]/api/v1` | Aktif |
| **Staging / UAT** | `https://staging-api.[Isi Manual: domain].com/api/v1` | Standby |
| **Produksi** | `https://api.[Isi Manual: domain].com/api/v1` | Siap Rilis |

---

## 2. Standar Header & Autentikasi

### 2.1 Header Permintaan Standar
```http
Content-Type: application/json
Accept: application/json
Authorization: Bearer <access_token>
```

### 2.2 Amplop Respons Standar (Standard JSON Response Envelope)

#### Format Respons Berhasil (Success — 200 / 201):
```json
{
  "success": true,
  "status_code": 200,
  "message": "Operation executed successfully",
  "data": {
    "id": "[entity_id]",
    "item": "[data_payload]"
  },
  "timestamp": "2026-11-07T08:00:00Z"
}
```

#### Format Respons Gagal (Error — 400 / 401 / 404 / 422 / 500):
```json
{
  "success": false,
  "status_code": 422,
  "message": "Validation failed for request input",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": [
      {
        "field": "[field_name]",
        "message": "[error_description]"
      }
    ]
  },
  "timestamp": "2026-11-07T08:00:00Z"
}
```

---

## 3. Spesifikasi Endpoint

### 3.1 Modul: [Isi Manual: Nama Modul, misal: Autentikasi / Pengguna]

#### 3.1.1 [Isi Manual: Nama Endpoint, misal: Login Pengguna]
- **Method:** `POST`
- **Path:** `/auth/login`
- **Autentikasi Diperlukan:** `Tidak (Public)`
- **Deskripsi:** Melakukan verifikasi kredensial pengguna dan mengembalikan token otorisasi.

**Request Body (JSON):**
```json
{
  "username": "user@example.com",
  "password": "[REDACTED_PASSWORD]"
}
```

**Response 200 OK (Berhasil):**
```json
{
  "success": true,
  "status_code": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOi...",
    "expires_in": 3600,
    "user": {
      "id": "usr_01",
      "name": "Jane Doe",
      "role": "user"
    }
  },
  "timestamp": "2026-11-07T08:00:00Z"
}
```

**Response 401 Unauthorized (Kredensial Salah):**
```json
{
  "success": false,
  "status_code": 401,
  "message": "Invalid username or password",
  "error": {
    "code": "AUTH_INVALID_CREDENTIALS"
  },
  "timestamp": "2026-11-07T08:00:00Z"
}
```

---

### 3.2 Modul: [Isi Manual: Nama Modul Berikutnya]
*(Salin struktur di atas untuk setiap endpoint baru yang dibangun)*

---

> [!tip] PETUNJUK KHUSUS TEMPLATE
> Pastikan tipe data, nama field, dan kode status HTTP telah disepakati sebelum komponen terintegrasi. Hapus catatan tip ini setelah dokumen disesuaikan dengan proyek Anda.
