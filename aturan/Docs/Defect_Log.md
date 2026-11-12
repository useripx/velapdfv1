# Defect & Bug Log — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** PELACAK CACAT & BUG AKTIF (ACTIVE DEFECT TRACKER)  
> **DESIGNATED APPROVER:** Rujuk [config/organization.md](../config/organization.md) (`Yogi Ario` / `@useripx`)  
> **PENANGGUNG JAWAB QA / AUDIT:** Carl (`AGT-PRD-DEV-001`)  
> **TERAKHIR DIPERBARUI:** 2026-11-12 10:46 WIB  
> **ACUAN STANDAR:** [Docs/Standards/Defect_Management.md](Standards/Defect_Management.md), [Docs/Standards/QA_Framework.md](Standards/QA_Framework.md), & [Docs/Rules/Legacy_Handover_Protocol.md](Rules/Legacy_Handover_Protocol.md)  
> **PRINSIP UTAMA:** *Defect tidak boleh ditutup hanya karena kode telah diubah; wajib diverifikasi ulang (Retesting PASS).*

---

## 1. Ringkasan Status Defect

| Total Defect Terdeteksi | Open (Menunggu Perbaikan) | In Progress / Retesting | Closed (Tuntas Terverifikasi) |
| :---: | :---: | :---: | :---: |
| **16** | **4** *(Legacy Terisolasi)* | **0** | **12** *(Tahap 0–3 + Hotfix UI)* |

*(Catatan: Sisa 4 item terbuka `DEF-LEG-001`, `DEF-LEG-002`, `DEF-LEG-003` pada utilitas legacy lama, dan `DEF-LEG-008` pada warning deprekasi Gradle/Compose diisolasi agar modul stabil tidak mengalami regresi liar sesuai [`Legacy_Handover_Protocol.md`](Rules/Legacy_Handover_Protocol.md).)*

---

## 2. Tabel Indeks Defect

