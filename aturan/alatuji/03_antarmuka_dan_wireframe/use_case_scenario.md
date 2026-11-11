# TABEL SKENARIO USE CASE RINCI (USE CASE SCENARIO)

> **Mata Kuliah:** Metodologi Penelitian (Metopen) TA 2026/2027  
> **Mahasiswa:** Yogi Ario Pratama | NPM: 2313020004 | Kelas: 4B  
> **Program Studi:** S-1 Teknik Informatika, Universitas Nusantara PGRI Kediri  
> **Dosen Pembimbing Proposal:** Rony Heri Irawan, M.Kom.  
> **Judul Usulan Penelitian:** *Implementasi Rule-Based Inference pada Sistem VelaPDF*  
> **Fokus Dokumen:** Dasar penyusunan berkas T32 (Pemodelan UML OOAD) dan Bab III Laporan 2.

---

### Tabel Skenario Use Case: Modul Analisis dan Peringkasan Dokumen PDF Cerdas

| Komponen Skenario | Rincian Spesifikasi |
| :--- | :--- |
| **Identitas Use Case** | `UC-01` |
| **Nama Use Case** | **Menganalisis dan Merangkum Dokumen PDF Cerdas** |
| **Aktor Utama** | Pengguna Mandiri (*Single Standalone End-User* / Civitas Akademika) |
| **Deskripsi Singkat** | Pengguna memotret dokumen fisik atau mengunggah berkas PDF dari memori gawai, sistem mengekstrak karakter teks secara luring via Google ML Kit, melakukan prapemrosesan NLP (pemecahan kalimat & stopword removal), memetakan konsep ke ontologi semantik JSON (fakta SPO), mengeksekusi penalaran aturan *Forward Chaining*, serta menyajikan intisari ringkasan dokumen dan klausul penting secara terstruktur. |
| **Kondisi Awal (*Pre-condition*)** | 1. Aplikasi VelaPDF telah terpasang dan aktif pada smartphone Android.<br>2. Pengguna telah memberikan izin akses penyimpanan internal (*READ_MEDIA_IMAGES* / *READ_EXTERNAL_STORAGE*) atau izin kamera.<br>3. Berkas ontologi (`document_ontology.json`) dan berkas aturan (`production_rules.json`) tersedia pada direktori lokal aset aplikasi. |
| **Kondisi Akhir (*Post-condition*)** | 1. Teks dokumen diekstraksi dan disegmentasi ke himpunan kalimat NLP.<br>2. Fakta semantik SPO dipetakan ke konsep ontologi dan dimuat ke *Working Memory*.<br>3. Seluruh aturan produksi yang relevan telah dievaluasi dan ditembakkan (*fired*).<br>4. Ringkasan intisari dokumen (topik, poin penting, kewajiban, tenggat waktu) disajikan kepada pengguna.<br>5. Metadata, fakta, dan ringkasan tersimpan secara persisten pada SQLite Room (`tb_dokumen`, `tb_fakta_semantik`, `tb_ringkasan_inferensi`). |

---

### Alur Kerja Normal (Main Scenario / Normal Flow)

