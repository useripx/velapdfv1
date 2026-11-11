# Project Analysis & Audit Baseline Handover — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** ANALISIS PROYEK & AUDIT FASE 0 (BROWNFIELD HANDOVER RECORD)  
> **LANGKAH WORKFLOW:** Fase 0 Handover / Step 2 Engineering Workflow  
> **PEMILIK PROYEK:** Yogi Ario (`@useripx`)  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL AUDIT:** 11/11/2026  
> **ACUAN STANDAR:** [Docs/Standards/Engineering_Standards.md](Standards/Engineering_Standards.md), [Docs/Rules/Android_Kotlin_MVVM_Rules.md](Rules/Android_Kotlin_MVVM_Rules.md), [Docs/Rules/Legacy_Handover_Protocol.md](Rules/Legacy_Handover_Protocol.md)

---

## 1. Ringkasan Eksekutif & Karakteristik Proyek

- **Nama Proyek / Application ID:** `Vela PDF` / `com.njagakneai.velapdf` (`versionCode = 3`, `versionName = "1.3 (Beta Revision 3)"`)
- **Tujuan Bisnis & Kondisi Awal:**  
  Aplikasi Android utilitas dokumen PDF **100% Offline-First** (dengan opsi login Google/Firebase atau masuk langsung sebagai Tamu Offline). Fitur utilitas yang sudah berjalan meliputi **Image to PDF**, **Merge PDF**, **PDF to Image**, **Edit / Scan Ulang Citra Tunggal**, **Riwayat Lokal (Room)**, dan **Pengaturan (DataStore)**.  
  Fitur inti penelitian yang belum tersambung adalah **OCR On-Device & Analisis/Ringkasan Dokumen Cerdas (ASK Vela)** berbasis *Rule-Based Inference (Forward Chaining)* & ontologi dalam Bahasa Indonesia, yang rancangannya telah disiapkan oleh pengembang sebelumnya pada `C:\Users\yogia\Music\Docs\`.
- **Target Pengguna:** Mahasiswa, civitas akademika, dan pengguna umum Android yang membutuhkan konversi dokumen serta ringkasan/kesimpulan isi dokumen resmi maupun buku/PDF materi (misal buku IoT) secara luring.
- **Tingkat Risiko Proyek (`Risk Tier`):** **Medium–High** (terdapat migrasi paket `panjalupdf` → `velapdf` yang belum di-commit pada `develop`, cakupan unit test mendekati nol, dan integrasi modul OCR/AI baru yang berdampak ke navigasi & skema Room).

---

## 2. Audit Lingkungan Teknologi Eksisting (Hasil Pemeriksaan Gradle & Manifest)

*(Hasil pemeriksaan Read-Only pada `gradle/libs.versions.toml`, `gradle-wrapper.properties`, `app/build.gradle.kts`, dan `AndroidManifest.xml`. Versi dikunci dan tidak diubah sepihak.)*

| Komponen Arsitektur | Konfigurasi Aktual Eksisting | Versi | Catatan / Risiko Kompatibilitas |
| :--- | :--- | :---: | :--- |
| **Bahasa Pemrograman** | Kotlin (`JavaVersion.VERSION_11`) | `2.4.20` | Compiler sukses (`17` warning deprekasi API Compose/Android) |
| **AGP & Gradle** | Gradle Wrapper / Android Gradle Plugin | `9.8.0` / `9.4.1` | KSP `2.3.6`; `gradle.properties` memicu `6` warning properti AGP 10 |
| **SDK Android** | `minSdk` / `targetSdk` / `compileSdk` | `26` / `36` / `37` | `GmsDocumentScanningDelegateActivity` memakai `tools:remove="android:screenOrientation"` |
| **Pola Arsitektur** | **MVVM + Clean Architecture (`data` / `domain` / `di` / `ui` / `utils`)** | - | Aturan lama di `aturan/` masih tertulis MVVM polos — perlu diselaraskan |
| **UI Toolkit** | **100% Jetpack Compose** (Material 3, Navigation Compose, Coil, Accompanist) | Compose BOM `2026.09.00` | Murni Compose (tidak campur XML Views); Navigation `2.10.2`, Coil `2.7.0`, Accompanist `0.37.3` |
| **State Management** | **`StateFlow`** (+ local Compose `remember` state) | Kotlinx Coroutines | Beberapa operasi disk di ViewModel masih perlu `withContext(Dispatchers.IO)` |
| **Concurrency / Async** | Kotlin Coroutines & Flow | Bawaan Kotlin `2.4.20` | Nihil `GlobalScope` di `.kt` |
| **Dependency Injection (DI)** | **Dagger Hilt** (`@HiltAndroidApp`, `@AndroidEntryPoint`) | `2.60.1` | `hiltNavigationCompose = 1.4.0`; `di/AppModule.kt` masih stub kosong |
| **PDF & OCR Engine** | **Google ML Kit Doc Scanner & Text Recognition** + **iText7 Core** + native `PdfRenderer`/`PdfDocument` | Scanner `16.0.0`, OCR `16.0.1`, iText7 `9.8.0` | `MlKitRepository` sudah ter-bind via Hilt tapi belum mendukung render PDF & belum masuk `NavGraph` |
| **Auth / Cloud (Opsional)** | Google Services, Firebase BOM, Credential Manager, Google ID | GS `4.5.0`, Firebase `34.19.0`, Cred `1.6.0` | Mode Tamu Offline sudah tersedia di `LoginScreen` |
| **Database & Prefs Lokal** | **Room (`AppDatabase`)** + **DataStore Preferences** | Room `2.8.5`, DataStore `1.2.1` | Room `version = 1` (`history_table`, `exportSchema = false`); penambahan tabel baru **wajib** memakai `Migration(1, 2)` |

> [!NOTE] Discrepancy Dokumentasi Uji (`metopen/`) vs Workspace Aktual  
> Berkas `C:\Users\yogia\Music\Docs\metopen\05_spesifikasi_testbed\testbed_hardware_software.md` mencantumkan versi lama (`Gradle 8.9`, `Kotlin 2.0.21`, `iText7 7.2.5`, `Room 2.6.1`, `Hilt 2.51.1`, `Compose BOM 2024.09.00`), sedangkan codebase aktual telah menggunakan versi lebih baru pada tabel di atas. Sesuai instruksi pemilik proyek, pengembangan mengikuti versi aktual codebase.

---

## 3. Audit Kondisi Repositori & Warisan Kode Pengembang Sebelumnya

- **Status Git, Branch & Remote:**
  - Branch aktif: `develop` (tracking `origin/develop [gone]`, total **61 commit** historis; 5 commit terakhir: `f162ed2` KHS export, `ab7aa81`, `673fecd`, `49981c4`, `2f77fb4`).
  - Remote aktif saat ini: `origin` masih mengarah ke `https://github.com/useripx/PanjaluPDF.git`.
  - Target remote baru: `https://github.com/useripx/velapdfv1.git` — terverifikasi melalui `git ls-remote` dalam keadaan **kosong (`0` branch / `0` commit)** dan siap menerima dorongan baseline bersih.
  - Perubahan kerja belum ter-commit (*uncommitted changes*): **183 item** (`121` deleted, `25` modified, `37` untracked), meliputi migrasi paket `com.njagakneai.panjalupdf` → `com.njagakneai.velapdf`, penghapusan `Docs/` lama & fitur KHS (`PreviewKhsScreen.kt`, `KhsExporter.kt`), pembaruan Gradle/res/Manifest, keberadaan folder `aturan/`, `app/gs.json`, serta berkas log crash root (`hs_err_pid23324.log`, `hs_err_pid23732.log`, `replay_pid23732.log`) dan `velapdf_template/node_modules/`.
