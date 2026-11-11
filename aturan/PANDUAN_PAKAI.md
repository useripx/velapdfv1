# PANDUAN PENGGUNAAN TEMPLATE PROYEK ANDROID KOTLIN (MVVM — HANDOVER)

> **DOKUMEN INI ADALAH PETUNJUK MEMULAI (QUICK START GUIDE)**  
> Disiapkan khusus untuk **Yogi Ario (Project Owner)** guna melanjutkan pengembangan aplikasi mobile **Android Kotlin (MVVM)** warisan pengembang sebelumnya secara aman, rapi, dan teraudit.

---

## 1. Cara Aman Memasang Folder Ini ke Proyek Kotlin Lama

Berbeda dengan proyek baru dari nol, proyek lanjutan **sudah memiliki kode sumber dan konfigurasi bawaan** dari pengembang sebelumnya. Ikuti langkah aman berikut saat melakukan Copy-Paste isi folder `pakai-kotlin/`:

1. **Amankan Cabang Git Proyek Lama Terlebih Dahulu:**  
   Buka terminal di root folder proyek Kotlin Anda, pastikan status bersih, lalu buat cabang khusus persiapan:
   ```bash
   git status
   git checkout -b feature/00_governance_and_audit
   ```
2. **Copy-Paste Berkas Baru ke Root Proyek Android Anda:**  
   Salin folder dan berkas berikut langsung ke root proyek Android Anda (sejajar dengan `app/`, `build.gradle*`, dan `settings.gradle*`):
   - `AGENTS.md`
   - `GEMINI.md`
   - `config/`
   - `agents/`
   - `Docs/` *(memuat `Docs/Rules/Android_Kotlin_MVVM_Rules.md` dan `Docs/Rules/Legacy_Handover_Protocol.md`)*
   - `skills/`
   - `changelog/`
   - `.env.example` *(bila belum ada)*
3. **Hati-hati Saat Menyalin `.gitignore` dan `README.md` (MERGE, JANGAN TIMPA BUTA!):**
   - Jika proyek lama **sudah punya `.gitignore`**, jangan langsung *Replace/Overwrite*! Buka `.gitignore` lama Anda dan **tambahkan** aturan perlindungan dari `pakai-kotlin/.gitignore` (terutama bagian `local.properties`, `.gradle/`, `*.jks`, `*.keystore`, `keystore.properties`, dan `Docs/universal-rule`).
   - Jika proyek lama **sudah punya `README.md`**, pertahankan catatan setup penting dari developer sebelumnya, lalu gabungkan panduan tata kelola dan perintah `./gradlew` dari `pakai-kotlin/README.md`.

---

## 2. Isi Identitas Singkat di `AGENTS.md`

Buka `AGENTS.md` di proyek Anda, lalu lengkapi bagian yang masih bertanda `[Isi Manual: ...]` pada **Bagian 1** (Nama Aplikasi Android, Workspace Lokal, dan Tujuan Pengembangan Lanjutan). Bagian bertanda `[Audit di Fase 0]` boleh dibiarkan agar diisi otomatis oleh AI Agent saat melakukan audit kode pengembang lama.

---

## 3. Prompt ke AI Agent (Fase 0 Audit & Implementation Plan Dapat Dikomentari)

### A. Prompt Pertama (Wajib Fase 0 Audit Read-Only)
Saat membuka proyek Android Kotlin Anda bersama AI Agent (Antigravity, Gemini, Claude Code, Cursor, dll.), **jangan langsung menyuruh agen ngoding fitur baru**. Kirimkan prompt berikut agar agen melakukan pemetaan warisan kode terlebih dahulu:

> *"Halo! Proyek ini adalah kelanjutan aplikasi Android Kotlin (MVVM) dari pengembang sebelumnya. Tolong baca `AGENTS.md`, `Docs/universal-rule.md`, `Docs/Rules/Android_Kotlin_MVVM_Rules.md`, dan `Docs/Rules/Legacy_Handover_Protocol.md`.  
> Sekarang jalankan **FASE 0 (Audit Read-Only)** secara tuntas: periksa `settings.gradle*`, `build.gradle*`, `libs.versions.toml`, `AndroidManifest.xml`, struktur paket MVVM di `app/src/`, UI toolkit yang dipakai (XML vs Compose), DI, state management (`StateFlow` vs `LiveData`), database Room, potensi secret/API key yang hardcoded, serta cek baseline build (`./gradlew assembleDebug` dan `./gradlew testDebugUnitTest`).  
> Catat seluruh temuan di `Docs/Project_Analysis.md` dan bug bawaan di `Docs/Defect_Log.md`. **DILARANG keras memodifikasi kode aplikasi apa pun sebelum laporan audit Fase 0 saya tinjau dan setujui.**"*

### B. Prompt Saat Meminta Fitur / Perbaikan Baru (Wajib Plan Bisa Dikomentari)
Setelah Fase 0 selesai, gunakan instruksi ini agar agen menerbitkan *Implementation Plan* yang bisa Anda komentari langsung sebelum eksekusi:

> *"Tolong buatkan **Implementation Plan** untuk [sebutkan fitur/perbaikan bug]. Sesuai aturan khusus Agen AI pada `AGENTS.md` Bagian 2.1 dan `Docs/Implementation_plan.md`, **wajib terbitkan rencana dalam bentuk Artifact interaktif (`RequestFeedback: true` bila IDE mendukung) dan sinkronkan ke `Docs/Implementation_plan.md` lengkap dengan slot komentar agar bisa saya komentari langsung per bagian** (jangan hanya teks chat biasa). Tunggu komentar dan persetujuan saya sebelum menyentuh kode."*

---

## 4. Ringkasan Aturan Khusus yang Sudah Tertanam

| Berkas Aturan | Fungsi Utama Pelindung Proyek Anda |
| :--- | :--- |
| **[`Docs/Rules/Android_Kotlin_MVVM_Rules.md`](Docs/Rules/Android_Kotlin_MVVM_Rules.md)** | Menjaga batas lapisan View–ViewModel–Repository, mengunci versi `compileSdk`/`targetSdk`/Gradle/AGP/Kotlin, mencegah `Memory Leak` & `GlobalScope`, mewajibkan `Room Migration`, serta gate `./gradlew testDebugUnitTest`, `lintDebug`, dan `assembleDebug`. |
| **[`Docs/Rules/Legacy_Handover_Protocol.md`](Docs/Rules/Legacy_Handover_Protocol.md)** | Mewajibkan Audit Fase 0 sebelum coding, melarang *mass refactoring* kode lama yang sudah jalan, melarang campur XML/Compose atau 2 framework DI tanpa persetujuan, dan memisahkan bug bawaan (`[LEGACY]`) dari bug baru. |
| **[`AGENTS.md`](AGENTS.md)** & **[`Docs/Implementation_plan.md`](Docs/Implementation_plan.md)** | Mengatur aturan **khusus Agen AI** agar setiap pembuatan *Implementation Plan* dan *Chained Plan* **wajib diterbitkan dalam format Artifact (`RequestFeedback: true`) dan berkas dokumen berslot komentar** yang dapat Anda komentari langsung sebelum eksekusi kode. |
| **[`.gitignore`](.gitignore)** | Menutup celah kebocoran `.gradle/`, `local.properties`, `*.jks`, `*.keystore`, `keystore.properties`, `.env`, dan file kunci lainnya. |

---

*Jika Anda juga masih membutuhkan template untuk proyek Aplikasi Downloader sebelumnya, folder [`../pakai/`](../pakai/) tetap tersedia secara terpisah dan tidak tertimpa.*
