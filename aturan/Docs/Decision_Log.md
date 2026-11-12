# Decision Log (ADR) — VelaPDF (`com.njagakneai.velapdf`)

> **STATUS DOKUMEN:** CATATAN KEPUTUSAN ARSITEKTUR AKTIF (ACTIVE ARCHITECTURE DECISION RECORD)  
> **DESIGNATED APPROVER:** Yogi Ario (`@useripx` — rujuk [`config/organization.md`](../config/organization.md))  
> **PENYUSUN:** Carl (`AGT-PRD-DEV-001`)  
> **TERAKHIR DIPERBARUI:** 11/11/2026

---

## Indeks Rekaman Keputusan

| ID Keputusan | Tanggal | Topik / Fitur | Keputusan Singkat | Status |
| :---: | :---: | :--- | :--- | :---: |
| **`ADR-001`** | 2026-11-11 | Arsitektur & Fokus Fitur | Pertahankan **MVVM + Clean Architecture** (100% Compose, Hilt, Room `2.8.5`, DataStore); hapus permanen modul KHS | **APPROVED** |
| **`ADR-002`** | 2026-11-11 | Strategi Git, `velapdfv1.git` & Keamanan | Folder `aturan/` **mentok di `develop`** (`main` bersih 100%); `app/google-services.json` di-`.gitignore`; riwayat `velapdfv1.git` bersih | **APPROVED** |
| **`ADR-003`** | 2026-11-11 | Lingkup OCR & AI Offline (`ASK Vela`) | Fokus **Ringkasan & Kesimpulan Bahasa Indonesia** via *Rule-Based Inference (Forward Chaining)* tanpa Q&A interaktif/Cloud LLM | **APPROVED** |
| **`ADR-005`** | 2026-11-12 | Perbaikan Celah NLP/Ontologi (`A–D`) & Manajemen Riwayat | Pemisahan *Document Summarization* vs *Rule Key Points*, pelestarian kata negasi/modalitas, pemetaan eksplisit Ontologi–SPO, dan hapus massal Room chunked `500` tanpa naik versi DB | **APPROVED** |

---

## Rincian Rekaman Keputusan Aktif

### `[ADR-001]` — Pertahankan Stack Eksisting (MVVM + Clean Architecture + Compose + Hilt) & Penghapusan Permanen KHS

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-11
- **Pengusul:** Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Dokumentasi aturan awal (`aturan/`) hanya menyebut MVVM umum dan template lama, sementara codebase VelaPDF memakai **MVVM + Clean Architecture** (`data/`, `domain/`, `di/`, `ui/`, `utils/`) dengan Compose BOM `2026.09.00`, Hilt `2.60.1`, Room `2.8.5`, Kotlin `2.4.20`, AGP `9.4.1`, dan Gradle `9.8.0`. Di samping itu, sisa fitur `KHS` (`PreviewKhsScreen`, `KhsExporter`) telah dihapus di working tree sebelum serah terima.

#### 2. Keputusan & Alasan
- Menjaga 100% arsitektur, versi SDK (`minSdk 26`, `targetSdk 36`, `compileSdk 37`), dan stack Compose/Hilt tanpa perombakan.
- Menyelaraskan dokumen di `aturan/` (`AGENTS.md`, `GEMINI.md`, `Android_Kotlin_MVVM_Rules.md`) agar mengikuti codebase aktual.
- Menghapus modul KHS secara permanen agar pengembangan fokus pada utilitas PDF serta OCR & Analisis Dokumen AI offline.

---

### `[ADR-002]` — Strategi Clean `main` Strict (`aturan/` Mentok di `develop`) & Keamanan `google-services.json`

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-11
- **Pengusul:** Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Repositori lama masih menunjuk `PanjaluPDF.git` dengan 183 perubahan menggantung, crash logs JVM, duplikat `app/gs.json`, serta konfigurasi Firebase `app/google-services.json` di history lama. Pemilik proyek meminta pindah ke repo baru `https://github.com/useripx/velapdfv1.git` dengan kondisi `main` super bersih dan `aturan/` mentok di `develop`.