- **Status Baseline Build Awal:**
  - `.\gradlew.bat assembleDebug --no-daemon`: **PASS** (`BUILD SUCCESSFUL in 3m 42s`, 51 actionable tasks; `compileDebugKotlin` sukses dengan 17 warning deprekasi & 6 warning AGP 10).
  - `.\gradlew.bat testDebugUnitTest --no-daemon`: **PASS** secara teknis, namun cakupan hanya **1 unit test dummy** (`ExampleUnitTest.addition_isCorrect`: `tests="1" skipped="0" failures="0" errors="0"`, `0.011s`).
  - `.\gradlew.bat lintDebug --no-daemon`: **FAIL (Baseline Warisan)** — `Lint found 2 errors, 46 warnings, 1 hint` (`HistoryScreen.kt:98` `[NonObservableLocale]` dan `AndroidManifest.xml:5` `[PermissionImpliesUnsupportedChromeOsHardware]`, tercatat sebagai `DEF-LEG-009`).
- **Peta Struktur Paket Eksisting (`app/src/main/java/com/njagakneai/velapdf/`):**
  ```text
  com.njagakneai.velapdf
  ├── VelaPDFApplication.kt              (@HiltAndroidApp)
  ├── MainActivity.kt                    ([LEGACY] template mati "Hello Android!")
  ├── data/
  │   ├── database/                      (AppDatabase.kt v1, HistoryDao.kt)
  │   ├── model/                         (CompressionLevel, EditOutputFormat, HistoryEntity, HistoryItem, ImageFormat, MergeableDocument, SelectedImage)
  │   ├── ocr/                           (MlKitRepository.kt — @Singleton bundled OCR)
  │   ├── preferences/                   (PreferencesManager.kt — DataStore "velapdf_settings")
  │   └── repository/                    (PdfGenerationState.kt, PdfRepository.kt)
  ├── di/
  │   ├── AppModule.kt                   ([LEGACY] stub kosong)
  │   └── DatabaseModule.kt              (Provides AppDatabase & HistoryDao)
  ├── domain/
  │   ├── PdfConverterEngine.kt          ([LEGACY] stub kosong)
  │   └── model/                         (OcrModels.kt: ScannedPage, ExtractedText)
  ├── ui/
  │   ├── components/                    (CompressionDialog, FormatSelectorChips, GridImagePicker, NotificationToast, PermissionDialogs, SortableDocumentList, SortableImageGrid)
  │   ├── navigation/                    (NavGraph.kt — 12 route; OcrPreviewScreen & AI Analysis belum masuk)
  │   ├── screen/                        (DashboardScreen, ImageToPdfScreen, MergePdfScreen, PdfToImageScreen, EditImageScreen, OcrPreviewScreen, ConverterScreen, HistoryScreen, SettingsScreen, LoginScreen, PermissionsScreen, PermissionsViewModel.kt, SplashScreen, SuccessScreen)
  │   ├── theme/                         (Color.kt, Theme.kt, Type.kt — HankenGrotesk)
  │   ├── view/                          (MainActivity.kt — launcher @AndroidEntryPoint aktif)
  │   └── viewmodel/                     (Auth, EditImage, History, ImageToPdf, MainViewModel [stub], MergePdf, Ocr, PdfToImage, Settings, Splash)
  └── utils/                             (BitmapProcessor, FileShareHelper, FileUriHelper, NotificationHelper, PdfGenerator, PdfMergerEngine, PdfToImageEngine, PermissionHelper, PermissionUtils [stub])
  ```
