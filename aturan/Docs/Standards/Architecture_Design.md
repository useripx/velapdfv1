# Software Architecture & Design Standard

> **STATUS:** STANDAR REKAYASA TEKNIS (PILLAR 2 SPECIFICATION)  
> **OTORITAS TERTINGGI:** Yogi Ario (PROJECT OWNER / LEAD DEVELOPER)  
> **TARGET PENGGUNA:** Seluruh Pengembang & AI Agent  
> **ACUAN UTAMA:** [Docs/Standards/Engineering_Standards.md](Engineering_Standards.md) & [Docs/universal-rule.md](../universal-rule.md)  
> **PRINSIP TATA KELOLA:** *No Plan, No Code. No Approval, No Code.*

---

## 1. Pendahuluan

Arsitektur perangkat lunak adalah pondasi struktural yang menentukan keandalan, skalabilitas, keamanan, dan kemudahan pemeliharaan sistem. Dokumen ini menetapkan standar perancangan arsitektur yang berlandaskan pada prinsip **Pragmatic Architecture**:
> **Pilihlah arsitektur berdasarkan kebutuhan nyata, kendala domain, dan skala sistem, BUKAN berdasarkan tren sesaat atau kebiasaan teknologi tertentu.**

AI Agent **MUST NOT** memutuskan pola arsitektur secara sepihak. Jika pola arsitektur belum dilampirkan atau belum disepakati, AI Agent **MUST** bertanya terlebih dahulu kepada Yogi Ario.

---

## 2. Prinsip Fundamental Arsitektur Perangkat Lunak

Setiap perancangan arsitektur dan desain komponen **MUST** mematuhi prinsip inti rekayasa berikut:

1. **Separation of Concerns (SoC):**  
   Setiap modul atau layer hanya bertanggung jawab atas satu aspek fungsional sistem:
   - *Presentation Layer (UI/Views/Controllers):* Menangani interaksi pengguna dan parsing input/request.
   - *Business Logic Layer (Services/Use Cases):* Mengeksekusi aturan bisnis inti tanpa ketergantungan pada UI atau framework transport.
   - *Data Access Layer (Repositories/Models):* Menangani komunikasi dan persistensi data (database, cache, file system).
2. **High Cohesion & Loose Coupling:**  
   Komponen dalam satu modul harus saling berkaitan erat (*high cohesion*), sedangkan ketergantungan antar modul yang berbeda harus seminimal mungkin (*loose coupling*) melalui abstraksi interface atau kontrak publik.
3. **Dependency Inversion & Boundaries:**  
   Modul tingkat tinggi (domain logic) **MUST NOT** bergantung pada modul tingkat rendah (detail teknis database/transport). Keduanya harus bergantung pada abstraksi (*interfaces/contracts*).
4. **Anti-Overengineering (YAGNI & KISS):**  
   Jangan membangun lapisan abstraksi atau pola microservices yang kompleks untuk aplikasi monolitik kecil. Rancanglah sistem sesederhana mungkin yang mampu menyelesaikan masalah secara tuntas.

---

## 3. Standar Keputusan Arsitektur (Architecture Decision Record - ADR)

Setiap keputusan arsitektur yang berdampak luas (seperti pemilihan framework, sistem database, pola manajemen state, mekanisme autentikasi, atau integrasi pihak ketiga) **MUST** dicatat dalam berkas [`Docs/Decision_Log.md`](../Decision_Log.md) menggunakan format **ADR**:

### Elemen Wajib ADR:
1. **ID Keputusan:** `[ADR-XXX]` berurutan (misal `[ADR-001]`).
2. **Judul:** Nama keputusan teknis yang jelas.
3. **Konteks & Masalah:** Latar belakang kendala atau kebutuhan teknis.
4. **Analisis Pilihan Solusi (Wajib Trade-Off):**  
   Menyajikan minimal 2 opsi solusi beserta perbandingan obyektif:
   - *Kelebihan (Pros)*
   - *Kekurangan (Cons)*
   - *Kompleksitas & Biaya Pemeliharaan*
5. **Rekomendasi Ahli:** Pilihan rekomendasi teknis terbaik yang diusulkan AI Agent kepada Yogi Ario beserta alasan logisnya.
6. **Status Keputusan:** `PROPOSED`, `APPROVED`, `REJECTED`, atau `SUPERSEDED`.
7. **Penyetuju Resmi:** Wajib disetujui secara eksplisit oleh **Yogi Ario** sebelum kode diimplementasikan.

---

## 4. Desain Komponen & Pemisahan Modul (Modular Boundaries)

Untuk proyek berskala menengah hingga besar, direktori kode sumber **MUST** memiliki batasan modular yang tegas:

```text
[Source Root]/
├── modules/
│   ├── auth/                      # Modul independen
│   │   ├── AGENTS.md              # Aturan teknis lokal Level 3 (Docs/AGENTS-module-template.md)
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── models/
│   │   └── tests/
│   └── billing/                   # Modul independen
│       ├── controllers/
│       ├── services/
│       └── models/
└── shared/                        # Komponen yang dapat dipakai lintas modul
    ├── database/
    ├── utils/
    └── types/
```

### Aturan Impor Antar-Modul:
- Modul `billing` **DILARANG KERAS** mengimpor berkas internal privat dari modul `auth` (seperti langsung mengakses tabel/model internal `auth`).
- Komunikasi antar-modul **MUST** melalui kontrak publik yang disepakati (*Public Interface, DTO, atau Event Bus*).

---

## 5. Aliran Data & Kontrak Antarmuka (Data Flow & API Contracts)

1. **Arah Aliran Data Searah (Unidirectional Data Flow):**  
   Aliran data harus dapat diprediksi:  
   $$\text{Request} \longrightarrow \text{Controller} \longrightarrow \text{Validator} \longrightarrow \text{Service} \longrightarrow \text{Repository} \longrightarrow \text{Database}$$
   Dilarang memanggil database secara langsung dari View atau Controller tanpa melalui layer Service/Repository.
2. **Kontrak API Terstandarisasi:**  
   Setiap interaksi antarsistem via HTTP/REST **MUST** mematuhi spesifikasi [`Database_API_Standards.md`](Database_API_Standards.md) dan tercatat pada template [`Docs/API_Contract.md`](../API_Contract.md).
3. **Immutability pada Data Transfer:**  
   Data yang dikirimkan antarlayer dianjurkan menggunakan struktur data yang tidak dapat dimutasi secara liar (*Data Transfer Objects / DTOs / Readonly Types*).

---

## 6. Manajemen Ketergantungan Eksternal (External Dependency Management)

1. **Audit Kebutuhan Sebelum Instalasi:**  
   AI Agent **MUST NOT** memasang library atau package baru hanya untuk fungsi sepele yang dapat diselesaikan dengan 10-20 baris kode native standar (*avoid dependency bloat*).
2. **Kriteria Pemilihan Library Pihak Ketiga:**
   - Reputasi dan keaktifan pemeliharaan di komunitas (*maintenance status*).
   - Lisensi perangkat lunak yang kompatibel (MIT, Apache 2.0, BSD; hindari lisensi viral seperti GPL pada proyek tertutup).
   - Ukuran bundel (*bundle size*) dan dampak performa.
   - Tidak memiliki riwayat kerentanan keamanan kritis (*CVE/vulnerabilities*).
3. **Persetujuan Yogi Ario:**  
   Penambahan dependensi baru pada manifest (`package.json`, `composer.json`, `requirements.txt`, dll.) **wajib diajukan dalam rencana kerja dan disetujui Yogi Ario**.