#### 2. Keputusan & Alasan
- Mengalihkan remote ke `https://github.com/useripx/velapdfv1.git`.
- Menghapus `app/gs.json`, log crash root, `velapdf_template/`, serta 5 stub Kotlin kosong tanpa referensi.
- Mengeluarkan `app/google-services.json` dari index Git sambil mempertahankannya di disk lokal & melindunginya di `.gitignore`.
- Memisahkan fungsi branch: `main` hanya berisi kode proyek Android VelaPDF bersih (**0% `aturan/`**), sedangkan `develop` menyimpan kode integrasi beserta folder `aturan/` (`Docs/`, `alatuji/`, `changelog/`, `config/`).

---

### `[ADR-003]` — Ruang Lingkup Analisis Dokumen AI (`ASK Vela` & `Ringkasan` Bahasa Indonesia Luring)

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-11
- **Pengusul:** Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Terdapat kebutuhan penegasan batas fungsionalitas AI offline agar sesuai dengan arah penelitian (Sempro) dan arsitektur *Rule-Based Inference*.

#### 2. Keputusan & Alasan
- **ASK Vela** dan aksi **Ringkasan** pasca-scan beroperasi **100% offline** via Google ML Kit OCR + NLP + Ontologi JSON + *Forward Chaining*.
- Keluaran berupa **ringkasan poin inti & kesimpulan dalam Bahasa Indonesia saja**, tanpa fitur tanya jawab interaktif (*Q&A*), terdokumentasi resmi di [`batasan_masalah.md`](batasan_masalah.md).


---

## ADR-004: Arsitektur 4-JSON Eksternal, Parser Murni JVM, Shared `@Singleton` Repository StateFlow & Migrasi Eksplisit Room v2

