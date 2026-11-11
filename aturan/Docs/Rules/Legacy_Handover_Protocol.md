# PROTOKOL MELANJUTKAN PENGEMBANGAN DARI PENGEMBANG SEBELUMNYA
## (LEGACY / BROWNFIELD HANDOVER GOVERNANCE PROTOCOL)

> **DOCUMENT ID:** RUL-HND-BRN-001  
> **VERSION:** v1.0.0  
> **STATUS:** AKTIF (LEVEL 3 HANDOVER & CONTINUATION RULES)  
> **SUMBER ATURAN UTAMA (LEVEL 1):** [`Docs/universal-rule.md`](../universal-rule.md)  
> **INDUK OPERASIONAL (LEVEL 3):** [`AGENTS.md`](../../AGENTS.md)  
> **ATURAN PLATFORM TERKAIT:** [`Docs/Rules/Android_Kotlin_MVVM_Rules.md`](Android_Kotlin_MVVM_Rules.md)  
> **DESIGNATED APPROVER / PROJECT OWNER:** Rujuk [`config/organization.md`](../../config/organization.md) *(Yogi Ario)*

---

## 1. Tujuan & Filosofi Handover

Proyek ini adalah aplikasi yang **melanjutkan pengembangan dari pengembang sebelumnya (*brownfield / handover codebase*)**. Kesalahan paling berbahaya pada proyek lanjutan adalah AI Agent menganggap repositori seperti proyek kosong, lalu menulis ulang kode lama atau mencampur pola arsitektur hingga merusak fitur yang sudah jalan.

**Prinsip Utama:**  
> ***"Pahami dan petakan seluruh warisan kode terlebih dahulu secara Read-Only sebelum mengusulkan perubahan apa pun."***

---

## 2. Fase 0 Wajib: Audit Baseline Codebase (Strictly Read-Only)

Sebelum menyusun rencana fitur baru di [`Docs/Implementation_plan.md`](../Implementation_plan.md) dan **MUST NOT** mengubah satu baris kode aplikasi pun, AI Agent **MUST** menuntaskan Audit Baseline Fase 0 dengan memeriksa:

1. **Konfigurasi Build & Versi SDK:**
   - Baca `settings.gradle` / `settings.gradle.kts`, root & module `build.gradle` / `build.gradle.kts`, `gradle/libs.versions.toml` (jika ada), serta `gradle/wrapper/gradle-wrapper.properties`.
   - Catat: Versi AGP, Versi Kotlin, `compileSdk`, `targetSdk`, `minSdk`, *Application ID*, *Flavor/BuildTypes*, dan daftar seluruh library pihak ketiga.
2. **Manifest, Izin & Entry Point:**
   - Periksa `AndroidManifest.xml` (izin `uses-permission`, `Activity` launcher, `Service`, `Receiver`, konfigurasi jaringan `networkSecurityConfig` / `usesCleartextTraffic`).
