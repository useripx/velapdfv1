# KATALOG SAMPEL DOKUMEN UJI & KARAKTERISASI DATASET (SK-01 S.D. SK-05)

> **Mata Kuliah:** Metodologi Penelitian (Metopen) TA 2026/2027  
> **Mahasiswa:** Yogi Ario Pratama | NPM: 2313020004 | Kelas: 4B  
> **Program Studi:** S-1 Teknik Informatika, Universitas Nusantara PGRI Kediri  
> **Dosen Pembimbing Proposal:** Rony Heri Irawan, M.Kom.  
> **Judul Usulan Penelitian:** *Implementasi Rule-Based Inference pada Sistem VelaPDF*  
> **Fokus Dokumen:** Karakterisasi data empiris dokumen resmi untuk pengujian sistem perangkum offline, pemenuhan berkas T32, Laporan 2 (Bab III), dan Tugas 5.

---

## 1. Ikhtisar Dataset Uji Empiris

Dataset pengujian terdiri atas 5 (lima) berkas dokumen digital resmi berformat PDF riil yang merepresentasikan dokumen formal kelembagaan, regulasi, dan prosedur operasional kampus (Universitas Nusantara PGRI Kediri). Dokumen-dokumen ini dirancang untuk menguji ketahanan modul OCR lokal, ketepatan prapemrosesan NLP Bahasa Indonesia, kelengkapan ekstraksi fakta ontologi (SPO), penembakan aturan klasifikasi (*rule firing*), dan kualitas intisari ringkasan dokumen secara luring.

Seluruh berkas fisik disimpan secara permanen pada folder:
`Docs/metopen/04_sampel_dokumen_uji/`

---

## 2. Tabel Matriks Karakterisasi & Otentisitas Berkas (SHA-256 Checksum)

Setiap berkas dokumen uji dijamin integritas digitalnya melalui penanda sidik jari 64-karakter heksadesimal SHA-256 otentik berikut:

| Kode Skenario | Nama Berkas Dokumen | Ukuran Berkas | Jumlah Halaman | Checksum SHA-256 Otentik | Kategori & Karakteristik Dokumen |
| :---: | :---: | :---: | :---: | :--- | :--- |
| **SK-01** | `SK-01_Surat_Keputusan_Rektor.pdf` | 5.136 Byte | 1 Halaman | `04358C9AF749AE583C9EBB073CD6EA2D50ED30651EBF0B8FD1DC8A512F502ED6` | **Surat Keputusan (SK) Rektor:** Memuat konsiderans (Menimbang, Mengingat) dan diktum mengikat KESATU s.d. KEEMPAT; Menguji RULE_01 & RULE_07. |
| **SK-02** | `SK-02_Surat_Edaran_Jadwal_KRS.pdf` | 4.586 Byte | 1 Halaman | `4D9CC3797A44483AD1BEE2849CFE2D5AB0FA29E4634BF61B0B0E35276BC9CF7B` | **Surat Edaran (SE) Resmi:** Memuat perihal edaran, sasaran civitas, batas waktu registrasi, dan peringatan tanpa susulan; Menguji RULE_02 & RULE_05. |
| **SK-03** | `SK-03_Pedoman_SOP_Ujian_Skripsi.pdf` | 4.926 Byte | 1 Halaman | `3420246403728BE830B9C422D279538BEFFFD5F9A126515011DDC1799188ED63` | **Pedoman Prosedur Baku (SOP):** Memuat alur pendaftaran, syarat administrasi SKS/IPK, dan batas revisi naskah; Menguji RULE_03 & ekstraksi alur kerja. |
| **SK-04** | `SK-04_Peraturan_Tata_Tertib_Kampus.pdf` | 4.026 Byte | 1 Halaman | `EC4A452958D630C745246072FFAC8E8F8EDC21068DA154F8D296A429676F76F2` | **Peraturan & Tata Tertib:** Memuat pasal normatif hak mahasiswa, kewajiban etika, larangan plagiasi, dan sanksi skorsing; Menguji RULE_04 & RULE_06. |
| **SK-05** | `SK-05_Laporan_Ringkasan_Kegiatan.pdf` | 4.243 Byte | 1 Halaman | `E9DCED9CFFE4AF4B6A8755692F17D5A5816B39655CE38828C3920754E95928F0` | **Laporan Resmi Pelaksanaan:** Memuat ringkasan capaian kuantitatif program pengabdian (15 desa, 450 mahasiswa) dan rekomendasi tindak lanjut. |

