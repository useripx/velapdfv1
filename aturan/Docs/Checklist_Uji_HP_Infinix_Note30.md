# Checklist Uji Perangkat Fisik (Infinix Note 30) Pasca-Tahap — VelaPDF

> **TUJUAN DOKUMEN:** Panduan langkah demi langkah bagi **Yogi Ario (`@useripx`)** untuk menguji langsung APK **VelaPDF** pada HP fisik **Infinix Note 30** setiap kali satu tahapan selesai dikerjakan oleh AI Agent, sebelum menyatakan **`buatkan rilis`** pada *Implementation Plan* tahap berikutnya.  
> **VERSI SAAT INI DI `app/build.gradle.kts`:** `versionCode = 4` / `versionName = "1.4.0"` *(Tahap 02 telah dirilis ke `v1.4.0`; APK build Tahap 03 [`app-debug.apk`](../../app/build/outputs/apk/debug/app-debug.apk) siap diuji di HP fisik **sebelum** perintah **`buatkan rilis`** mem-bump versi ke `versionCode = 5` / `v1.5.0`)*.

---

## Bagian A — Checklist Uji Dasar & Isolasi Offline (Wajib Setiap Akhir Tahap)

1. **Pemasangan APK:**
   - Pasang APK terbaru di **Infinix Note 30** *(jika menimpa aplikasi lama yang sudah terpasang, pastikan riwayat lama tidak hilang dan aplikasi tidak crash saat pertama dibuka)*.
2. **Aktifkan Mode Pesawat (Airplane Mode ON):**
   - Matikan **Wi-Fi** dan **Data Seluler** sepenuhnya untuk membuktikan seluruh fitur inti benar-benar berjalan **100% Luring (*Offline*)**.
3. **Alur Masuk & Izin:**
   - Buka aplikasi $\rightarrow$ lewati `SplashScreen` $\rightarrow$ pada `PermissionsScreen` beri izin **Kamera** dan **Penyimpanan/Media**.
   - Pada layar `Login`, ketuk **"Masuk Sebagai Tamu (Mode Offline)"**.
   - Buka menu drawer samping (*navigation drawer*) dan cek apakah nama & nomor versi aplikasi terbaca dengan jelas.
4. **Sanity Check Utilitas PDF Lama (Antisipasi Regresi):**
   - Coba cepat **Image to PDF** (1–2 gambar $\rightarrow$ jadi PDF).
   - Coba **Merge PDF** (gabungkan 2 PDF kecil $\rightarrow$ berhasil).
   - Coba **PDF to Image** (ubah 1 PDF $\rightarrow$ gambar tersimpan).
   - Buka menu **`Riwayat`** / **`Settings`** dan pastikan tidak ada tombol error atau crash.

---

## Bagian B — Checklist Uji Khusus Fitur AI & OCR (Tahap 01 + Tahap 02)

Jalankan pengujian berikut **dalam kondisi Airplane Mode ON** di **Infinix Note 30**:

### 1. Uji Kamera Langsung (`Scan` & `ASK Vela -> Pindai Kamera`)
- [ ] Buka **`ASK Vela`** $\rightarrow$ ketuk **`Pindai Kamera`** (atau tab bawah **`Scan`**).
- [ ] Foto **1–3 halaman buku cetak Bahasa Indonesia / surat cetak resmi** dengan cahaya cukup.
- [ ] Pada pratinjau/edit gambar (`EditImageScreen` / `ImageToPdfScreen`), uji tombol **"Edit OCR"** dulu: pastikan teks hasil OCR terbaca wajar dan bisa disunting manual bila ada salah huruf.
- [ ] Ketuk **"Buat Ringkasan & Kesimpulan (Offline)"** dan pastikan langsung terbuka layar **`Ringkasan & Kesimpulan`**.

### 2. Uji Pilih Gambar Galeri
- [ ] Buka **`ASK Vela`** $\rightarrow$ ketuk **`Pilih Gambar`**.
- [ ] Pilih **2 foto halaman dokumen** dari galeri $\rightarrow$ pastikan jumlah halaman terdeteksi sesuai dan ringkasan berhasil dibuat offline.

### 3. Uji Unggah Berkas PDF + Rentang Halaman
- [ ] Buka **`ASK Vela`** $\rightarrow$ ketuk **`Unggah Berkas PDF`**.
- [ ] Uji **PDF Surat Resmi 1 Halaman** (misal `SK-01` s.d. `SK-05`): pilih **`Semua Halaman`**.
- [ ] Uji **PDF Multi-Halaman** (misal Jurnal `10` hal. atau `Roadmap.pdf` `19` hal. / Buku IoT): pilih **`Pilih Rentang Halaman Tertentu`** (contoh `Dari Hal. 1` s.d. `Sampai Hal. 5`) $\rightarrow$ pastikan dialog proses berjalan halus tanpa HP terasa hang/force close, dan label **`Cakupan:`** di hasil benar menampilkan `Hal. 1 - 5`.

