# DOKUMEN ARTIFAK AKADEMIK: PEMETAAN LENGKAP KE TUGAS 4 (FORMULASI & WORKED EXAMPLES)

> **Mata Kuliah:** Metodologi Penelitian (Metopen) TA 2026/2027  
> **Mahasiswa:** Yogi Ario Pratama | NPM: 2313020004 | Kelas: 4B  
> **Program Studi:** S-1 Teknik Informatika, Universitas Nusantara PGRI Kediri  
> **Dosen Pembimbing Proposal:** Rony Heri Irawan, M.Kom.  
> **Judul Usulan Penelitian:** *Implementasi Rule-Based Inference pada Sistem VelaPDF*  
> **Fokus Dokumen:** Pemenuhan Instrumen Formulasi Matematis Metodologi dan Simulasi Langkah demi Langkah (*Worked Examples / Toy Walkthrough*) untuk Uji Black-Box Testing dan Evaluasi System Usability Scale (SUS).

---

## 1. Landasan Formulasi Matematis & Evaluasi Sistem

Berdasarkan *Universal Rule* dan Panduan Penugasan Metopen, instrumen evaluasi sistem rekayasa perangkat lunak wajib menyertakan perumusan matematis formal bersistem penomoran resmi `(2.X)` yang merepresentasikan parameter uji kuantitatif:

### 1.1 Formulasi Pengujian Kelayakan Fungsional (Black-Box Testing)
Tingkat keberhasilan eksekusi fungsional sistem dihitung menggunakan rasio persentase kelulusan kasus uji (*test case passing rate*):

$$\tag{2.1} P_{valid} = \left( \frac{m}{n} \right) \times 100\%$$

*Keterangan Variabel Formulasi (2.1):*
* $P_{valid}$ : Persentase kevalidan fungsional sistem perangkat lunak (Target: $100{,}0\%$).
* $m$ : Jumlah skenario kasus uji yang menghasilkan luaran aktual sesuai dengan luaran harapan (*actual output == expected output*).
* $n$ : Total seluruh skenario kasus uji yang dieksekusi pada lingkungan pengujian terkunci (*testbed*).

---

### 1.2 Formulasi Evaluasi Keterpakaian Sistem (System Usability Scale / SUS)
Penilaian keterpakaian pengguna mengacu pada instrumen baku *System Usability Scale* (Brooke, 1996) yang terdiri atas 10 butir pertanyaan skala Likert 1–5:

1. **Perhitungan Skor Kontribusi Butir Positif (Nomor Ganjil: 1, 3, 5, 7, 9):**
   $$\tag{2.2} S_{k,i} = X_{k,i} - 1 \quad \text{untuk } i \in \{1, 3, 5, 7, 9\}$$

2. **Perhitungan Skor Kontribusi Butir Negatif (Nomor Genap: 2, 4, 6, 8, 10):**
   $$\tag{2.3} S_{k,i} = 5 - X_{k,i} \quad \text{untuk } i \in \{2, 4, 6, 8, 10\}$$

3. **Perhitungan Skor Total Terkonversi Responden ke-$k$:**
   Total nilai kontribusi dari ke-10 butir pernyataan dijumlahkan, kemudian dikalikan dengan faktor pengali koreksi skala 2,5 agar menghasilkan rentang skor standar 0 hingga 100:
   $$\tag{2.4} \text{Skor}_k = \left( \sum_{i=1}^{10} S_{k,i} \right) \times 2{,}5$$

4. **Perhitungan Rata-Rata Skor SUS Kelompok Pengguna:**
   Rata-rata keseluruhan skor keterpakaian dari sejumlah $K$ responden dihitung menggunakan rata-rata aritmatika:
   $$\tag{2.5} \overline{\text{SUS}} = \frac{1}{K} \sum_{k=1}^K \text{Skor}_k$$

*Kriteria Interpretasi Ilmiah:* Berdasarkan tolok ukur empiris Bangor, Kortum, dan Miller (2008), skor rata-rata $\overline{\text{SUS}} \ge 68$ menetapkan bahwa sistem berada pada kuadran *Acceptable* (Dapat Diterima), *Grade Scale B/C*, dan *Adjective Rating: Good*.

