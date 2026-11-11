# SPESIFIKASI LINGKUNGAN PENGUJIAN KOMPUTASI (TESTBED INVARIANCE)

> **Mata Kuliah:** Metodologi Penelitian (Metopen) TA 2026/2027  
> **Mahasiswa:** Yogi Ario Pratama | NPM: 2313020004 | Kelas: 4B  
> **Program Studi:** S-1 Teknik Informatika, Universitas Nusantara PGRI Kediri  
> **Dosen Pembimbing Proposal:** Rony Heri Irawan, M.Kom.  
> **Judul Usulan Penelitian:** *Implementasi Rule-Based Inference pada Sistem VelaPDF*  
> **Fokus Dokumen:** Penguncian parameter lingkungan pengujian guna menjamin keterulangan (*reproducibility*) saintifik, pemenuhan berkas T32, Laporan 2 (Bab III), dan Tugas 5.

---

## 1. Urgensi Penguncian Testbed (Testbed Invariance)

Berdasarkan *Universal Rule* dan Petunjuk Teknis Penugasan Metodologi Penelitian, evaluasi kinerja komputasi cerdas lokal (seperti latensi OCR, durasi inferensi *Forward Chaining*, dan konsumsi memori) sangat dipengaruhi oleh spesifikasi perangkat keras dan konfigurasi runtime. Untuk mencegah variansi pengukuran yang tidak terkontrol, seluruh pengujian eksperimental dan operasional sistem VelaPDF dikunci secara ketat (*frozen environment*) pada spesifikasi di bawah ini.

---

## 2. Profil Perangkat Keras Uji Mandiri (Target Physical Mobile Device)

Sistem VelaPDF diuji secara nyata menggunakan gawai fisik (*physical target device*) kelas menengah (*mid-range*) yang merepresentasikan karakteristik umum perangkat pengguna:

| Parameter Perangkat Keras | Spesifikasi Teknis Terkunci | Justifikasi / Catatan Operasional |
| :--- | :--- | :--- |
| **Model Gawai / Brand** | **Xiaomi Redmi Note 11** (Spesifikasi Acuan) | Perangkat kelas menengah umum di lingkungan mahasiswa/kampus. |
| **Chipset / SoC** | Qualcomm Snapdragon 680 (SM6225) 6nm | Representasi CPU arsitektur ARM64 hemat daya. |
| **Konfigurasi Inti CPU** | Octa-Core (4x 2.4 GHz Kryo 265 Gold & 4x 1.9 GHz Kryo 265 Silver) | Menilai performa *multi-threading* OCR dan penalaran aturan. |
| **Grafis (GPU)** | Adreno 610 | Akselerasi pemrosesan grafis dan antarmuka Jetpack Compose. |
| **Memori Kerja (RAM)** | **6 GB LPDDR4X** (+ 2 GB Virtual Memory Extension) | Memastikan alokasi *heap memory* VelaPDF (< 50 MB) terisolasi aman. |
| **Penyimpanan Internal** | 128 GB UFS 2.2 | Kecepatan baca/tulis I/O SQLite Room dan cache berkas PDF lokal. |
| **Layar & Resolusi** | AMOLED 6,43 inci, $2400 \times 1080$ piksel (FHD+), 395 ppi | Pengujian tata letak antarmuka responsif dan pratinjau dokumen. |
| **Kamera Belakang** | 50 MP, f/1.8 (Sensor Utama PDAF) | Masukan citra dokumen fisik untuk Google ML Kit Document Scanner. |
| **Sistem Operasi (OS)** | **Android 13** (Tiramisu, API Level 33) / MIUI 14 | Kepatuhan izin runtime modern (`READ_MEDIA_IMAGES`). |

---

## 3. Profil Komputer Pengembang & Pembangun (Host Workstation / Build Environment)

Kompilasi kode program, verifikasi integritas digital SHA-256, dan audit arsitektural dilaksanakan pada stasiun kerja pengembang:

| Parameter Host Workstation | Spesifikasi Teknis |
| :--- | :--- |
| **Prosesor (CPU)** | 12th Gen Intel(R) Core(TM) i5-12500H (12 Physical Cores, 16 Threads, up to 4.50 GHz) |
| **Memori Utama (RAM)** | 16 GB DDR4/DDR5 Dual-Channel High Speed |
| **Sistem Operasi** | Microsoft Windows 11 Home Single Language (64-bit, OS Build 26300) |
| **Integrated Development Environment** | Android Studio Ladybug / Hedgehog Feature Drop (2024.x) |
| **Java Development Kit (JDK)** | OpenJDK 17.0.10 LTS (Target Java Bytecode Version 11) |
| **Android SDK Tools** | Android SDK Build-Tools 35.0.0, Platform API 37 (Compile), API 36 (Target), API 26 (Min) |
| **Build Automation** | Gradle 8.9 / Gradle Wrapper dengan Kotlin DSL (`build.gradle.kts`) |
| **Kotlin Compiler** | Kotlin Language Version 2.0.21 (Compose Compiler Extension terintegrasi) |

---

## 4. Matriks Pustaka Kunci & Versi Dependensi Terkunci (Software Stack)

Seluruh dependensi pihak ketiga dikunci secara deklaratif pada berkas `gradle/libs.versions.toml` dan `app/build.gradle.kts`:

| Nama Pustaka | Versi Terkunci | Peran Metodologis dalam Sistem |
| :--- | :---: | :--- |
| **Google ML Kit Document Scanner** | `16.0.0-beta1` | Pemindaian interaktif dokumen fisik via Google Play Services API. |
| **Google ML Kit Text Recognition** | `16.0.1` | Ekstraksi karakter teks optik (OCR) 100% lokal on-device. |
| **iText7 Core** | `7.2.5` | Engine komputasi manipulasi struktur berkas PDF secara lokal. |
| **Android Room Persistence** | `2.6.1` | Abstraksi ORM basis data relasional lokal SQLite (`tb_dokumen`, dll.). |
| **Dagger Hilt** | `2.51.1` | Dependency Injection untuk decoupling arsitektur Clean Architecture. |
| **Jetpack Compose BOM** | `2024.09.00` | Toolkit antarmuka deklaratif modern berbasis Material Design 3. |
| **DataStore Preferences** | `1.1.1` | Penyimpanan konfigurasi preferensi pengguna secara asinkron. |
| **Coil Compose** | `2.7.0` | Pemuatan dan *caching* citra dokumen efisien pada memori internal. |

---

## 5. Protokol Isolasi Lingkungan Pengujian (Testing Isolation Protocol)

Untuk menjaga akurasi pengukuran latensi dan konsumsi sumber daya saat eksperimen SK-01 s.d. SK-05:
1. **Mode Uji Terisolasi (Airplane Mode On):** Mode Pesawat diaktifkan sepanjang pengujian untuk membuktikan ketiadaan transmisi data internet (0 byte) dan menonaktifkan gangguan panggilan masuk/SMS.
2. **Penghentian Proses Latar Belakang (Kill Background Apps):** Seluruh aplikasi pihak ketiga yang berjalan di latar belakang dimatikan paksa (*Force Stop*) sebelum pencatatan waktu dimulai.
3. **Kecerahan Layar & Termal Tetap:** Tingkat kecerahan layar dikunci pada angka 50% tanpa fitur *Auto-Brightness*, dan suhu permukaan gawai dijaga dalam rentang operasional normal ($30^\circ\text{C} - 35^\circ\text{C}$) guna menghindari *CPU Thermal Throttling*.
4. **Pembersihan Cache Sebelum Pengujian:** Ruang *cache* aplikasi dibersihkan sebelum setiap putaran pengujian skenario agar pembacaan waktu I/O disk mencerminkan kondisi awal yang adil (*cold-start baseline*).

