# REKAPITULASI ASET & ARTEFAK RIIL PROYEK VELAPDF (METOPEN 2026/2027)

> **Mata Kuliah:** Metodologi Penelitian (Metopen) TA 2026/2027  
> **Mahasiswa:** Yogi Ario Pratama | NPM: 2313020004 | Kelas: 4B  
> **Program Studi:** S-1 Teknik Informatika, Universitas Nusantara PGRI Kediri  
> **Dosen Pembimbing Proposal:** Rony Heri Irawan, M.Kom.  
> **Judul Usulan Penelitian:** *Implementasi Rule-Based Inference pada Sistem VelaPDF*  
> **Ruang Lingkup Sistem:** Aplikasi Pemindai Dokumen Cerdas & Perangkum PDF Otomatis Luring (*Smart Document Scanner & PDF Summarizer*) Berbasis AI Simbolik (Ontologi JSON, Forward Chaining, dan NLP Bahasa Indonesia Ringan).  
> **Status:** 100% Lengkap & Tervalidasi Sesuai Panduan `kebutuhan_projek_velapdf.md` dan `PENUGASAN.pdf`.

---

## 1. Ringkasan Eksekutif & Struktur Direktori Aset

Seluruh aset, kode sumber inti, skema basis pengetahuan ontologi, sampel berkas uji PDF, spesifikasi lingkungan pengujian terkunci (*testbed*), dan dokumen pemetaan penugasan akademik telah disiapkan dan disusun secara sistematis pada folder `Docs/metopen/`:

```
Docs/metopen/
├── README.md                                  # Ringkasan Eksekutif & Panduan Integrasi Aset Metopen
├── 01_kode_sumber/                            # Prioritas 1: Aset Kode Sumber Inti (Kotlin Android)
│   ├── DocumentAnalysisModels.kt              # Data Models, Entitas Fakta SPO, Ontologi, Aturan & Ringkasan
│   ├── MlKitOcrHelper.kt                      # Modul OCR Lokal On-Device (Google ML Kit Text Recognition)
│   ├── NlpTextPreprocessor.kt                 # Prapemrosesan NLP Bahasa Indonesia (Tokenisasi & Stopwords)
│   ├── OntologyLoader.kt                      # Modul Parser & Loader Ontologi Semantik (document_ontology.json)
│   ├── RuleInferenceEngine.kt                 # Mesin Inferensi Rule-Based Deduktif (Forward Chaining)
│   ├── FactBasedSummarizer.kt                 # Modul Perangkum Ekstraktif Berbasis Fakta & Relevansi Ontologi
│   └── DocumentAnalysisViewModel.kt           # Pengendali Alur Layar (Message-to-Method Invariance)
├── 02_skema_basis_pengetahuan/                # Prioritas 2: Skema Ontologi, Aturan, & PDM SQL
│   ├── document_ontology.json                 # Taksonomi Dokumen Resmi (SK, SE, SOP, Peraturan, Laporan)
│   ├── production_rules.json                  # Basis Aturan Produksi Formal (RULE_01 s.d. RULE_08)
│   └── pdm_schema.sql                         # Skema Data Fisik (PDM) 3 Tabel Relasional SQLite/Room
├── 03_antarmuka_dan_wireframe/                # Prioritas 3: Desain UI, Wireframe, & Skenario Use Case
│   ├── wireframe_and_ui_flow.md               # Mockup Layar, Alur Antarmuka, Dekomposisi BCE
│   └── use_case_scenario.md                   # Skenario Use Case Formal Rinci (UC-01: Analisis & Rangkum Dokumen)
├── 04_sampel_dokumen_uji/                     # Prioritas 4: Sampel Dokumen Uji Empiris (SK-01 s.d SK-05)
│   ├── SK-01_Surat_Keputusan_Rektor.pdf       # Dokumen Uji 1: SK Penetapan Kalender Akademik
│   ├── SK-02_Surat_Edaran_Jadwal_KRS.pdf      # Dokumen Uji 2: SE Jadwal Pengisian KRS (Tenggat Kritis)
│   ├── SK-03_Pedoman_SOP_Ujian_Skripsi.pdf    # Dokumen Uji 3: SOP Sidang Skripsi (Alur & Syarat SKS)
│   ├── SK-04_Peraturan_Tata_Tertib_Kampus.pdf # Dokumen Uji 4: Regulasi Tata Tertib (Larangan & Sanksi)
│   ├── SK-05_Laporan_Ringkasan_Kegiatan.pdf   # Dokumen Uji 5: Laporan Eksekutif Capaian KKN Tematik
│   ├── dataset_catalog.md                     # Katalog Karakterisasi Dokumen Uji & Hash Otentik
│   └── ground_truth_facts.json                # Ground Truth Fakta SPO, Target Aturan & Ringkasan
├── 05_spesifikasi_testbed/                    # Prioritas 5: Profil Spesifikasi Lingkungan Uji
│   ├── testbed_hardware_software.md           # Penguncian Parameter Hardware, Software Stack & Protokol Uji
│   └── checksum_sha256.txt                    # Daftar Nilai Hash SHA-256 Otentik Seluruh Aset
└── 06_pemetaan_tugas_akademik/                # Jembatan Integrasi ke Penugasan Metopen
    ├── pemetaan_ke_tugas_3.md                 # Cetak Biru Naskah Tugas 3 (UML, BCE, Invariance, PDM)
    ├── pemetaan_ke_laporan_2_bab3.md          # Draf Lengkap Bab III Metodologi (SDLC Prototyping)
    └── pemetaan_ke_tugas_4.md                 # Perumusan Formula & 2 Worked Examples (Black-Box & SUS)
```