---

## 2. Simulasi Perhitungan Langkah demi Langkah (Worked Examples / Toy Walkthrough)

### 2.1 Kasus 1: Toy Walkthrough Pengujian Black-Box Modular (Modul Transaksi Utama)
Simulasi pengujian fungsional dilakukan pada Modul Analisis dan Peringkasan Dokumen Cerdas (`UC-01`) dengan merancang 6 (enam) skenario kasus uji yang mencakup uji normal (*happy path*), analisis nilai batas (*Boundary Value Analysis* / BVA), dan masukan data ilegal:

| Kode Uji | Skenario Pengujian | Masukan Kasus Uji (*Test Input*) | Luaran Harapan (*Expected Output*) | Luaran Aktual (*Actual Output*) | Status Kelulusan |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | Uji Alur Normal Dokumen Surat Keputusan (SK) | Berkas PDF `SK-01_Surat_Keputusan_Rektor.pdf` (Nomor: `112/SK/UNP-KD/X/2026`) | Terekstraksi konsiderans & diktum, penembakan `RULE_01` (Klasifikasi SK) & `RULE_07` (Legalitas Formal), tanggal mulai perkuliahan terekstraksi. | `RULE_01` & `RULE_07` dieksekusi, ringkasan eksekutif dan tanggal mulai kuliah tampil. | **VALID** |
| **TC-02** | Uji Deteksi Tenggat Waktu Kritis (*Actionable Item*) | Berkas PDF `SK-02_Surat_Edaran_Jadwal_KRS.pdf` (Tenggat 25 Jan 2026) | Penembakan `RULE_02` (Klasifikasi SE) & `RULE_05` (Tenggat Waktu Kritis), peringatan tanpa susulan terangkat ke ringkasan. | Peringatan tenggat 25 Jan 2026 23.59 WIB tampil dengan prioritas tinggi. | **VALID** |
| **TC-03** | Uji Ekstraksi Alur Prosedur Baku (*SOP Walkthrough*) | Berkas PDF `SK-03_Pedoman_SOP_Ujian_Skripsi.pdf` (Syarat 138 SKS, IPK 2.50) | Penembakan `RULE_03` (Klasifikasi SOP) & `RULE_05`, syarat 138 SKS dan tenggat revisi 14 hari kerja terekstraksi. | Alur 4 tahap dan syarat administrasi tersaji terstruktur. | **VALID** |
| **TC-04** | Uji Isolasi Klausul Larangan Keras (*Negative Constraint*) | Berkas PDF `SK-04_Peraturan_Tata_Tertib_Kampus.pdf` (Larangan Plagiasi & Sanksi) | Penembakan `RULE_04` (Klasifikasi Peraturan) & `RULE_06` (Deteksi Larangan Khusus), klausul larangan plagiarisme dan sanksi skorsing terangkat. | Peringatan larangan keras dan ancaman sanksi terisolasi dalam blok peringatan. | **VALID** |
| **TC-05** | Uji Pemadatan Teks Laporan & Target Rasio Kompresi | Berkas PDF `SK-05_Laporan_Ringkasan_Kegiatan.pdf` (Capaian 15 desa, 120 UMKM) | Penembakan `RULE_08` (Klasifikasi Laporan) & `RULE_07`, seluruh angka capaian dipertahankan, rasio kompresi $\ge 70\%$. | Capaian kuantitatif dipertahankan, rasio kompresi teks mencapai $73{,}5\%$. | **VALID** |
| **TC-06** | Uji Penanganan Masukan Ilegal / Berkas Korup | Berkas biner non-PDF / teks kosong 0 byte | Sistem menolak berkas, menampilkan pesan galat terstruktur tanpa ANR atau crash. | Muncul notifikasi "Format berkas tidak didukung / dokumen kosong". | **VALID** |

**Langkah Kalkulasi Substitusi ke Formula (2.1):**
1. Identifikasi parameter:
   * Total skenario kasus uji teruji ($n$) $= 6$.
   * Jumlah skenario berstatus valid ($m$) $= 6$.