- **Audit Keamanan Warisan (Secret & Keystore Check):**
  - Pemindaian kode sumber Kotlin tidak menemukan `GlobalScope`, `fallbackToDestructiveMigration`, `Log.*`, `AIza`, `SECRET`, `API_KEY`, ataupun sisa namespace `panjalupdf`.
  - `app/gs.json` (untracked) identik SHA-256 (`d6b67a288c4b...`) dengan `app/google-services.json` (`project_id = comnjagakneaivelapdf`, 3 klien OAuth).
  - `.gitignore` mengomentari `#*.jks`, `#*.keystore`, dan `#app/google-services.json` sehingga berisiko tidak memblokir keystore produksi di masa depan bila tidak diperketat.
- **Utang Teknis & Bug Bawaan Teridentifikasi (`[LEGACY]`):**  
  Lihat rincian lengkap `DEF-LEG-001` s.d. `DEF-LEG-008` pada [`aturan/Docs/Defect_Log.md`](Defect_Log.md).

---

## 4. Analisis Batasan & Asumsi (Constraints & Assumptions)

### 4.1 Batasan Sistem (`Constraints`)
1. **Lokus Pemrosesan 100% Offline (OCR & AI):** Ekstraksi OCR, prapemrosesan NLP Bahasa Indonesia, ekstraksi fakta SPO, penembakan aturan (*Forward Chaining*), perumusan ringkasan/kesimpulan Bahasa Indonesia, dan penyimpanan Room **wajib** berjalan luring (0 penggunaan internet).
2. **Batasan Fitur Tanya Jawab (Out-of-Scope Implementasi):** Sesuai arahan Yogi Ario, menu **ASK Vela** dan tombol ringkasan berfokus menghasilkan **ringkasan dan kesimpulan dalam Bahasa Indonesia** tanpa fitur tanya jawab (*Q&A*) interaktif. Batasan ini dicatat dalam `aturan/Docs/batasan_masalah.md` pada Tahap 0.
3. **Kepatuhan Dokumentasi Lokal:** Seluruh dokumentasi tata kelola, rencana implementasi (`Docs/`), cadangan chat (`Docs/chat/`), serta aset uji (`alatuji/`) dikelola rapi di dalam direktori `aturan/` tanpa menyebar berkas aturan ke root proyek.
4. **Kepatuhan SDK & Dependensi:** `minSdk 26`, `targetSdk 36`, `compileSdk 37`, Compose, dan Hilt dipertahankan tanpa perubahan versi.