| Defect ID | Tanggal | Modul | Judul Cacat / Masalah | Severity | Priority | Status | Verifikasi Akhir |
| :---: | :---: | :--- | :--- | :---: | :---: | :---: | :---: |
| **`DEF-LEG-001`** | 2026-11-11 | `PdfToImage` & `Utils` | `[LEGACY]` Penggunaan operator non-null assertion `!!` (11 titik berisiko `NullPointerException`) | Medium | P2-Medium | **OPEN** | Menunggu |
| **`DEF-LEG-002`** | 2026-11-11 | `PdfToImage` & `FileUriHelper` | `[LEGACY]` Penggunaan `Uri.fromFile(...)` berisiko `FileUriExposedException` saat berbagi/buka berkas | Medium | P2-Medium | **OPEN** | Menunggu |
| **`DEF-LEG-003`** | 2026-11-11 | `ViewModels` & `MlKitRepository` | `[LEGACY]` Operasi I/O disk/bitmap & decode URI berjalan di luar `Dispatchers.IO` *(Catatan: `MlKitRepository`, `DocumentAnalysisRepository`, & `SummaryPdfExporter` sudah lunas `Dispatchers.IO`/`Default` di Tahap 1; sisa legacy VM ditahan terisolasi)* | High | P1-High | **OPEN (Mitigated on OCR/AI)** | Parsial Tahap 1 |
| **`DEF-LEG-004`** | 2026-11-11 | `App` / `DI` / `Navigation` | `[LEGACY]` 5 berkas stub mati kosong (`MainActivity` template, `AppModule`, `PdfConverterEngine`, `MainViewModel`, `PermissionUtils`) + placeholder `ConverterScreen.kt` | Low | P3-Low | **CLOSED** | 2026-11-11 (Tahap 0 & Tahap 1 PASS) |
| **`DEF-LEG-005`** | 2026-11-11 | `OCR` & `Dashboard` | `[LEGACY]` Modul OCR (`OcrPreviewScreen`/`OcrViewModel`) terputus dari `NavGraph` & Analisis AI masih placeholder | High | P1-High | **CLOSED** | 2026-11-11 (Retest PASS — Tahap 1 `task-715`) |
| **`DEF-STAGE2-001`** | 2026-11-12 | `Domain NLP` & `4 JSON` | Celah `A–D`: asal fakta `mengindikasikanKonsep`, stopword menghapus kata negasi (`tidak`/`wajib`), pemetaan Ontologi–SPO belum eksplisit, & campuran ringkasan isi vs poin aturan | High | P1-High | **CLOSED** | 2026-11-12 (`20/20` unit test + `3/3` androidTest PASS) |
| **`DEF-STAGE2-002`** | 2026-11-12 | `AskVelaScreen.kt` | Peringatan Android Lint `UseKtx` pada parsing URI string (`toUri()` KTX) | Low | P3-Low | **CLOSED** | 2026-11-12 (`lintDebug` `0 errors`) |
| **`DEF-STAGE3-001`** (`[OBS-UI-001]`) | 2026-11-12 | `AskVelaScreen`, `AnalysisProgressOverlay`, `DocumentSummaryScreen` | Kontras ikon kotak gelap & pill progress/kompresi rendah, serta placeholder search bar terpotong elipsis | Medium | P2-Medium | **CLOSED** | 2026-11-12 (7 screenshot Tahap 3 verified) |
| **`DEF-STAGE3-002`** (`[OBS-NLP-001]`) | 2026-11-12 | `FactBasedSummarizer.kt` | Rasio kompresi surat sangat pendek (`SK-01..05` `< 220` kata) bernilai `0.0%` karena template narasi lebih panjang dari naskah sumber | Medium | P2-Medium | **CLOSED** | 2026-11-12 (`FactBasedSummarizerTest` `4/4` & `SK-01` `49.4%`) |
| **`DEF-STAGE3-003`** (Paket 1B + XML Fix) | 2026-11-12 | `MlKitRepository`, `TextExtractionCleaner`, `NlpTextPreprocessor` | Ekstraksi OCR bitmap memicu salah baca tanda baca/spasi dan `iText7` sempat melempar `UnsupportedOperationException (setXIncludeAware)` di Android runtime saat membaca XMP PDF | High | P1-High | **CLOSED** | 2026-11-12 (`benchmark_results_tahap3.csv`: 17 PDF dalam `29.467s`) |
| **`DEF-LEG-006`** | 2026-11-11 | `DashboardScreen` | `[LEGACY]` Teks versi aplikasi hardcoded (`VelaPDF v1.0.0`) tidak selaras dengan Gradle `1.3 (Beta Revision 3)` | Low | P3-Low | **CLOSED** | 2026-11-11 (Retest PASS — Tahap 1 `task-715`) |
| **`DEF-LEG-007`** | 2026-11-11 | `app/src/test` | `[LEGACY]` Cakupan unit test praktis kosong (hanya 1 dummy test `2 + 2 == 4` di `ExampleUnitTest.kt`) | High | P1-High | **CLOSED** | 2026-11-11 (Retest PASS — **11/11 tests PASS**) |
| **`DEF-LEG-008`** | 2026-11-11 | `Build` / `Git` / `Manifest` | `[LEGACY]` Izin `READ_MEDIA_VIDEO` & warning deprekasi Gradle/AGP/Compose legacy *(catatan: `app/gs.json`, log crash, & `.gitignore` sudah tuntas di Tahap 0)* | Medium | P2-Medium | **OPEN** | Menunggu |
| **`DEF-LEG-009`** | 2026-11-11 | `HistoryScreen` & `Manifest` | `[LEGACY]` 2 Error Lint bawaan memblokir `./gradlew lintDebug` (`NonObservableLocale` & `PermissionImpliesUnsupportedChromeOsHardware`) | High | P1-High | **CLOSED** | 2026-11-11 (Retest PASS — Tahap 0) |
| **`DEF-FIX-001`** | 2026-11-12 | `PdfToImageScreen.kt` | Kontainer isi `Column` tanpa `verticalScroll`, menyebabkan baris `"Pilih lokasi manual (Save As)"` terpotong menabrak `bottomBar` (`"Konversi ke ZIP"`) saat opsi ZIP aktif | Medium | P1-High | **CLOSED** | 2026-11-12 (`testDebugUnitTest` 26/26, `lintDebug` 0 error, & 2 screenshot emulator PASS) |
| **`DEF-FIX-002`** | 2026-11-12 | `HistoryScreen.kt` & `NavGraph.kt` | `TopAppBar` pada `"Riwayat Konversi"` tidak memiliki `navigationIcon` kembali ke Beranda | Medium | P1-High | **CLOSED** | 2026-11-12 (Navigasi kembali ke Beranda & screenshot emulator PASS) |

