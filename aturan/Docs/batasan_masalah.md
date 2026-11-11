# Batasan Masalah Sistem — VelaPDF (`Rule-Based Inference & Offline Summarization`)

> **STATUS DOKUMEN:** BATASAN MASALAH RESMI (BAHAN SEMINAR PROPOSAL & RUANG LINGKUP IMPLEMENTASI)  
> **PROYEK:** VelaPDF (`com.njagakneai.velapdf`)  
> **PENELITI / PEMILIK PROYEK:** Yogi Ario Pratama (`NPM: 2313020004` / `@useripx`)  
> **PENYUSUN DOKUMENTASI:** Carl (`AGT-PRD-DEV-001`)  
> **TERAKHIR DIPERBARUI:** 11/11/2026  

---

## 1. Tujuan Dokumen

Dokumen ini merangkum batasan ruang lingkup fungsional dan teknis aplikasi **VelaPDF** untuk keperluan **Seminar Proposal (Sempro)** serta sebagai pagar implementasi pengembangan perangkat lunak agar fokus riset tetap terarah pada pemrosesan dokumen **100% luring (*offline-first*)** menggunakan **Google ML Kit OCR On-Device** dan **Rule-Based Inference (*Forward Chaining*)**.

---

## 2. Daftar Batasan Masalah (Scope Boundaries)

### 2.1 Batasan Tanya Jawab Interaktif & Kecerdasan Generatif Cloud (`No Q&A / No Cloud LLM`)
- Sistem (**ASK Vela** maupun aksi **Ringkasan** pasca-pemindaian) **hanya menghasilkan ringkasan isi dokumen dan kesimpulan terstruktur** secara deterministik berdasarkan fakta semantik dan aturan produksi (*IF–THEN*).
- Sistem **tidak menyediakan fitur tanya jawab interaktif (*chatbot Q&A*)**, *prompting* bebas, maupun pemanggilan model *Large Language Model (LLM)* berbasis awan/internet (*cloud API*). Seluruh proses inferensi berlangsung **100% offline** di perangkat pengguna (*0-byte internet data usage*).

### 2.2 Batasan Bahasa Dokumen & Keluaran (`Bahasa Indonesia Only`)
- Pemrosesan teks alami (*NLP Preprocessing*: *sentence splitting*, *stopword removal*), pemetaan taksonomi ontologi (`document_ontology.json`), penembakan aturan produksi (`production_rules.json`), dan sintesis kesimpulan pada tahap saat ini **hanya mendukung dokumen berbahasa Indonesia** serta **menghasilkan ringkasan dan kesimpulan dalam Bahasa Indonesia saja**.
- Kata kunci teknis serapan atau istilah baku lintas-disiplin yang umum muncul di dalam naskah buku berbahasa Indonesia (seperti *IoT*, *MQTT*, *sensor*, *gateway*, *broker*, *HTTP*) tetap dapat dikenali sebagai kata kunci pembentuk topik, namun naskah penuh berbahasa asing (seperti artikel bahasa Inggris murni atau bahasa non-Latin) berada di luar dukungan sistem saat ini.

### 2.3 Batasan Format, Kualitas & Jenis Konten Masukan
- Sumber masukan dokumen untuk analisis AI dibatasi pada **citra/foto halaman hasil pemindaian kamera** (`JPG`/`PNG`/`WEBP`) serta berkas digital **`PDF` standar tanpa proteksi kata sandi (*non-encrypted*)**.
- Sistem **belum menangani**:
  1. Berkas `PDF` yang terkunci kata sandi atau terproteksi DRM.
  2. Ekstraksi tulisan tangan (*handwriting*), aksara non-Latin, tabel bersarang rumit, grafik visual/diagram rangkaian tanpa keterangan teks, maupun notasi rumus matematika kompleks.
  3. Format dokumen perkantoran/multimedia lain di luar foto dan PDF (seperti `.docx`, `.pptx`, `.xlsx`, atau berkas audio).

### 2.4 Batasan Ukuran Dokumen & Sumber Daya Perangkat Luring
- Untuk menjaga batas konsumsi memori kerja (*heap memory* lokal $\le 50\text{ MB}$) serta waktu respons komputasi pada perangkat Android kelas menengah secara offline, analisis berkas `PDF` berhalaman tebal (misal buku ajar) diarahkan pada pemrosesan **rentang halaman tertentu / batas halaman aman per sesi analisis** (misalnya 10–35 halaman per sesi seperti *halaman 23 s.d. 35*), bukan pemrosesan ratusan halaman sekaligus dalam satu kali pemicu OCR.

### 2.5 Batasan Utilitas PDF, Penyuntingan Grafis & Layanan Akun Online
- **Penghapusan Modul KHS:** Fitur pemindaian/ekspor Kartu Hasil Studi (`KHS`) telah dihilangkan secara permanen agar aplikasi berfokus pada utilitas PDF inti (`Image to PDF`, `Merge PDF`, `PDF to Image`, `Riwayat Lokal`, `Pengaturan`) dan fitur analisis dokumen AI offline.
- **Penyuntingan Citra:** Penyuntingan gambar hasil pindai terbatas pada pemindaian ulang, pemotongan sudut otomatis, dan penyaring dasar melalui *ML Kit Document Scanner* serta koreksi manual teks hasil OCR sebelum diringkas; fitur desain grafis lanjutan (seperti pembubuhan tanda tangan digital, watermark, anotasi gambar bebas, atau pengaturan warna/kecerahan manual) berada di luar batasan.
- **Akun & Sinkronisasi Awan:** Aplikasi dirancang *offline-first* dengan opsi **"Masuk Sebagai Tamu (Mode Offline)"** pada [`LoginScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/LoginScreen.kt); fitur manajemen akun daring (*sign-up* mandiri, *reset password*) dan sinkronisasi riwayat lintas perangkat berbasis cloud tidak diimplementasikan.

---

## 3. Poin Penyesuaian Lanjutan (Dikonfirmasi Bersamaan dengan Rencana Tahap 1)

Beberapa detail UX antarmuka yang masih dapat dipilih antara implementasi di Tahap 1 atau dicatat ke batasan akhir adalah:
1. Arah tombol tab bawah `Convert` di [`DashboardScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/DashboardScreen.kt) (menggantikan [`ConverterScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/ConverterScreen.kt) yang masih placeholder kosong).
2. Mekanisme pemilihan rentang halaman saat mengunggah PDF tebal di menu **ASK Vela**.
3. Opsi ekspor hasil ringkasan (cukup simpan ke Room + Salin/Bagikan teks, atau ditambah ekspor ringkasan ke berkas `.pdf`).