### 4. Verifikasi Mutu Hasil AI di `DocumentSummaryScreen`
- [ ] **Kategori Dokumen:** Label kategori sesuai isi (mis. `Surat Keputusan (SK)`, `Artikel / Jurnal Ilmiah`, `Buku Ajar / Referensi Teknis`).
- [ ] **Pemisahan Jelas (Celah D):** Ada Kartu **"Ringkasan Isi Dokumen (*Document Summarization*)"**, Kartu **"Ringkasan Perwakilan Halaman / Bagian"** lengkap label `Hal. X`, dan Kartu terpisah **"Poin Penting Berbasis Aturan (*Rule-Based Key Points*)"**.
- [ ] **Accordion Teknis (Celah A & C):** Buka **`Fakta Semantik SPO & Ontologi`** dan **`Jejak Rule-Based Inference`**: pastikan tampak pola `[Subjek] predikat -> objek [Kelas.Properti]` serta aturan `RULE_XX` yang menembak.
- [ ] *(Opsional tapi disarankan — Uji Celah B Negasi)*: Pindai/ketik kalimat *"Pihak terkait tidak wajib mengumpulkan laporan."* di Edit OCR $\rightarrow$ pastikan **"tidak wajib"** tidak berubah menjadi kewajiban positif.
- [ ] **Salin, Bagikan & Ekspor PDF:** Ketuk **`Salin Teks`**, lalu ketuk **`Ekspor Ringkasan ke PDF`** $\rightarrow$ ketuk **`Buka PDF`** dan pastikan berkas PDF hasil ekspor bisa dibuka rapi oleh penampil PDF di Infinix Note 30 tanpa error.

### 5. Uji Pencarian & Hapus Massal Riwayat (`ASK Vela`)
- [ ] Pastikan **`Riwayat Ringkasan Tersimpan`** sudah berisi minimal **2–3 item**.
- [ ] Ketik kata kunci (misal judul, topik, atau kategori seperti `"Keputusan"` / `"Ontologi"`) di kotak pencarian $\rightarrow$ daftar tersaring seketika $\rightarrow$ ketuk **`X`** untuk mengembalikan semua daftar.
- [ ] Ketuk salah satu riwayat lama $\rightarrow$ pastikan hasilnya terbuka ulang dengan cepat tanpa memindai OCR ulang.
- [ ] Ketuk **`Tandai`** $\rightarrow$ centang beberapa kartu (uji juga tombol **`Pilih Semua`**) $\rightarrow$ ketuk **`Hapus (N)`** $\rightarrow$ tekan **`Hapus`** pada dialog konfirmasi.
- [ ] Tutup paksa (*swipe up* dari Recent Apps) lalu buka kembali aplikasi **VelaPDF** $\rightarrow$ pastikan riwayat yang dihapus tetap hilang, riwayat lain aman, dan berkas PDF asli di penyimpanan HP tetap utuh.

---

## Bagian C — Uji Tambahan Khusus Tahap 03 (Polish UI, Surat Pendek, & Hybrid PDF/OCR)

Setelah memasang [`app-debug.apk`](../../app/build/outputs/apk/debug/app-debug.apk) hasil Tahap 03 dalam **Airplane Mode ON**, pastikan 5 hal baru ini terlihat langsung di **Infinix Note 30**:
- [ ] **Kontras UI `AskVelaScreen` & `DocumentSummaryScreen` (`[OBS-UI-001]`):** Ikon putih pada tombol `Unggah Berkas PDF` / `Pindai Kamera` / `Pilih Gambar` tampak jelas, pill progress `"Analisis AI 100% Luring"` terbaca terang, serta hint `"Cari judul, topik, atau jenis..."` tidak terpotong.
- [ ] **Kecepatan & Akurasi Nomor Surat PDF Digital (`Paket 1B`):** Saat menganalisis `SK-01_Surat_Keputusan_Rektor.pdf`, hasil terbuka sekejap (`< 1` detik) dan nomor surat `112/SK/UNP-KD/X/2026` utuh sempurna tanpa hilang garis miring.
- [ ] **Pemadatan Surat Pendek (`[OBS-NLP-001]`):** Pada `SK-01`, pill biru menampilkan `Pemadatan Teks` positif (`~49%`, bukan `0.0%`).
- [ ] **PDF Campuran Scan + Teks (`Roadmap.pdf`):** Tetap terbaca mulus dari awal hingga akhir (teks digital via `iText7` + halaman diagram/gambar via ML Kit OCR).
- [ ] **Kamera Fisik Nyata (`Pindai Kamera` di pencahayaan ruang & kertas sungguhan):** Uji memfoto 1–2 halaman buku cetak / surat kampus langsung, pastikan fitur pemotongan sudut otomatis ML Kit Document Scanner + pembersihan baris terpenggal (`-`) + layar **"Edit OCR"** berjalan nyaman.

---

## Bagian D — Keputusan Setelah Selesai Menguji

- ✅ **Jika semua poin di atas berjalan lancar di Infinix Note 30:**  
  Berikan komentar **`buatkan rilis`** pada **Implementation Plan (`implementation_plan.md`)**. AI Agent (`Carl`) akan otomatis mem-bump versi ke **`v1.5.0` (`versionCode = 5`)** & merilis ke GitHub (`main` bersih tanpa `aturan/`).
- 🛠️ **Jika ditemukan bug/tampilan kurang pas di Infinix Note 30:**  
  Catat/screenshot layarnya dan beritahu AI Agent agar diperbaiki dulu sebelum rilis dibuat.