- **Tanggal:** 2026-11-11
- **Status:** Accepted & Implemented (Tahap 01 — `feature/01_ocr_dan_ask_vela`)
- **Konteks:** Prototipe awal pada `aturan/alatuji/01_kode_sumber/` masih mencampur *stopwords*/singkatan di `NlpTextPreprocessor.kt` dan pola regex SPO di `OntologyLoader.kt` secara *hardcoded*, hanya mempunyai 2 berkas JSON skema (`document_ontology.json` dan `production_rules.json`), serta belum memiliki persistent cache hasil analisis di `AppDatabase.kt` v1 (`history_table`). Selain itu, `org.json` bawaan Android SDK tidak tersedia murni di JVM unit test tanpa mocking atau Robolectric, dan teks multi-halaman hasil OCR terlalu besar untuk dilewatkan lewat argumen URI rute Jetpack Navigation.
- **Keputusan:**
  1. **Pemisahan 4 Berkas JSON Eksternal (`app/src/main/assets/`):**
     - `nlp_lexicon_id.json` (732 *stopwords*, 25 singkatan terproteksi, 7 penanda seksi, 33 `domainBoostTerms`).
     - `spo_pattern_dictionary.json` (15 pola ekstraksi fakta SPO berbasis regex + pengayaan pola dokumen & lembaga).
     - `document_ontology.json` (8 kelas konsep resmi & buku ajar/informatif).
     - `production_rules.json` (13 kaidah Forward Chaining `RULE_01` s.d. `RULE_13`).
  2. **Pure-Kotlin Recursive Descent JSON Parser ([`SimpleJsonParser.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/domain/ontology/SimpleJsonParser.kt)) + [`AssetTextProvider`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/di/AnalysisModule.kt):** Menghindari ketergantungan pada `org.json` Android agar seluruh pipeline NLP, SPO, Ontologi, Forward Chaining, dan Summarizer dapat diuji cepat pada JVM unit test menggunakan stub `TestAssetProvider`.
  3. **Shared `@Singleton` [`DocumentAnalysisRepository.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/data/repository/DocumentAnalysisRepository.kt):** Menyimpan `latestOcrPages`, `latestMetadata`, dan `latestSummary` dalam `StateFlow` reaktif yang dibagikan ke `OcrPreviewScreen`, `AskVelaScreen`, dan `DocumentSummaryScreen` tanpa batas panjang rute Navigation Compose.
  4. **Room Database v2 + `MIGRATION_1_2` Eksplisit ([`AppDatabase.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/data/database/AppDatabase.kt), [`DatabaseModule.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/di/DatabaseModule.kt)):** Menambah `analysis_history_table` (`AnalysisHistoryEntity`) melalui SQL `CREATE TABLE IF NOT EXISTS` eksplisit **tanpa** `fallbackToDestructiveMigration()` agar data riwayat pengguna lama di `history_table` tetap utuh.
- **Konsekuensi:**
  - *Positif:* Penambahan kosakata domain atau pola SPO baru ke depan cukup mengedit aset JSON tanpa mengubah arsitektur kode; 11/11 unit test berjalan offline murni di JVM dalam hitungan sub-detik; zero risiko *TransactionTooLargeException* antar layar Compose; zero risiko penghapusan data Room pengguna saat pembaruan aplikasi.
  - *Trade-off:* Skema field baru pada 4 JSON harus mengikuti struktur yang dikenali [`OntologyLoader.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/domain/ontology/OntologyLoader.kt) & [`NlpTextPreprocessor.kt`](file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/src/main/java/com/njagakneai/velapdf/domain/nlp/NlpTextPreprocessor.kt).

---

### `[ADR-005]` — Penyempurnaan Celah NLP/Ontologi (`A–D`) & Manajemen Riwayat Tanpa Migrasi DB

- **Status:** **APPROVED**
- **Tanggal Usulan & Pengesahan:** 2026-11-12 (Tahap 02 Revisi 1)
- **Pengusul:** Yogi Ario (`@useripx`) & Carl (`AGT-PRD-DEV-001`)
- **Penyetuju Resmi:** Yogi Ario (`@useripx`)

#### 1. Konteks Masalah
Selama persiapan Tahap 02, teridentifikasi 4 celah ilmiah pada mesin AI 4-JSON (`A`: asal fakta `mengindikasikanKonsep` & penanda artikel ilmiah; `B`: penghapusan kata negasi/modalitas seperti *tidak, bukan, belum, wajib, dilarang, harus* sebagai *stopword*; `C`: belum adanya pemetaan eksplisit antara properti Ontologi seperti `nomorDokumen` dengan predikat SPO `memilikiNomor`; `D`: bercampurnya *rule-based key-point extraction* dengan ringkasan isi dokumen secara utuh), serta kebutuhan pengguna untuk mencari dan menghapus banyak riwayat tersimpan di `ASK Vela`.

#### 2. Keputusan & Alasan
- **Celah A (`document_ontology.json` + `OntologyLoader.kt` + `spo_pattern_dictionary.json` + `production_rules.json`):** Mendeklarasikan fakta sintesis `Dokumen -> mengindikasikanKonsep : <ConceptId>` secara eksplisit pada hasil analisis konsep ontologi dan menambah kelas `ArtikelJurnalIlmiah` + pola SPO riset (`SPO_RESEARCH_METHOD`, `SPO_ADVANTAGE`, `SPO_LIMITATION`) serta `RULE_14`–`RULE_16`.
- **Celah B (`nlp_lexicon_id.json` + `NlpTextPreprocessor.kt`):** Mengamankan `preservedNegationAndModalityWords` (*tidak, bukan, belum, jangan, tanpa, wajib, dilarang, harus, dilarang keras*) agar tidak terhapus oleh `stopwords` dan memberi penalti pada klausa negasi kewajiban (*"tidak wajib"*) agar tidak memicu aturan kewajiban positif.
- **Celah C (`document_ontology.json` + `OntologyLoader.kt`):** Menambahkan kamus pemetaan eksplisit `spoPropertyMappings` / `ontologyBindings` sehingga setiap fakta SPO terpetakan tegas ke `ConceptId.propertyName` sekaligus mengevaluasi `missingRequiredProperties`.
- **Celah D (`DocumentSummary` + `FactBasedSummarizer.kt` + `DocumentSummaryScreen.kt` + `SummaryPdfExporter.kt`):** Memisahkan secara eksplisit **Ringkasan Isi Dokumen (*Document Summarization*)** (sintesis menyeluruh plus ringkasan perwakilan awal/inti/akhir halaman), dan **Poin Penting Berbasis Aturan (*Rule-Based Key Points*)**.
- **Pencarian & Hapus Massal Riwayat (`AskVelaScreen.kt` + `AnalysisHistoryDao.kt` + `DocumentAnalysisRepository.kt`):** Menambahkan filter pencarian murni di ViewModel dan hapus massal dengan `deleteByIds` + `deleteAnalysesBatched(..., batchSize = 500)` tanpa menaikkan versi Room (tetap `v2`).
- *Konsekuensi:* Struktur `DocumentSummary` memiliki field eksplisit baru yang tetap kompatibel dengan Room `v2` (disimpan sebagai teks terangkum pada `analysis_history_table`).
