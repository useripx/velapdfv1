# Coding Standards & Clean Implementation

> **STATUS:** STANDAR REKAYASA TEKNIS (PILLAR 3 SPECIFICATION)  
> **OTORITAS TERTINGGI:** Yogi Ario (PROJECT OWNER / LEAD DEVELOPER)  
> **TARGET PENGGUNA:** Seluruh Pengembang & AI Agent  
> **ACUAN UTAMA:** [Docs/Standards/Engineering_Standards.md](Engineering_Standards.md) & [Docs/universal-rule.md](../universal-rule.md)  
> **PRINSIP TATA KELOLA:** *No Plan, No Code. No Verification, No Completion.*

---

## 1. Pendahuluan

Dokumen ini mendefinisikan standar kualitas penulisan kode sumber (*code quality and craftsmanship*) yang berlaku untuk seluruh pengembang dan AI Agent. Standar ini bersifat *stack-agnostic*, dirancang untuk menghasilkan kode yang bersih, aman, mudah dipahami, mudah diuji, dan berumur panjang tanpa menimbulkan utang teknis (*technical debt*).

---

## 2. Empat Standar Presisi Penulisan Kode (Clean & Surgical Coding)

Setiap baris kode yang ditulis **MUST** mematuhi 4 pilar presisi berikut:

### 2.1 Think Before Coding (Pikirkan Sebelum Menulis Kode)
- Nyatakan asumsi teknis secara eksplisit sebelum mengetik kode. Jika terdapat keraguan atau ambiguitas logika, tanyakan kepada Yogi Ario.
- Jika ada beberapa interpretasi solusi, sajikan seluruh opsi beserta *trade-off* — **dilarang memilih pendekatan sepihak secara diam-diam**.
- Jika terdapat pendekatan yang jauh lebih sederhana dan efisien, sampaikan secara transparan.

### 2.2 Simplicity First & Anti-Overengineering (Utamakan Kesederhanaan)
- Tulis kode minimum yang menyelesaikan masalah secara tuntas dan elegan.
- **Dilarang membuat fitur spekulatif:** Jangan menambahkan kode untuk skenario "siapa tahu nanti butuh" (*You Aren't Gonna Need It - YAGNI*).
- **Hindari abstraksi prematur:** Jangan membuat pola desain rumit (*over-abstraction*) untuk logika yang hanya digunakan satu kali. Jika 50 baris kode sudah cukup, jangan menulis 200 baris.

### 2.3 Surgical Changes (Modifikasi Kode Presisi Bedah)
- **Sentuh Hanya Apa yang Wajib Diubah:** Modifikasi hanya berkas dan fungsi yang relevan dengan tugas yang disetujui.
- **Don't Touch Unbroken Adjacent Code:** Dilarang keras mengubah, merefaktor, atau memformat ulang kode di sekitar yang sudah berjalan normal (*don't touch unbroken adjacent code*).
- **Clean Up Only Your Own Mess:** Bersihkan variabel, impor, atau fungsi sementara yang Anda buat. Jangan menghapus *dead code* lama yang bukan buatan Anda pada sesi aktif kecuali diminta secara eksplisit.
- **Pertahankan Gaya Penulisan Eksisting:** Sesuaikan gaya indentasi, penamaan, dan struktur dengan basis kode yang telah ada.

### 2.4 Goal-Driven Execution (Eksekusi Berbasis Sasaran Terverifikasi)
- Tetapkan kriteria keberhasilan yang terukur sebelum menulis kode.
- Uji dan ulangi proses hingga terbukti lulus tanpa error (*Loop until verified 100% PASS*).
- Setiap baris kode yang ditambahkan atau diubah harus dapat dilacak langsung ke kebutuhan pada *Implementation Plan*.

---

## 3. Konvensi Penamaan (Naming Conventions)

Penamaan harus deskriptif, bermakna, dan mencerminkan maksud bisnis (*intent-revealing*):

| Kategori Kode | Konvensi Standar | Contoh Baik | Contoh Buruk |
| :--- | :--- | :--- | :--- |
| **Kelas / Interface / Tipe** | `PascalCase` | `UserAuthenticationService`, `InvoiceRepository` | `user_service`, `dataClass` |
| **Fungsi / Method** | `camelCase` (kata kerja awal) | `calculateTotalDiscount()`, `getUserProfile()` | `calc()`, `data()`, `doStuff()` |
| **Variabel / Properti** | `camelCase` (kata benda) | `userEmail`, `isAccountVerified` | `temp`, `a`, `flag1`, `val` |
| **Konstanta / Enums** | `UPPER_SNAKE_CASE` | `MAX_RETRY_ATTEMPTS`, `DEFAULT_PAGE_SIZE` | `maxRetry`, `limit` |
| **Berkas Kode Sumber** | `kebab-case` atau `PascalCase` (konsisten per bahasa) | `user-controller.ts` atau `UserController.cs` | `User_controller.TS`, `test1.js` |
| **Tabel Basis Data** | `snake_case` (jamak/plural) | `user_profiles`, `order_items` | `UserProfile`, `tblOrders` |