2. Substitusi ke rumus:
   $$P_{valid} = \left( \frac{6}{6} \right) \times 100\% = 1{,}0 \times 100\% = 100{,}0\%$$
3. **Kesimpulan Ilmiah:** Seluruh kasus uji modular lulus 100%, membuktikan artefak memenuhi target kelayakan fungsional rekayasa perangkat lunak tanpa galat kritis.

---

### 2.2 Kasus 2: Toy Walkthrough Kalkulasi Skor SUS 5 Responden
Simulasi evaluasi keterpakaian dilakukan dengan mengumpulkan data tiruan mentah skala Likert 1–5 dari 5 (lima) responden perwakilan mahasiswa terhadap 10 butir instrumen SUS baku:

*Daftar 10 Butir Instrumen SUS Baku (Brooke, 1996):*
1. *P1 (Positif):* Saya berpikir akan sering menggunakan sistem VelaPDF ini.
2. *P2 (Negatif):* Saya merasa sistem ini terlalu rumit untuk digunakan.
3. *P3 (Positif):* Saya merasa sistem ini sangat mudah untuk digunakan.
4. *P4 (Negatif):* Saya membutuhkan bantuan orang lain atau teknisi untuk menggunakan sistem ini.
5. *P5 (Positif):* Saya merasa fitur-fitur sistem ini terintegrasi dengan sangat baik.
6. *P6 (Negatif):* Saya merasa ada terlalu banyak hal yang tidak konsisten pada sistem ini.
7. *P7 (Positif):* Saya rasa kebanyakan orang akan cepat mempelajari sistem ini.
88: 8. *P8 (Negatif):* Saya merasa sistem ini sangat membingungkan saat pertama kali dipakai.
89: 9. *P9 (Positif):* Saya merasa sangat percaya diri saat menggunakan sistem ini.
90: 10. *P10 (Negatif):* Saya perlu mempelajari banyak hal terlebih dahulu sebelum dapat mengoperasikan sistem.

#### Tabel Data Mentah Responden ($X_{k,i}$):
| Responden ($k$) | P1 | P2 | P3 | P4 | P5 | P6 | P7 | P8 | P9 | P10 |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **R1** | 5 | 1 | 5 | 2 | 5 | 2 | 5 | 2 | 5 | 2 |
| **R2** | 4 | 2 | 4 | 2 | 4 | 2 | 4 | 2 | 4 | 2 |
| **R3** | 5 | 2 | 4 | 3 | 5 | 2 | 4 | 3 | 5 | 2 |
| **R4** | 4 | 1 | 4 | 2 | 5 | 2 | 4 | 2 | 4 | 2 |
| **R5** | 4 | 2 | 4 | 3 | 4 | 2 | 4 | 3 | 5 | 3 |

---

#### Langkah Kalkulasi Transformasi Skor Kontribusi ($S_{k,i}$):
*Aturan: Butir Ganjil $(X - 1)$, Butir Genap $(5 - X)$*

1. **Kalkulasi Responden 1 (R1):**
   * $S_{1,1} = 5 - 1 = 4$
   * $S_{1,2} = 5 - 1 = 4$
   * $S_{1,3} = 5 - 1 = 4$
   * $S_{1,4} = 5 - 2 = 3$
   * $S_{1,5} = 5 - 1 = 4$
   * $S_{1,6} = 5 - 2 = 3$
   * $S_{1,7} = 5 - 1 = 4$
   * $S_{1,8} = 5 - 2 = 3$
   * $S_{1,9} = 5 - 1 = 4$
   * $S_{1,10} = 5 - 2 = 3$
   * $\sum S_{1,i} = 4 + 4 + 4 + 3 + 4 + 3 + 4 + 3 + 4 + 3 = 36$
   * $\text{Skor}_1 = 36 \times 2{,}5 = 90{,}0$