3. **Pemetaan Implementasi MVVM Pengembang Sebelumnya:**
   - Telusuri struktur paket pada `app/src/main/java` (atau `kotlin/`): identifikasi lokasi *Views*, *ViewModels*, *Models/Entities*, *Repositories*, *Network/API*, *Local DB (Room/SQLite)*, dan *DI (Hilt/Koin/Dagger/Manual)*.
   - Identifikasi konvensi yang dipakai pengembang lama:
     - **UI Toolkit:** XML Views (*ViewBinding*/*DataBinding*) atau Jetpack Compose?
     - **State Holder:** `StateFlow`/`SharedFlow` atau `LiveData`?
     - **Async Engine:** Kotlin Coroutines, RxJava, atau Callback?
     - **Networking & Serialization:** Retrofit/Ktor/Volley + Gson/Moshi/Kotlinx.Serialization?
4. **Audit Keamanan Warisan & Kebersihan Git:**
   - Periksa riwayat Git (`git branch -a`, `git status`, `git log -n 20 --oneline`).
   - Periksa apakah ada kredensial, URL rahasia, `keystore`, atau API key yang telanjur *hardcoded* atau ter-commit oleh pengembang sebelumnya. Jika ditemukan, **segera laporkan kepada Yogi Ario** *(jangan diam-diam menghapus histori Git tanpa perintah)*.
5. **Uji Kesehatan Build Awal (*Baseline Build & Test Check*):**
   - Uji apakah repositori bawaan saat ini dapat dikompilasi dan diuji dengan perintah baseline:
     - `./gradlew assembleDebug`
     - `./gradlew testDebugUnitTest`
   - Catat hasil nyatanya (lulus bersih, ada warning deprecated, atau memang sudah gagal sejak dari pengembang sebelumnya).

Seluruh hasil langkah 1–5 **MUST** dituangkan secara objektif ke [`Docs/Project_Analysis.md`](../Project_Analysis.md) dan disajikan kepada Yogi Ario sebelum masuk ke tahap implementasi fitur baru.

---

## 3. Aturan Ketat Saat Memodifikasi Kode Warisan

### 3.1 Larangan *Mass Refactoring* ("Anti-Boy Scout Trap")
- AI Agent **MUST NOT** menulis ulang, mengganti nama paket (*package rename*), memindahkan berkas ke struktur folder baru, atau memformat ulang berkas milik pengembang sebelumnya yang **berfungsi normal dan berada di luar ruang lingkup tugas** (*Don't touch unbroken adjacent code*).
- Jika kode lama terasa berantakan atau memiliki *code smell*, **JANGAN diubah diam-diam**. Catat sebagai temuan utang teknis di [`Docs/Project_Analysis.md`](../Project_Analysis.md) atau ajukan proposal terpisah pada [`Docs/Implementation_plan.md`](../Implementation_plan.md) jika ingin dirapikan nanti.

### 3.2 Larangan Pencampuran Paradigma (*No Architecture Mixing*)
- AI Agent **MUST** mengikuti gaya kode, konvensi penamaan, pembungkus state, dan UI toolkit yang sudah dominan di modul terkait.
- **Dilarang keras** melakukan salah satu dari hal berikut tanpa ADR tertulis di [`Docs/Decision_Log.md`](../Decision_Log.md) dan **persetujuan eksplisit Yogi Ario**:
  - Memasang framework DI baru saat proyek sudah memakai Hilt/Koin/Dagger.
  - Menyisipkan layar Jetpack Compose baru ke dalam aplikasi XML murni (atau sebaliknya).
  - Mengganti library HTTP/JSON/Image Loader eksisting (misal memaksakan Ktor/Coil saat aplikasi penuh memakai Retrofit/Glide).
  - Mengubah skema Room DB secara destruktif (`.fallbackToDestructiveMigration()`).

### 3.3 Isolasi Bug Bawaan vs Bug Baru
- Jika selama audit atau pengerjaan ditemukan bug yang memang sudah ada dari kode pengembang sebelumnya:
  1. Catat temuan tersebut pada [`Docs/Defect_Log.md`](../Defect_Log.md) dengan prefix identifikasi asal (misal: `[LEGACY] DEF-LEG-001`).
  2. Laporkan kepada Yogi Ario: apakah bug bawaan tersebut perlu langsung diperbaiki dalam batch saat ini atau ditunda ke task tersendiri.
  3. **MUST NOT** menyamarkan bug bawaan maupun memperluas ruang lingkup perubahan tanpa persetujuan.

### 3.4 Integrasi Fitur Baru secara Aman (*Strangler / Extension Pattern*)
- Saat menambahkan fitur baru pada proyek lama yang rapuh, utamakan membuat **modul/kelas/komponen baru yang bersih dan teruji (*unit tested*)** sesuai pola MVVM, lalu menghubungkannya ke alur lama melalui titik integrasi seminimal mungkin agar risiko regresi pada fitur pengembang lama mendekati nol.

---

## 4. Checklist Gerbang Keluar Fase 0 (*Phase 0 Sign-Off*)

Sebelum AI Agent mengajukan *Implementation Plan* Tahap 1 untuk fitur baru atau perbaikan bug, pastikan kondisi berikut terpenuhi:

- [ ] Repositori telah diamankan ke branch kerja terpisah (bukan bekerja langsung di `main`).
- [ ] Seluruh modul, `build.gradle*`, `AndroidManifest.xml`, dan struktur MVVM lama telah diinventarisasi di [`Docs/Project_Analysis.md`](../Project_Analysis.md).
- [ ] Konfirmasi teknis telah didapatkan atau dipetakan: UI Toolkit (XML vs Compose), DI framework, State holder (`StateFlow` vs `LiveData`), SDK versions, dan database lokal.
- [ ] Status baseline build (`assembleDebug` & `testDebugUnitTest`) telah dicatat secara faktual beserta daftar bug bawaan (jika ada) di [`Docs/Defect_Log.md`](../Defect_Log.md).
- [ ] Hasil audit potensi *secret/keystore leakage* dari developer lama telah diperiksa dan dilaporkan ke Yogi Ario.
- [ ] Yogi Ario telah meninjau hasil analisis Fase 0 dan menyetujui arah rencana kerja Tahap 1.