---

## 3. Rincian Skenario Pengujian (SK-01 s.d. SK-05)

### Skenario 1 (SK-01): Kasus Surat Keputusan (SK) Penetapan Kalender Akademik
* **Tujuan Pengujian:** Menguji deteksi pola formal hukum konsiderans ("Menimbang", "Mengingat") dan diktum ("MEMUTUSKAN", "Menetapkan").
* **Target Aturan:** `RULE_01` (Klasifikasi SK) dan `RULE_07` (Verifikasi Kelengkapan Unsur Legalitas).
* **Luaran Harapan:** Dokumen terklasifikasi sebagai "Surat Keputusan (SK)", nomor `112/SK/UNP-KD/X/2026` terekstraksi, dan tanggal perkuliahan terdeteksi sebagai intisari utama.

### Skenario 2 (SK-02): Kasus Surat Edaran (SE) dengan Klausul Tenggat Waktu Ketat
* **Tujuan Pengujian:** Menguji kemampuan ekstraksi NLP dan inferensi dalam mendeteksi kombinasi kewajiban bimbingan DPA dan batas waktu (*deadline*) pengisian KRS.
* **Target Aturan:** `RULE_02` (Klasifikasi SE) dan `RULE_05` (Deteksi Tenggat Waktu Kritis).
* **Luaran Harapan:** Teridentifikasi status "Surat Edaran", klausul peringatan kritis terangkat: "Batas Waktu: 25 Januari 2026 pukul 23.59 WIB".

### Skenario 3 (SK-03): Kasus Pedoman Standar Operasional Prosedur (SOP)
* **Tujuan Pengujian:** Menguji perangkuman berbasis tahapan kerja prosedural dan prasyarat administrasi.
* **Target Aturan:** `RULE_03` (Klasifikasi Pedoman SOP).
* **Luaran Harapan:** Teridentifikasi alur 4 tahap pelaksanaan sidang dan kewajiban revisi 14 hari kerja kalender.

### Skenario 4 (SK-04): Kasus Peraturan Normatif, Hak, dan Larangan Disiplin
* **Tujuan Pengujian:** Menguji isolasi pasal kewajiban etika dan klausul larangan keras.
* **Target Aturan:** `RULE_04` (Klasifikasi Peraturan) dan `RULE_06` (Deteksi Klausul Larangan Khusus).
* **Luaran Harapan:** Ringkasan memuat peringatan tegas: larangan plagiarisme dan perjudian serta ancaman sanksi skorsing.

### Skenario 5 (SK-05): Kasus Laporan Ringkasan Capaian Kegiatan
* **Tujuan Pengujian:** Menguji pemadatan teks laporan kuantitatif dan rekomendasi tindak lanjut.
* **Target Aturan:** Peringkasan berbasis frekuensi kata kunci NLP dan entitas capaian (15 desa, 450 mahasiswa).
* **Luaran Harapan:** Rasio pemadatan teks mencapai $\ge 70\%$ dengan mempertahankan seluruh angka capaian kunci.

---

## 4. Perluasan Sampel Dokumen Uji Tahap 02 (12 Berkas Tambahan, Total Corpus = 17 PDF)

Untuk menguji ketahanan pipeline 4-JSON dan OCR lokal pada dokumen akademik nyata berbahasa Indonesia di luar 5 skenario formal baseline (`SK-01` s.d. `SK-05`), corpus uji diperluas dengan **12 berkas PDF nyata** dari ruang kerja proyek (`aturan/alatuji/04_sampel_dokumen_uji/`):

