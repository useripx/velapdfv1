# Test Report — Tahap 01: Pipeline Offline OCR, 4 Kamus JSON & Rule-Based Inference (`ASK Vela`)

> **STATUS GERBANG PENGUJIAN:** `PASS` *(Gerbang Otomatis JVM Unit Test, Android Lint, & Assemble Debug APK)*  
> **PROYEK:** VelaPDF (`com.njagakneai.velapdf` — `v1.3 Beta Revision 3`)  
> **DESIGNATED APPROVER:** Yogi Ario (`@useripx`)  
> **PENGUJI:** Carl (`AGT-PRD-DEV-001`)  
> **TANGGAL PENGUJIAN:** 2026-11-11 10:35 WIB  
> **BRANCH:** `feature/01_ocr_dan_ask_vela`  
> **ACUAN STANDAR:** [Docs/Standards/QA_Framework.md](Standards/QA_Framework.md) & [Docs/Standards/Verification_Evidence.md](Standards/Verification_Evidence.md)

---

## 1. Ringkasan Eksekusi Pengujian (Executive Test Summary)

| Metrik Kualitas | Hasil Aktual (`task-715`) | Ambang Batas Gerbang (Threshold) | Status Gerbang |
| :--- | :---: | :---: | :---: |
| **Total JVM Unit Tests** | `11` | Minimal 1 per skenario `SK-01..SK-05` & Buku IoT | **PASS** |
| **Passed (Lulus)** | `11` | Seluruh unit test wajib lulus | **PASS (100%)** |
| **Failed / Errors / Skipped** | `0 / 0 / 0` | Wajib `0` | **PASS** |
| **Android Lint (`lintDebug`)** | `0 errors, 48 warnings, 1 hint` | Wajib `0 errors` | **PASS** |
| **APK Build (`assembleDebug`)** | `BUILD SUCCESSFUL` | Kompilasi & packaging APK debug bebas error | **PASS** |
| **UI Emulator / Visual Device Test** | `PENDING (Belum Dijalankan di Device/Emulator)` | Pengujian visual di emulator/HP pada Tahap 2 | **MANUAL / TAHAP 2** |

---

## 2. Lingkungan & Alat Pengujian

- **Sistem Operasi:** Windows (PowerShell)
- **Toolchain Terkunci:** Gradle `9.8.0`, AGP `9.4.1`, Kotlin `2.4.20`, Compose BOM `2026.09.00`, Hilt `2.60.1`, Room `2.8.5`, JUnit `4.13.2`
- **Perintah Eksekusi Verifikasi:** `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon`
- **Basis Data & Aset Uji:** 4 berkas JSON asli di `app/src/main/assets/` dimuat oleh `TestAssetProvider` (`nlp_lexicon_id.json`, `spo_pattern_dictionary.json`, `document_ontology.json`, `production_rules.json`) + teks skenario otentik `SK-01..SK-05` & *Buku IoT Hal. 23–35*.

---

## 3. Rincian Kasus Uji Unit Test (`app/build/test-results/testDebugUnitTest/`)

| Suite / Test Case | Skenario Uji | Hasil Aktual | Durasi | Status |
| :--- | :--- | :--- | :---: | :---: |
| `NlpLexiconAndPreprocessorTest` #1 | Parsing `732` stopwords, `25` singkatan terlindungi, `7` section markers, `33` domain boost terms | Lulus verifikasi pembacaan `nlp_lexicon_id.json` | `0.008s` (suite) | **PASS** |
| `NlpLexiconAndPreprocessorTest` #2 | Proteksi singkatan (`No.`, `Dr.`, `Hal.`) & ekstraksi kata kunci domain tanpa stopword | Kalimat tidak terpotong salah; top keywords akurat | included | **PASS** |
| `SpoPatternAndRuleEngineTest` #1 (`SK-01`) | Surat Keputusan Rektor (`112/SK/UNP-KD/X/2026`) | Nomor terekstrak; `RULE_01` & `RULE_07` menembak | `0.332s` (suite) | **PASS** |
| `SpoPatternAndRuleEngineTest` #2 (`SK-02`) | Surat Edaran Jadwal KRS (`048/SE/UNP-KD/I/2026`) | Fakta kewajiban & tenggat; `RULE_02`, `RULE_05`, `RULE_07` menembak | included | **PASS** |
| `SpoPatternAndRuleEngineTest` #3 (`SK-03`) | Pedoman SOP Ujian Skripsi (`SOP/FT-TI/2026/03`) | Fakta tenggat revisi; `RULE_03` & `RULE_05` menembak | included | **PASS** |
| `SpoPatternAndRuleEngineTest` #4 (`SK-04`) | Peraturan Tata Tertib Kampus (`05 TAHUN 2026`) | Fakta larangan; `RULE_04` & `RULE_06` menembak | included | **PASS** |
| `SpoPatternAndRuleEngineTest` #5 (`SK-05`) | Laporan Ringkasan LPPM (`215/LPPM/UNP-KD/2026`) | Nomor/Tanggal Dokumen/Ketua LPPM; `RULE_08` & `RULE_07` menembak | included | **PASS** |
| `SpoPatternAndRuleEngineTest` #6 (`IoT Hal. 23–35`) | Buku Ajar IoT Bahasa Indonesia Hal. 23, 28, 35 | Klasifikasi `BukuAjarTeknis`; SPO definisi/komponen; `RULE_09`, `RULE_10`, `RULE_11`/`12` menembak | included | **PASS** |
| `FactBasedSummarizerTest` #1 | Buku IoT multi-halaman | Paragraf kesimpulan Bahasa Indonesia, keyPoints & `pageReferences` valid (`23/24/29`) | `0.059s` (suite) | **PASS** |
| `FactBasedSummarizerTest` #2 | Teks sangat singkat (*edge case*) | Tidak crash; *fallback* paragraf kesimpulan & intisari terisi | included | **PASS** |
| `ExampleUnitTest` | Baseline sanity test | `2 + 2 == 4` | `0.001s` | **PASS** |

---

## 4. Bukti Konkret Pengujian

```text
> Task :app:testDebugUnitTest
> Task :app:packageDebug
> Task :app:assembleDebug
> Task :app:lintAnalyzeDebug
> Task :app:lintReportDebug
Wrote HTML report to file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/build/reports/lint-results-debug.html
Wrote SARIF report to file:///C:/Users/yogia/AndroidStudioProjects/VelaPDF/app/build/reports/lint-results-debug.sarif
> Task :app:lintDebug

BUILD SUCCESSFUL in 1m 53s
61 actionable tasks: 21 executed, 40 up-to-date
```

- **Lint Text Summary (`app/build/reports/lint-results-debug.txt`):** `0 errors, 48 warnings, 1 hint` (dari seluruh kode baru Tahap 1, hanya terdapat 1 saran `UseKtx` pada `AskVelaScreen.kt:360`).
- **Catatan Uji Lanjutan:** Pengambilan bukti visual layar HP/Emulator Android (`aturan/Docs/Evidence/SS/valid/`) serta uji OCR PDF fisik & konsumsi RAM $\le 50\text{ MB}$ dijadwalkan pada **Tahap 02 (`feature/02_evaluasi_testbed`)**.