---

## 2. Matriks Pemenuhan Kebutuhan Aset (`kebutuhan_projek_velapdf.md`)

| Prioritas Kebutuhan | Item yang Diminta | Berkas Hasil yang Disiapkan | Status Pemenuhan |
| :---: | :--- | :--- | :---: |
| **Prioritas 1** | **Aset Kode Sumber Inti**<br>• Ekstraksi OCR Lokal<br>• Prapemrosesan NLP<br>• Parser Ontologi Semantik<br>• Mesin Inferensi Aturan<br>• Perangkum Ekstraktif<br>• ViewModel Pengendali Layar | • [`MlKitOcrHelper.kt`](01_kode_sumber/MlKitOcrHelper.kt)<br>• [`NlpTextPreprocessor.kt`](01_kode_sumber/NlpTextPreprocessor.kt)<br>• [`OntologyLoader.kt`](01_kode_sumber/OntologyLoader.kt)<br>• [`RuleInferenceEngine.kt`](01_kode_sumber/RuleInferenceEngine.kt)<br>• [`FactBasedSummarizer.kt`](01_kode_sumber/FactBasedSummarizer.kt)<br>• [`DocumentAnalysisModels.kt`](01_kode_sumber/DocumentAnalysisModels.kt)<br>• [`DocumentAnalysisViewModel.kt`](01_kode_sumber/DocumentAnalysisViewModel.kt) | **100% LENGKAP**<br>*(Memenuhi Asas Message-to-Method Invariance)* |
| **Prioritas 2** | **Skema Basis Pengetahuan**<br>• Ontologi JSON Dokumen Resmi<br>• Aturan Produksi IF-THEN<br>• Skema Fisik Relasional SQLite | • [`document_ontology.json`](02_skema_basis_pengetahuan/document_ontology.json)<br>• [`production_rules.json`](02_skema_basis_pengetahuan/production_rules.json)<br>• [`pdm_schema.sql`](02_skema_basis_pengetahuan/pdm_schema.sql) | **100% LENGKAP**<br>*(SQLite/Room PDM 3 Tabel Fisik Terintegrasi)* |
| **Prioritas 3** | **Antarmuka & Wireframe**<br>• Sketsa Alur Layar ASCII<br>• Dekomposisi Pola BCE<br>• Skenario Use Case Rinci (UC-01) | • [`wireframe_and_ui_flow.md`](03_antarmuka_dan_wireframe/wireframe_and_ui_flow.md)<br>• [`use_case_scenario.md`](03_antarmuka_dan_wireframe/use_case_scenario.md) | **100% LENGKAP**<br>*(Siap Digunakan untuk T31 & T32)* |
| **Prioritas 4** | **Sampel Dokumen Uji**<br>• 5 Dokumen PDF Riil Formal<br>• Karakterisasi Dataset<br>• Ground Truth Evaluasi | • 5 Berkas PDF Fisik ([`SK-01` s.d. `SK-05`](04_sampel_dokumen_uji/))<br>• [`dataset_catalog.md`](04_sampel_dokumen_uji/dataset_catalog.md)<br>• [`ground_truth_facts.json`](04_sampel_dokumen_uji/ground_truth_facts.json) | **100% LENGKAP**<br>*(Tersedia 5 Kasus SK-01 s.d. SK-05)* |
| **Prioritas 5** | **Profil Lingkungan Uji**<br>• Hardware Target Mobile (Xiaomi Redmi Note 11)<br>• Host Workstation Build<br>• Verifikasi Hash SHA-256 Otentik | • [`testbed_hardware_software.md`](05_spesifikasi_testbed/testbed_hardware_software.md)<br>• [`checksum_sha256.txt`](05_spesifikasi_testbed/checksum_sha256.txt) | **100% LENGKAP**<br>*(Terkunci untuk Testbed Invariance & Tugas 5)* |