---

## 3. Rincian Temuan Defect Aktif (`[LEGACY]` Baseline)

### `[DEF-LEG-001]` — `[LEGACY]` Penggunaan Operator Non-Null Assertion `!!` Berisiko Crash

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `PdfToImageScreen.kt`, `PdfToImageViewModel.kt`, `PdfToImageEngine.kt`, `FileUriHelper.kt`
- **Lingkungan Uji:** Analisis Statis Kotlin & Kompilasi Gradle `compileDebugKotlin`
- **Severity (Dampak Teknis):** Medium
- **Priority (Urgensi Waktu):** P2-Medium
- **Status:** **OPEN** *(Isolasi `[LEGACY]` — sentuh hanya jika modul terkait dimodifikasi atau atas persetujuan Yogi Ario)*

#### Kondisi Aktual yang Ditemukan:
Pemindaian kode sumber Kotlin mendeteksi **11 kemunculan `!!`**:
- `app/src/main/java/com/njagakneai/velapdf/ui/screen/PdfToImageScreen.kt` (baris 110: `result.data!!.data!!`, baris 112 & 230: `selectedPdfUri!!`)
- `app/src/main/java/com/njagakneai/velapdf/ui/viewmodel/PdfToImageViewModel.kt` (baris 155, 161, 168, dan baris 184 yang juga memicu compiler warning *Unnecessary non-null assertion (!!) on a non-null receiver of type 'Uri'*)
- `app/src/main/java/com/njagakneai/velapdf/utils/FileUriHelper.kt` (baris 71: `File(uri.path!!)` di dalam `getSafeUri`)
- `app/src/main/java/com/njagakneai/velapdf/utils/PdfToImageEngine.kt` (baris 87, 90, 96)

#### Risiko / Dampak:
Jika `Intent` hasil pemilih dokumen atau `uri.path` bernilai `null` pada edge case sistem Android/SAF, aplikasi dapat langsung berhenti (*force close* `NullPointerException`).

---

### `[DEF-LEG-002]` — `[LEGACY]` Penggunaan `Uri.fromFile(...)` Berisiko `FileUriExposedException`

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `PdfToImageViewModel.kt`, `FileUriHelper.kt`
- **Lingkungan Uji:** Audit Kode Kotlin (`targetSdk = 36`, `minSdk = 26`)
- **Severity (Dampak Teknis):** Medium
- **Priority (Urgensi Waktu):** P2-Medium
- **Status:** **OPEN** *(Isolasi `[LEGACY]`)*

#### Kondisi Aktual yang Ditemukan:
- `PdfToImageViewModel.kt` (baris 99, 147, 152, 160, 168) membentuk URI keluaran menggunakan `Uri.fromFile(...)` pada direktori publik `Documents/VelaPDF/...`, berbeda dengan `ImageToPdfViewModel` yang menggunakan `FileProvider`.
- `FileUriHelper.kt` baris 50 (`copyUriToCache`) mengembalikan `Uri.fromFile(cacheFile)`.

#### Risiko / Dampak:
Pada Android 7.0+ (`API 24+`), URI berskema `file://` yang dikirimkan keluar aplikasi (misal lewat `Intent.ACTION_VIEW` dari `HistoryScreen`) memicu `FileUriExposedException` apabila tidak dikonversi ke `content://` melalui `FileProvider`.

---