| No. Langkah | Aksi Aktor (Pengguna) | Respons Sistem (VelaPDF) |
| :---: | :--- | :--- |
| **1** | Pengguna menekan kartu menu **"Analisis & Ringkas Dokumen AI"** pada `DashboardScreen`. | Sistem menampilkan opsi sumber dokumen: *Pindai Kamera* atau *Pilih Berkas PDF*. |
| **2** | Pengguna memilih berkas PDF atau memindai dokumen fisik dari memori perangkat. | Sistem membaca berkas, membangkitkan nilai *checksum* SHA-256 berkas, dan menginisialisasi status pemrosesan. |
| **3** | Pengguna menekan tombol konfirmasi pemilihan berkas. | Objek `DocumentAnalysisViewModel` memanggil `MlKitOcrHelper.extractFromPdfUri()` atau `extractFromImageUri()`. |
| **4** | — | Mesin Google ML Kit on-device mengekstrak karakter teks per halaman secara luring dan mengirimkan `OcrRawResult` ke ViewModel. |
| **5** | — | ViewModel menginstruksikan `NlpTextPreprocessor.processDocument()` untuk membersihkan teks, memecah kalimat, menyaring *stopwords* bahasa Indonesia, dan mendeteksi seksi dokumen. |
| **6** | — | Sistem menampilkan layar `OcrPreviewScreen` yang memuat pratinjau teks mentah dan segmen kalimat terstruktur. |
| **7** | Pengguna meninjau kalimat dan menekan tombol **"Jalankan Peringkasan AI"**. | ViewModel memanggil `OntologyLoader.loadOntologyFromAsset()` dan mengekstrak atom fakta semantik SPO (`extractSemanticFacts`). |
| **8** | — | ViewModel memanggil `RuleInferenceEngine.loadRulesFromAsset()` dan mengeksekusi metode `executeForwardChaining()`. |
| **9** | — | `RuleInferenceEngine` mengevaluasi premis aturan produksi, menembakkan aturan yang cocok, menyimpulkan jenis dokumen, dan mencatat jejak penalaran (*InferenceTrace*). |
| **10** | — | ViewModel menginstruksikan `FactBasedSummarizer.generateDocumentSummary()` untuk memilih kalimat berbobot tertinggi dan menyusun laporan ringkasan intisari. |
| **11** | — | Sistem mengarahkan navigasi ke `DocumentSummaryScreen` dan menampilkan ringkasan terstruktur beserta rasio pemadatan teks. |
| **12** | Pengguna meninjau hasil ringkasan dan menekan tombol **"Simpan ke Riwayat"**. | Sistem menyimpan data master dokumen, fakta semantik, dan luaran ringkasan secara persisten ke basis data SQLite Room. |

---

### Alur Alternatif & Penanganan Eksepsi (Alternative Flows)

* **Alur Alternatif 4a: Kualitas Citra Buram / Dokumen Kosong (OCR Failure)**
  1. Jika `OcrRawResult.isSuccess == false` atau panjang karakter teks $= 0$, sistem membatalkan alur normal.
  2. Sistem menampilkan kotak dialog: *"Dokumen tidak dapat terbaca dengan jelas. Pastikan pencahayaan cukup dan orientasi dokumen tegak (portrait)."*
  3. Sistem memberikan opsi `[Pindai Ulang]` yang mengembalikan pengguna ke Langkah 1 tanpa menyimpan rekaman sampah ke basis data.

* **Alur Alternatif 6a: Koreksi Teks Manual oleh Pengguna (*Manual Correction*)**
  1. Pada `OcrPreviewScreen`, pengguna menemukan kesalahan pengenalan karakter (misal karakter "O" terbaca angka "0" pada nomor surat).
  2. Pengguna menyunting teks pada *OutlinedTextField*.
  3. Sistem memperbarui data kalimat sebelum dialirkan ke tahap ekstraksi fakta ontologi.

* **Alur Alternatif 9a: Dokumen Tanpa Kesesuaian Aturan Khusus (*Default Classification*)**
  1. Jika tidak ada aturan spesifik yang ditembakkan oleh mesin inferensi, sistem menerapkan aturan dasar (*default rule*).
  2. Sistem menandai jenis dokumen sebagai `Dokumen Umum / Surat Dinas` dan mengekstrak poin intisari berdasarkan bobot frekuensi kata kunci NLP murni.

* **Alur Alternatif 9b: Deteksi Konflik Aturan Produksi (*Conflict Resolution*)**
  1. Jika terdapat dua aturan yang premisnya terpenuhi secara simultan, `RuleInferenceEngine` mengeksekusi aturan dengan nilai atribut `priority` tertinggi terlebih dahulu (*Priority Strategy*).
  2. Seluruh urutan penembakan dicatat secara transparan pada jejak inferensi (*InferenceTrace*).

---

### Kebutuhan Khusus Non-Fungsional (Special Requirements)
1. **Lokus Operasional 100% Luring (*Offline-First*):** Seluruh proses pada Langkah 4, 5, 7, 8, 9, 10, dan 12 wajib berjalan tanpa internet (0-byte data consumption).
2. **Batas Latensi Komputasi (*Response Time*):** Total durasi komputasi dari Langkah 7 hingga Langkah 11 tidak boleh melampaui ambang batas $1.500 \text{ ms}$ pada gawai uji standar kelas menengah (*mid-range testbed*).
3. **Efisiensi Alokasi Memori (*Heap Memory Footprint*):** Konsumsi memori kerja (*RAM usage*) selama proses peringkasan aktif tidak boleh melebihi $50 \text{ MB}$.