---

## 4. Penanganan Kesalahan (Error Handling Standards)

Pengelolaan error yang buruk adalah sumber utama kegagalan produksi. Aturan mutlak:

1. **DILARANG KERAS Menyembunyikan Error (No Silent Error Swallowing):**  
   AI Agent **MUST NOT** menangkap exception hanya untuk mendiamkannya atau mengabaikannya:
   ```typescript
   // ❌ DILARANG KERAS (Silent Catch / Swallow Error):
   try {
     processPayment(orderId);
   } catch (e) {
     // Dibiarkan kosong atau hanya console.log tanpa penanganan
   }

   // ✅ WAJIB: Tangani dengan tepat atau teruskan (rethrow):
   try {
     await processPayment(orderId);
   } catch (error) {
     logger.error("Payment processing failed for order", { orderId, error: error.message });
     throw new PaymentProcessingException(`Failed to process payment for order ${orderId}`, { cause: error });
   }
   ```
2. **Fail Fast & Explicit:**  
   Validasi prasyarat di awal fungsi (*guard clauses*). Jika data tidak valid, lempar error seketika sebelum mengeksekusi logika yang lebih dalam.
3. **Pesan Kesalahan Bermakna & Aman:**  
   Pesan error internal harus mencantumkan konteks masalah teknis, namun pesan error yang dikembalikan ke antarmuka pengguna (client) **MUST NOT** membocorkan struktur database, stack trace internal, atau kunci sensitif.

---

## 5. Standar Pencatatan Log (Structured Logging)

Pencatatan log bertujuan untuk mempermudah diagnosa dan observabilitas sistem:

1. **Format Log Terstruktur:**  
   Gunakan format terstruktur (JSON atau format ber-timestamp) dengan metadata relevan (Timestamp, Log Level, Message, Context Object, Request ID).
2. **Hierarki Level Log:**
   - `DEBUG`: Informasi detail untuk debugging lokal (dinonaktifkan di produksi).
   - `INFO`: Kejadian normal sistem (layanan dimulai, proses batch selesai).
   - `WARN`: Kondisi tidak biasa namun sistem masih dapat berjalan (retry terjadi, degradasi layanan sementara).
   - `ERROR`: Kegagalan fungsional yang memerlukan perhatian teknis (database query gagal, unhandled exception).
3. **LARANGAN MUTLAK PENCATATAN DATA SENSITIF (Zero Secret in Logs):**  
   AI Agent **MUST NOT** mencatat kredensial rahasia ke dalam file log:
   - Dilarang mencatat kata sandi (*password / plain text PIN*).
   - Dilarang mencatat kunci API, token JWT rahasia, atau signature rahasia.
   - Dilarang mencatat data finansial (CVV, nomor kartu kredit penuh) atau data PII sensitif.

---

## 6. Validasi Input di Gerbang Batas Sistem (Input Validation)

1. **Prinsip Zero Trust:**  
   Jangan pernah mempercayai data masukan dari luar sistem (user form, query param, payload webhook, third-party API).
2. **Validasi Skema Kuat (Schema Validation):**  
   Gunakan library validasi skema (seperti Zod, Joi, FluentValidation, Pydantic, atau validator bawaan framework) pada lapisan pengontrol (*Controller/Entry Point*).
3. **Sanitasi Data:**  
   Bersihkan data input dari karakter berbahaya (*strip tags, escape SQL, normalisasi string*) sebelum diproses oleh domain logic.

---

## 7. Dokumentasi Kode & Komentar yang Bermakna

1. **Self-Documenting Code First:**  
   Tulis kode yang jelas dengan nama variabel dan fungsi yang berbicara sendiri, sehingga meminimalkan kebutuhan komentar penjelas alur sederhana.
2. **Jelaskan "Mengapa" (Why), Bukan "Apa" (What):**  
   Komentar kode harus menjelaskan alasan bisnis, batasan teknis yang aneh, atau pencegahan bug khusus, bukan menjelaskan hal yang sudah jelas terbaca dari kode:
   ```typescript
   // ❌ Komentar Buruk (Menjelaskan apa yang sudah jelas):
   // Menambah i dengan 1
   i = i + 1;

   // ✅ Komentar Baik (Menjelaskan 'mengapa' dan konteks bisnis):
   // Timeout diatur ke 5000ms karena gateway pihak ketiga memerlukan waktu hingga 4 detik pada jam sibuk (lihat tiket #142)
   const TIMEOUT_MS = 5000;
   ```
3. **Preserve Documentation Integrity:**  
   Jangan pernah menghapus komentar atau docstring lama yang tidak berkaitan dengan perubahan Anda.