### `[DEF-LEG-003]` — `[LEGACY]` Operasi I/O Disk, Bitmap & URI Decode Berpotensi Memblokir Main Thread

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `EditImageViewModel.kt`, `ImageToPdfViewModel.kt`, `MergePdfViewModel.kt`, `PdfToImageViewModel.kt`, `MlKitRepository.kt`
- **Lingkungan Uji:** Audit Konkurensi Coroutines
- **Severity (Dampak Teknis):** High
- **Priority (Urgensi Waktu):** P1-High *(Khusus `MlKitRepository` & alur Scan/Edit akan ditangani pada Tahap 1)*
- **Status:** **OPEN**

#### Kondisi Aktual yang Ditemukan:
- `EditImageViewModel.saveImage()` menjalankan `BitmapFactory.decodeStream` dan penyalinan berkas di dalam `viewModelScope.launch` tanpa `withContext(Dispatchers.IO)`.
- `ImageToPdfViewModel.generatePdf()` masih menyalin `tempFile.copyTo(finalFile)` dan stream `contentResolver` di luar `withContext(Dispatchers.IO)`.
- `MlKitRepository.extractText()` menerima URI dan memproses `InputImage.fromFilePath(...)` tanpa pembungkus `withContext(Dispatchers.IO)` dan belum mendukung rendering halaman PDF.

---

### `[DEF-LEG-004]` — `[LEGACY]` Berkas Stub Kosong & Komponen UI Placeholder Tidak Terhubung

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `MainActivity.kt` (root package), `di/AppModule.kt`, `domain/PdfConverterEngine.kt`, `ui/viewmodel/MainViewModel.kt`, `utils/PermissionUtils.kt`, `ui/screen/ConverterScreen.kt`, `ui/screen/PermissionsViewModel.kt`
- **Severity (Dampak Teknis):** Low
- **Priority (Urgensi Waktu):** P3-Low
- **Status:** **CLOSED** *(5 berkas stub mati dihapus bersih di Tahap 0 & diverifikasi lulus kompilasi; placeholder `ConverterScreen.kt` & penempatan `PermissionsViewModel.kt` ditangani terpisah di Tahap 1)*

#### Kondisi Aktual yang Ditemukan:
- Terdapat dua `MainActivity`: file aktif `.ui.view.MainActivity` (terdaftar di `AndroidManifest.xml`), dan file yatim `com.njagakneai.velapdf.MainActivity.kt` berisi template `"Hello Android!"`.
- Berkas `di/AppModule.kt`, `domain/PdfConverterEngine.kt`, `ui/viewmodel/MainViewModel.kt`, dan `utils/PermissionUtils.kt` hanya berisi 1 baris deklarasi `package`.
- `ConverterScreen.kt` dirutekan di `NavGraph.kt` dengan `images = emptyList()` (placeholder tak terpakai).
- `PermissionsViewModel.kt` berada di paket `ui/screen/`, bukan `ui/viewmodel/`.

---

### `[DEF-LEG-005]` — `[LEGACY]` Modul OCR Terputus & Analisis Dokumen AI Masih Placeholder

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `NavGraph.kt`, `DashboardScreen.kt`, `OcrPreviewScreen.kt`, `OcrViewModel.kt`, `MlKitRepository.kt`
- **Severity (Dampak Teknis):** High
- **Priority (Urgensi Waktu):** P1-High *(Cakupan Utama Pengembangan Tahap 1)*
- **Status:** **CLOSED** *(Tuntas terintegrasi di Tahap 1 & terverifikasi via `testDebugUnitTest lintDebug assembleDebug` pada 11/11/2026)*

#### Kondisi Aktual yang Ditemukan:
- `OcrPreviewScreen` belum didaftarkan di `NavGraph.kt`.
- Aksi pindai dokumen pada `DashboardScreen` langsung menuju `SuccessScreen` tanpa melewati pratinjau teks, edit/review multi-halaman, maupun tombol ringkasan AI.
- Dua kartu di `DashboardScreen` (`"Analisis Dokumen"` & `"Hasil Analisis Dokumen"`) masih berupa `ComingSoonCard` berlabel `(Tahap Pengembangan)` yang hanya memunculkan dialog informasi.
- `OcrPreviewScreen` menyimpan `var textFieldValue` lokal tanpa sinkronisasi ulang jika state `extractedTexts` diperbarui dari luar, serta memperbarui ViewModel di setiap ketikan.
- Pipeline OCR & Rule-Based Inference dari dokumentasi penelitian (`metopen/`) belum diintegrasikan ke codebase Android `app/`.