2. **Kalkulasi Responden 2 (R2):**
   * Ganjil: $(4-1)=3, (4-1)=3, (4-1)=3, (4-1)=3, (4-1)=3 \implies \text{Total Ganjil} = 15$
   * Genap: $(5-2)=3, (5-2)=3, (5-2)=3, (5-2)=3, (5-2)=3 \implies \text{Total Genap} = 15$
   * $\sum S_{2,i} = 15 + 15 = 30$
   * $\text{Skor}_2 = 30 \times 2{,}5 = 75{,}0$

3. **Kalkulasi Responden 3 (R3):**
   * Ganjil: $(5-1)=4, (4-1)=3, (5-1)=4, (4-1)=3, (5-1)=4 \implies 18$
   * Genap: $(5-2)=3, (5-3)=2, (5-2)=3, (5-3)=2, (5-2)=3 \implies 13$
   * $\sum S_{3,i} = 18 + 13 = 31$
   * $\text{Skor}_3 = 31 \times 2{,}5 = 77{,}5$

4. **Kalkulasi Responden 4 (R4):**
   * Ganjil: $(4-1)=3, (4-1)=3, (5-1)=4, (4-1)=3, (4-1)=3 \implies 16$
   * Genap: $(5-1)=4, (5-2)=3, (5-2)=3, (5-2)=3, (5-2)=3 \implies 16$
   * $\sum S_{4,i} = 16 + 16 = 32$
   * $\text{Skor}_4 = 32 \times 2{,}5 = 80{,}0$

5. **Kalkulasi Responden 5 (R5):**
   * Ganjil: $(4-1)=3, (4-1)=3, (4-1)=3, (4-1)=3, (5-1)=4 \implies 16$
   * Genap: $(5-2)=3, (5-3)=2, (5-2)=3, (5-3)=2, (5-3)=2 \implies 12$
   * $\sum S_{5,i} = 16 + 12 = 28$
   * $\text{Skor}_5 = 28 \times 2{,}5 = 70{,}0$

---

#### Perhitungan Rata-Rata Kelompok Responden ($\overline{\text{SUS}}$):
Substitusi nilai skor kelima responden ke Formula (2.5):

$$\overline{\text{SUS}} = \frac{90{,}0 + 75{,}0 + 77{,}5 + 80{,}0 + 70{,}0}{5} = \frac{392{,}5}{5} = 78{,}50$$

---

### 2.3 Penarikan Kesimpulan Ilmiah Berdasarkan Teori Acuan
Berdasarkan nilai rata-rata $\overline{\text{SUS}} = 78{,}50$:
1. **Ambang Batas Keberhasilan:** Nilai $78{,}50$ melampaui ambang batas standar minimal kelulusan rekayasa ($\ge 68{,}00$) dengan selisih surplus sebesar $+10{,}50$ poin.
2. **Kategori Penerimaan (Acceptability Range):** Masuk ke dalam kategori **"Acceptable"** (Sangat Dapat Diterima oleh Pengguna).
3. **Peringkat Kualitatif (Adjective Rating):** Berada pada kategori **"Good"** menuju **"Excellent"**.
4. **Skala Nilai Akademik (Grade Scale):** Berada pada predikat **Grade B** (Bangor et al., 2008).
5. **Implikasi Rekayasa:** Pengguna menilai integrasi OCR lokal, prapemrosesan NLP, dan mesin inferensi ontologi pada sistem VelaPDF sangat praktis, intuitif, dan mempercepat pemahaman dokumen tanpa kekhawatiran privasi data.

---

## 3. Daftar Pustaka Seminal (Standar IEEE)

1. G. J. Myers, C. Sandler, and T. Badgett, *The Art of Software Testing*, 3rd ed. Hoboken, NJ, USA: John Wiley & Sons, 2011.
2. J. Brooke, "SUS: A 'quick and dirty' usability scale," in *Usability Evaluation in Industry*, P. W. Jordan, B. Thomas, I. L. McClelland, and B. Weerdmeester, Eds. London, UK: Taylor & Francis, 1996, pp. 189–194.
3. A. Bangor, P. T. Kortum, and J. T. Miller, "An empirical evaluation of the System Usability Scale," *International Journal of Human-Computer Interaction*, vol. 24, no. 6, pp. 574–594, 2008. https://doi.org/10.1080/10447310802205776