---

## 3. Manfaat Operasional untuk Tugas Metopen Mendatang

1. **Untuk Tugas 3 (Target: 16 Nov 2026):**
   * Berkas `06_pemetaan_tugas_akademik/pemetaan_ke_tugas_3.md` langsung menyajikan Use Case Diagram, Sequence Diagram BCE, Class Diagram, Tabel *Message-to-Method Invariance*, dan PDM 3 tabel relasional.
   * Berkas `03_antarmuka_dan_wireframe/wireframe_and_ui_flow.md` dan `use_case_scenario.md` menjadi bahan otentik penyusunan berkas bukti proses mandiri **T31** dan laporan perancangan **T32**.

2. **Untuk Laporan 2 / Bab III (Target: 23 Nov 2026):**
   * Berkas `06_pemetaan_tugas_akademik/pemetaan_ke_laporan_2_bab3.md` telah memuat draf utuh 8 subbab resmi jalur R&D (Metode R&D, Arsitektur 4 Lapisan, SRS Fungsional/Non-Fungsional, SDLC Prototyping, Elisitasi Empiris, Pemodelan UML, PDM SQLite, dan Kriteria Keberhasilan Black-Box 100% & SUS $\ge 68$).

3. **Untuk Tugas 4 (Target: 30 Nov 2026):**
   * Berkas `06_pemetaan_tugas_akademik/pemetaan_ke_tugas_4.md` menyajikan formula resmi berindeks (2.1 s.d. 2.5) beserta 2 *worked examples (toy walkthrough)* langkah demi langkah untuk pengujian Black-Box Testing modular ($P_{valid} = 100{,}0\%$) pada dokumen perangkum dan skor SUS 5 responden ($\overline{\text{SUS}} = 78{,}50$, predikat *Acceptable / Good*).

4. **Untuk Tugas 5 (Target: Des 2026):**
   * Pengujian komputasi nyata dapat diulang secara deterministik menggunakan skenario `SK-01` s.d. `SK-05` di atas lingkungan terkunci `testbed_hardware_software.md` dengan jaminan integritas berkas via `checksum_sha256.txt`.