#### Resolusi Tahap 1:
- Menghubungkan rute `AskVela`, `OcrPreview`, dan `DocumentSummary` di `NavGraph.kt`, menghapus placeholder `ConverterScreen.kt`, mengubah Bottom Bar menjadi `Beranda`, `Scan`, `ASK Vela`, `Settings`, memperbaiki `remember(textData.pageNumber, textData.rawText)` di `OcrPreviewScreen.kt`, mengaktifkan tombol **"Ringkasan AI (Offline)"** / **"Edit OCR"** pada `EditImageScreen`, `ImageToPdfScreen`, dan dialog pasca-scan `DashboardScreen`.

---

### `[DEF-LEG-006]` — `[LEGACY]` Teks Versi Aplikasi Hardcoded Tidak Konsisten

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `ui/screen/DashboardScreen.kt` (Drawer), `app/build.gradle.kts`
- **Severity (Dampak Teknis):** Low
- **Priority (Urgensi Waktu):** P3-Low
- **Status:** **CLOSED** *(Teratasi di Tahap 1 & lulus `lintDebug`/`assembleDebug` pada 11/11/2026)*

#### Kondisi Aktual & Resolusi:
Drawer `DashboardScreen.kt` sebelumnya menampilkan `"VelaPDF v1.0.0"`, sekarang membaca `packageManager.getPackageInfo(context.packageName, 0).versionName` secara dinamis sehingga sinkron dengan `"1.3 (Beta Revision 3)"`.

---

### `[DEF-LEG-007]` — `[LEGACY]` Ketiadaan Cakupan Pengujian Otomatis Bermakna

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `app/src/test/java/com/njagakneai/velapdf/`
- **Severity (Dampak Teknis):** High
- **Priority (Urgensi Waktu):** P1-High
- **Status:** **CLOSED** *(11/11 Unit Test JVM PASS termasuk skenario `SK-01..SK-05` & Buku IoT Hal. 23–35 pada 11/11/2026)*

#### Resolusi Tahap 1:
Menambahkan `TestAssetProvider.kt`, `NlpLexiconAndPreprocessorTest.kt` (`2/2 PASS`), `SpoPatternAndRuleEngineTest.kt` (`6/6 PASS`), dan `FactBasedSummarizerTest.kt` (`2/2 PASS`) bersama `ExampleUnitTest.kt` (`1/1 PASS`) sehingga total **`11/11` unit test lulus 100%** menggunakan 4 berkas JSON aset asli.

---

### `[DEF-LEG-008]` — `[LEGACY]` Higienitas Repositori, Duplikat Konfigurasi & Deprekasi Gradle/Kotlin

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `app/gs.json`, `app/google-services.json`, `.gitignore`, `gradle.properties`, `AndroidManifest.xml`, `PermissionHelper.kt`, root crash logs
- **Severity (Dampak Teknis):** Medium
- **Priority (Urgensi Waktu):** P2-Medium *(Sebagian diajukan dalam Rencana Tahap 0)*
- **Status:** **OPEN**

