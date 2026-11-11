# Defect & Bug Log — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** PELACAK CACAT & BUG AKTIF (ACTIVE DEFECT TRACKER)  
> **DESIGNATED APPROVER:** Rujuk [config/organization.md](../config/organization.md) (`Yogi Ario` / `@useripx`)  
> **PENANGGUNG JAWAB QA / AUDIT:** Carl (`AGT-PRD-DEV-001`)  
> **TERAKHIR DIPERBARUI:** 2026-11-11 09:33 WIB  
> **ACUAN STANDAR:** [Docs/Standards/Defect_Management.md](Standards/Defect_Management.md), [Docs/Standards/QA_Framework.md](Standards/QA_Framework.md), & [Docs/Rules/Legacy_Handover_Protocol.md](Rules/Legacy_Handover_Protocol.md)  
> **PRINSIP UTAMA:** *Defect tidak boleh ditutup hanya karena kode telah diubah; wajib diverifikasi ulang (Retesting PASS).*

---

## 1. Ringkasan Status Defect

| Total Defect Terdeteksi | Open (Menunggu Perbaikan) | In Progress / Retesting | Closed (Tuntas Terverifikasi) |
| :---: | :---: | :---: | :---: |
| **9** | **7** | **0** | **2** |

*(Catatan: Seluruh temuan awal pada tabel di bawah bertanda `[LEGACY]` hasil Audit Fase 0 Read-Only sesuai [Legacy_Handover_Protocol.md](Rules/Legacy_Handover_Protocol.md). Perbaikan hanya dieksekusi bila relevan dengan fitur aktif atau disetujui secara eksplisit oleh Yogi Ario.)*

---

## 2. Tabel Indeks Defect

| Defect ID | Tanggal | Modul | Judul Cacat / Masalah | Severity | Priority | Status | Verifikasi Akhir |
| :---: | :---: | :--- | :--- | :---: | :---: | :---: | :---: |
| **`DEF-LEG-001`** | 2026-11-11 | `PdfToImage` & `Utils` | `[LEGACY]` Penggunaan operator non-null assertion `!!` (11 titik berisiko `NullPointerException`) | Medium | P2-Medium | **OPEN** | Menunggu |
| **`DEF-LEG-002`** | 2026-11-11 | `PdfToImage` & `FileUriHelper` | `[LEGACY]` Penggunaan `Uri.fromFile(...)` berisiko `FileUriExposedException` saat berbagi/buka berkas | Medium | P2-Medium | **OPEN** | Menunggu |
| **`DEF-LEG-003`** | 2026-11-11 | `ViewModels` & `MlKitRepository` | `[LEGACY]` Operasi I/O disk/bitmap & decode URI berjalan di luar `Dispatchers.IO` | High | P1-High | **OPEN** | Menunggu |
| **`DEF-LEG-004`** | 2026-11-11 | `App` / `DI` / `Navigation` | `[LEGACY]` 5 berkas stub mati kosong (`MainActivity` template, `AppModule`, `PdfConverterEngine`, `MainViewModel`, `PermissionUtils`) — *catatan: placeholder `ConverterScreen` dijadwalkan di Tahap 1* | Low | P3-Low | **CLOSED** | 2026-11-11 (Retest PASS — Tahap 0) |
| **`DEF-LEG-005`** | 2026-11-11 | `OCR` & `Dashboard` | `[LEGACY]` Modul OCR (`OcrPreviewScreen`/`OcrViewModel`) terputus dari `NavGraph` & Analisis AI masih placeholder | High | P1-High | **OPEN** | Menunggu |
| **`DEF-LEG-006`** | 2026-11-11 | `DashboardScreen` | `[LEGACY]` Teks versi aplikasi hardcoded (`VelaPDF v1.0.0`) tidak selaras dengan Gradle `1.3 (Beta Revision 3)` | Low | P3-Low | **OPEN** | Menunggu |
| **`DEF-LEG-007`** | 2026-11-11 | `app/src/test` | `[LEGACY]` Cakupan unit test praktis kosong (hanya 1 dummy test `2 + 2 == 4` di `ExampleUnitTest.kt`) | High | P1-High | **OPEN** | Menunggu |
| **`DEF-LEG-008`** | 2026-11-11 | `Build` / `Git` / `Manifest` | `[LEGACY]` Duplikat `app/gs.json`, `.gitignore` longgar, crash log di root, izin `READ_MEDIA_VIDEO`, & warning deprekasi AGP/Kotlin | Medium | P2-Medium | **OPEN** | Menunggu |
| **`DEF-LEG-009`** | 2026-11-11 | `HistoryScreen` & `Manifest` | `[LEGACY]` 2 Error Lint bawaan memblokir `./gradlew lintDebug` (`NonObservableLocale` & `PermissionImpliesUnsupportedChromeOsHardware`) | High | P1-High | **CLOSED** | 2026-11-11 (Retest PASS — Tahap 0) |

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
- **Status:** **OPEN**

#### Kondisi Aktual yang Ditemukan:
- `OcrPreviewScreen` belum didaftarkan di `NavGraph.kt`.
- Aksi pindai dokumen pada `DashboardScreen` langsung menuju `SuccessScreen` tanpa melewati pratinjau teks, edit/review multi-halaman, maupun tombol ringkasan AI.
- Dua kartu di `DashboardScreen` (`"Analisis Dokumen"` & `"Hasil Analisis Dokumen"`) masih berupa `ComingSoonCard` berlabel `(Tahap Pengembangan)` yang hanya memunculkan dialog informasi.
- `OcrPreviewScreen` menyimpan `var textFieldValue` lokal tanpa sinkronisasi ulang jika state `extractedTexts` diperbarui dari luar, serta memperbarui ViewModel di setiap ketikan.
- Pipeline OCR & Rule-Based Inference dari dokumentasi penelitian (`metopen/`) belum diintegrasikan ke codebase Android `app/`.

---

### `[DEF-LEG-006]` — `[LEGACY]` Teks Versi Aplikasi Hardcoded Tidak Konsisten

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `ui/screen/DashboardScreen.kt` (Drawer), `app/build.gradle.kts`
- **Severity (Dampak Teknis):** Low
- **Priority (Urgensi Waktu):** P3-Low
- **Status:** **OPEN**

#### Kondisi Aktual yang Ditemukan:
`DashboardScreen.kt` menampilkan teks statis `"VelaPDF v1.0.0"`, sedangkan `app/build.gradle.kts` menetapkan `versionCode = 3` dan `versionName = "1.3 (Beta Revision 3)"`.

---

### `[DEF-LEG-007]` — `[LEGACY]` Ketiadaan Cakupan Pengujian Otomatis Bermakna

- **Tanggal Dilaporkan:** 2026-11-11
- **Pelapor:** Carl (`AGT-PRD-DEV-001`) — Audit Fase 0
- **Modul / Komponen Terkait:** `app/src/test/java/com/njagakneai/velapdf/ExampleUnitTest.kt`
- **Severity (Dampak Teknis):** High
- **Priority (Urgensi Waktu):** P1-High
- **Status:** **OPEN**

#### Kondisi Aktual yang Ditemukan:
Hanya terdapat 1 test dummy (`addition_isCorrect`: `assertEquals(4, 2 + 2)`) di `testDebugUnitTest`. Belum ada pengujian otomatis untuk ViewModel, Repository, engine PDF, pre-processing NLP, maupun Rule-Based Inference Engine.

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