| Kelompok Uji | Nama Berkas Dokumen | Ukuran (Byte) | Jumlah Halaman | Checksum SHA-256 Otentik | Peran Pengujian Empiris |
| :--- | :--- | :---: | :---: | :--- | :--- |
| **Jurnal Ilmiah 12** | `12 Penerapan Model Ontologi dalam Perkembangan Game Digital.pdf` | 553.433 | 10 Halaman | `CA12AB99A84EE4A54893C9B9B81F20A0F44AB365667124E131B54CAA9283846B` | Menguji klasifikasi `Artikel / Jurnal Ilmiah` (`RULE_14`) + ekstraksi definisi/metode/keunggulan (`RULE_10`, `RULE_11`, `RULE_16`). |
| **Jurnal Ilmiah 13** | `13 Konsep Ontologi pada Manajemen Rekayasa Perangkat Lunak Kajian Tentang Perkembangan dan Tantangannya.pdf` | 329.527 | 9 Halaman | `C7419DE4FE14D5DF3F4370D477B583793EE573AAA01CE8877B12964072610D1E` | Menguji konsistensi klasifikasi jurnal ilmiah rekayasa perangkat lunak & penembakan `RULE_14`, `RULE_10`, `RULE_16`. |
| **Jurnal Ilmiah 14** | `14 Pengklasifikasian Dokumen Teks Bahasa Indonesia berbasis Vektor Space Model dengan menggunakan Metode k-Nearest Neighbor (k-NN) dan Euclidean Distance.pdf` | 294.361 | 8 Halaman | `6E3A3C3B3231425834F776815FB0F7E23B96435C362F1CB30C94B2B83992927C` | Menguji jurnal NLP/VSM/k-NN berbahasa Indonesia & ekstraksi 67 fakta SPO tanpa kehabisan memori. |
| **Dokumen Panjang (`Roadmap`)** | `Roadmap.pdf` | 553.505 | 19 Halaman | `988E8DA1A9B9BF5DABE156380631BFD4E7EB478EAF57B930D20353F0769B3E8E` | Uji ketahanan render `PdfRenderer` + ML Kit OCR multi-halaman (`19` hal.) & stabilitas Java Heap. |
| **Dokumen Referensi (`next`)** | `next.pdf` | 32.167 | 4 Halaman | `46E765929D0EA132C6DAEFCC585CB5D6B99AF67D042E921AC25EBB6F2F72E5FF` | Menguji ekstraksi dokumen referensi teknis multi-halaman ukuran sedang (`4` hal.). |
| **Stress Test Tabular (`KHS1..5`)** | `KHS1.pdf` .. `KHS5.pdf` | 532.373 – 532.584 | 1 Halaman/berkas | `0602DB...` (`KHS1`), `25A55A...` (`KHS2`), `52C054...` (`KHS3`), `702D8F...` (`KHS4`), `1ECD15...` (`KHS5`) | Uji stabilitas bebas-*crash* saat menerima dokumen non-domain (KHS lama telah dihapus permanen per `ADR-001` & `batasan_masalah.md`). |
| **Stress Test Tabular (`Transkip1..2`)** | `Transkip1.pdf`, `Transkip2.pdf` | 1.265.835 & 1.005.933 | 2 Halaman/berkas | `00146513A177317ED45285410D91DA2422D27B68C47880DA1DF74C3F6FD3DF69` (`Transkip1`), `F988926071C76266918D485A5D9B4845E1136D8C6F2F415284B4D81EC8825CB1` (`Transkip2`) | Uji stabilitas OCR bitmap beresolusi besar (tabel nilai transkrip 2 halaman $> 1\text{ MB}$). |

*(Catatan Invariansi Metopen: Kelima berkas baseline `SK-01` s.d. `SK-05` beserta hash pada `checksum_sha256.txt` dibiarkan **tetap utuh 100%** sebagai kontrol utama proposal Metopen.)*