#### Kondisi Aktual yang Ditemukan:
1. `app/gs.json` (untracked) identik dengan `app/google-services.json` (SHA-256 prefix `d6b67a288c4b`, `project_id = comnjagakneaivelapdf`).
2. `.gitignore` menonaktifkan (komentar `#`) aturan untuk `*.jks`, `*.keystore`, dan `google-services.json`.
3. Berkas crash log JVM (`hs_err_pid23324.log`, `hs_err_pid23732.log`, `replay_pid23732.log`) serta folder `velapdf_template/node_modules/` ada di workspace root.
4. `AndroidManifest.xml` dan `PermissionHelper.kt` meminta `READ_MEDIA_VIDEO` meskipun fitur aplikasi berfokus pada dokumen PDF & gambar.
5. `gradle.properties` memicu 6 peringatan deprekasi AGP 9.4.1 / AGP 10 (`android.disallowKotlinSourceSets=false`, `android.usesSdkInManifest.disallowed=false`, `android.sdk.defaultTargetSdkToCompileSdkIfUnset=false`, `android.enableAppCompileTimeRClass=false`, `android.defaults.buildfeatures.resvalues=true`, `android.enableJetifier=true`).
6. Kompilasi Kotlin menghasilkan 17 warning deprekasi API Compose/Android (`hiltViewModel` paket lama `androidx.hilt.navigation.compose`, `Icons.Filled.*`, `statusBarColor`/`navigationBarColor`, `ExposedDropdownMenu` `menuAnchor()`, dan `Bitmap.CompressFormat.WEBP`).

---

### `[DEF-LEG-009]` — `[LEGACY]` 2 Error Android Lint Bawaan Memblokir `./gradlew lintDebug`

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `HistoryScreen.kt:98`, `AndroidManifest.xml:5`
- **Lingkungan Uji:** `.\gradlew.bat lintDebug --no-daemon`
- **Severity (Dampak Teknis):** High *(Memblokir wajib quality gate `lintDebug`)*
- **Priority (Urgensi Waktu):** P1-High
- **Status:** **CLOSED** *(Surgical 2-baris selesai & terverifikasi `0 errors` via `lintDebug` pada 11/11/2026)*

#### Kondisi Aktual yang Ditemukan:
`lintDebug` gagal dengan ringkasan `2 errors, 46 warnings, 1 hint`:
1. `HistoryScreen.kt:98` (`[NonObservableLocale]`): `Locale.getDefault()` dipanggil di dalam Composable (`LocalLocale.current.platformLocale` direkomendasikan Compose UI).
2. `AndroidManifest.xml:5` (`[PermissionImpliesUnsupportedChromeOsHardware]`): `<uses-permission android:name="android.permission.CAMERA" />` tanpa `<uses-feature android:name="android.hardware.camera" android:required="false" />`.

---

## 4. Riwayat Arsip Defect yang Terselesaikan (Closed Defects)