### 4.2 Asumsi Teknis (`Assumptions`)
1. Perangkat pengguna mendukung Google Play Services untuk ML Kit Document Scanner (atau pemilih berkas PDF/gambar standar jika memproses file lokal).
2. Ontologi (`document_ontology.json`) dan aturan produksi (`production_rules.json`) pada `app/src/main/assets/` diperluas untuk mendukung teks buku/materi umum berbahasa Indonesia maupun Inggris (seperti buku IoT halaman 23–35) sambil menjaga kompatibilitas penuh terhadap skenario formal `SK-01` s.d. `SK-05`.

---

## 5. Peta Ketergantungan Eksternal (External & System Integrations)

| Layanan / Library | Fungsi Sistem | Tingkat Kekritisan | Strategi Mitigasi / Catatan Offline |
| :--- | :--- | :---: | :--- |
| **Google ML Kit Text Recognition (`16.0.1`)** | OCR teks lokal on-device | **KRITIS** | Menggunakan model *bundled*, beroperasi 100% offline |
| **Google ML Kit Document Scanner (`16.0.0`)** | Pemindaian & *auto-crop* kamera | **TINGGI** | Dapat ditambah fallback pemilihan gambar/PDF galeri jika modul scanner belum siap |
| **iText7 Core (`9.8.0`) & Android `PdfRenderer`** | Pembuatan, penggabungan & rendering halaman PDF ke Bitmap | **KRITIS** | Sepenuhnya lokal di perangkat |
| **Firebase Auth / Credential Manager** | Login Google/Email opsional | **RENDAH (Opsional)** | Tersedia mode **"Masuk Sebagai Tamu (Mode Offline)"** pada `LoginScreen` |

---

## 6. Rekomendasi Tahapan Implementasi (Roadmap Ringkas)

1. **Tahap 0 (Bootstrap, Repo Alignment, Rules Tailoring & `alatuji`):** Menyesuaikan aturan di `aturan/` ke arsitektur proyek aktual + persona `Carl`, memindahkan/menyalin aset `metopen` ke `aturan/alatuji`, membuat `aturan/Docs/batasan_masalah.md`, merapikan `.gitignore` & `README.md` (`logo.png`), menukar remote ke `https://github.com/useripx/velapdfv1.git`, serta mengamankan baseline commit bersih sebelum fitur baru dikodekan.
2. **Tahap 1 (Inti OCR & Analisis Dokumen AI — Rule-Based Inference & ASK Vela):** Mengintegrasikan rendering PDF + OCR multi-halaman ke `MlKitRepository`, menyesuaikan modul NLP/Ontologi/Forward Chaining/Summarizer dengan perluasan domain buku/PDF teknis + Bahasa Indonesia, menambahkan aksi **"Ringkasan"** setelah scan/edit, membangun menu **"ASK Vela"** (pilih hasil scan atau unggah PDF → ringkasan & kesimpulan Bahasa Indonesia), migrasi Room DB v1→v2 bila skema fakta/ringkasan disimpan persisten, serta pengujian unit & emulator lengkap.