### `[DEF-LEG-009]` — Perbaikan 2 Error Android Lint Bawaan (RESOLVED — Tahap 0)
- **Tanggal Penyelesaian:** 2026-11-11 (`Batch 2`, verifikasi pasca-edit di `task-330`)
- **Resolusi:**
  1. [`HistoryScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/HistoryScreen.kt#L98): mengganti `Locale.getDefault()` dengan `androidx.compose.ui.platform.LocalLocale.current.platformLocale`.
  2. [`AndroidManifest.xml`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/AndroidManifest.xml#L5): menambahkan `<uses-feature android:name="android.hardware.camera" android:required="false" />`.
- **Bukti Verifikasi Ulang (Retest):** `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` -> `BUILD SUCCESSFUL in 3m 17s` (exit code `0`); `lint-results-debug.txt`: **`0 errors, 46 warnings, 1 hint`** (`NonObservableLocale` & `PermissionImpliesUnsupportedChromeOsHardware` = `[]` di SARIF); `TEST-com.njagakneai.velapdf.ExampleUnitTest.xml`: `tests="1" skipped="0" failures="0" errors="0"`.

### `[DEF-LEG-004]` — Pembersihan 5 Berkas Stub Kotlin Mati Tanpa Referensi (RESOLVED — Tahap 0)
- **Tanggal Penyelesaian:** 2026-11-11 (`Batch 1`, verifikasi kompilasi di `task-330`)
- **Resolusi:** Menghapus permanen 5 berkas stub mati (`com.njagakneai.velapdf.MainActivity.kt`, `di/AppModule.kt`, `domain/PdfConverterEngine.kt`, `ui/viewmodel/MainViewModel.kt`, `utils/PermissionUtils.kt`) beserta sampah root (`app/gs.json`, `hs_err_pid*.log`, `replay_pid*.log`, `velapdf_template/`). Sisa komponen placeholder `ConverterScreen.kt` (arahan tab bawah `"Convert"`) & penempatan `PermissionsViewModel.kt` dicatat di [`batasan_masalah.md`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/aturan/Docs/batasan_masalah.md#L44-L49) untuk penyelesaian di Tahap 1.
- **Bukti Verifikasi Ulang (Retest):** `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon` (`task-330`) -> `BUILD SUCCESSFUL in 3m 17s` tanpa broken import atau missing symbol.

### `[DEF-STAGE2-001]` — Penyelesaian Celah Ilmiah `A–D` Pipeline 4-JSON & NLP (RESOLVED — Tahap 2)
- **Tanggal Penyelesaian:** 2026-11-12
- **Resolusi:**
  1. **Celah A:** Mendefinisikan penghasilan fakta sintesis `Dokumen -> mengindikasikanKonsep : <ConceptId>` di `OntologyLoader.kt` serta menambahkan konsep `ArtikelJurnalIlmiah`, pola SPO riset (`spo_pattern_dictionary.json`), dan aturan `RULE_14`–`RULE_16` (`production_rules.json`).
  2. **Celah B:** Menambahkan `preservedNegationAndModalityWords` (`tidak`, `bukan`, `belum`, `jangan`, `tanpa`, `wajib`, `dilarang`, `harus`, `dilarang keras`) di `nlp_lexicon_id.json` & pengawal negasi pada `NlpTextPreprocessor.kt` / `OntologyLoader.kt` agar klausa `"tidak wajib"` tidak tertukar dengan `"wajib"`.
  3. **Celah C:** Menambahkan `spoPropertyMappings` pada `document_ontology.json` & resolusi binding eksplisit `ConceptId.propertyName` + penghitung `missingRequiredProperties`.
  4. **Celah D:** Memisahkan *Document Summarization* (`DocumentSummary.contentSummary`, `sectionSummaries`) dengan *Rule-Based Key Points* (`ruleKeyPoints`) pada domain model, `DocumentAnalysisRepository`, `DocumentSummaryScreen`, dan `SummaryPdfExporter`.
- **Bukti Verifikasi Ulang:** `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest --no-daemon` -> `BUILD SUCCESSFUL`, **`20/20` unit test JVM PASS**, plus **`3/3` androidTest PASS** di emulator offline (`Tes_untuk_Real_Phone`).

### `[DEF-STAGE2-002]` — Perbaikan `UseKtx` URI pada `AskVelaScreen.kt` (RESOLVED — Tahap 2)
- **Tanggal Penyelesaian:** 2026-11-12
- **Resolusi:** Menggunakan ekstensi `androidx.core.net.toUri()` di [`AskVelaScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/AskVelaScreen.kt#L360) sesuai standar Kotlin Android KTX.
- **Bukti Verifikasi Ulang:** `lint-results-debug.txt`: `0 errors, 47 warnings, 1 hint`.

---

## 5. Observasi QA Visual & Kualitas Teks Tahap 02 (Diselesaikan di Tahap 03)

Seluruh temuan visual dan akurasi teks berikut telah ditutup & diverifikasi ulang pada **Tahap 03** (`feature/03_uji_perangkat_fisik_dan_polish`, tanggal `2026-11-12`):
1. **`[DEF-STAGE3-001]` / `[OBS-UI-001]` — RESOLVED (Kontras UI & Placeholder Search):** Warna ikon di [`AskVelaScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/AskVelaScreen.kt), pill progress di [`AnalysisProgressOverlay.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/components/AnalysisProgressOverlay.kt), serta hero card & pill kompresi di [`DocumentSummaryScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/DocumentSummaryScreen.kt) ditingkatkan ke putih/biru muda kontras tinggi; placeholder pencarian diringkas ke `"Cari judul, topik, atau jenis..."`. **Bukti:** [`tahap3/01..07`](Evidence/SS/valid/tahap3/01_ask_vela_empty_fixed.png).
2. **`[DEF-STAGE3-002]` / `[OBS-NLP-001]` — RESOLVED (Pemadatan Dokumen Pendek):** [`FactBasedSummarizer.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/domain/summary/FactBasedSummarizer.kt) menggunakan `targetSummaryPoints` proporsional dan narasi eksekutif ringkas untuk dokumen `< 220` kata (`<= 2` hal.). **Bukti:** `SK-01..SK-05` naik dari `0.0%` menjadi **`38.0% – 58.0%`** (`SK-01` terverifikasi UI **`49.4% (154 -> 78 kata)`**).
3. **`[DEF-STAGE3-003]` / Paket 1B — RESOLVED (Hybrid `iText7` + `AndroidSafeXmlParserFactory` + Pembersih Teks):** [`TextExtractionCleaner.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/domain/nlp/TextExtractionCleaner.kt), [`MlKitRepository.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/data/ocr/MlKitRepository.kt), dan [`NlpTextPreprocessor.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/domain/nlp/NlpTextPreprocessor.kt) mengaktifkan ekstraksi native `iText7` presisi tinggi dengan fallback ML Kit OCR otomatis hanya untuk halaman citra (`Roadmap.pdf` hal. 11–16 -> `HYBRID`), ditambah `AndroidSafeXmlParserFactory` untuk mengatasi `UnsupportedOperationException` pada `DocumentBuilderFactory.setXIncludeAware` di runtime Android. **Bukti:** `SK-01` nomor `112/SK/UNP-KD/X/2026` utuh sempurna & benchmark 17 PDF selesai dalam **`29.467s`** (`26/26` unit test + `3/3` androidTest PASS).
4. **`[OBS-KHS-001]` Perilaku Dokumen Non-Domain (`KHS1..5`, `Transkip1..2`):** Berjalan aman (`NATIVE_ITEXT7`, `0` crash) sesuai batasan penghapusan modul KHS (`ADR-001`).

---

## 6. Hotfix UAT Perangkat Fisik (Branch `fix/pdf-to-image-overlap-and-history-back`)

Temuan uji langsung oleh **Yogi Ario (`@useripx`)** di **Infinix Note 30** pada `2026-11-12` telah diperbaiki dan diverifikasi ulang tanpa mengubah versi rilis ataupun memasuki Tahap 04:
1. **`[DEF-FIX-001]` — RESOLVED (Layout Bawah `PdfToImageScreen.kt` Terpotong/Tumpang Tindih):**
   - Menambahkan `rememberScrollState()`, `.verticalScroll(...)`, dan `.imePadding()` pada `Column` isi utama [`PdfToImageScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/PdfToImageScreen.kt#L284-L358) serta merapikan spasi vertikal (`vertical = 16.dp`, `top = 16.dp, bottom = 8.dp`, `Spacer(16.dp)`).
   - **Bukti:** [`fix/02_pdf_to_image_with_zip_fixed.png`](Evidence/SS/valid/fix/02_pdf_to_image_with_zip_fixed.png) dan mode gelap + skala font `1.25x` [`fix/03_pdf_to_image_large_font_scrolled.png`](Evidence/SS/valid/fix/03_pdf_to_image_large_font_scrolled.png).
2. **`[DEF-FIX-002]` — RESOLVED (Tombol Kembali ke Beranda pada `HistoryScreen.kt`):**
   - Menambahkan `navigationIcon` (`Icons.AutoMirrored.Filled.ArrowBack`, `contentDescription = "Kembali ke Beranda"`) di `TopAppBar` serta tombol `OutlinedButton("Kembali ke Beranda")` pada kondisi riwayat kosong di [`HistoryScreen.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/screen/HistoryScreen.kt#L35-L66), dan memastikan [`NavGraph.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/ui/navigation/NavGraph.kt#L214-L220) mem-pop stack ke `Screen.Dashboard.route`.
   - **Bukti:** [`fix/01_history_back_button.png`](Evidence/SS/valid/fix/01_history_back_button.png) & verifikasi UI tree kembali ke Dashboard (`Beranda`).
