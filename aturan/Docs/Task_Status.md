# Task & Progress Tracker — VelaPDF (`com.njagakneai.velapdf`)

> **DESIGNATED APPROVER:** Yogi Ario (`@useripx`)  
> **AGEN PENGEMBANG:** Carl (`AGT-PRD-DEV-001`)  
> **BRANCH AKTIF:** `develop` / `feature/03_uji_perangkat_fisik_dan_polish` *(Tahap 01 + 02 dirilis ke `main` & `develop` pada tag **`v1.4.0`**)*  
> **TERAKHIR DIPERBARUI:** 2026-11-12 09:35 WIB

---

## Ringkasan Progres Lintas Tahap

| Tahap | Deskripsi Pekerjaan | Branch | Status | Progres |
| :---: | :--- | :--- | :---: | :---: |
| **Tahap 00** | Penyelarasan Aturan (`aturan/`), Sterilisasi Legacy Stub (`DEF-LEG-004`), & Fix 2 Lint Error (`DEF-LEG-009`) | `develop` | **COMPLETED** | 100% |
| **Tahap 01** | Implementasi Pipeline OCR Luring + 4-JSON (`NLP + SPO + Ontologi + Rules`) + Menu `ASK Vela` + Room DB `v2` + Ekspor PDF + `aturan/simpan/tahap1.md` | `develop` & `main` (`v1.4.0`) | **RELEASED (`v1.4.0`)** | 100% |
| **Tahap 02** | Perbaikan Celah `A–D`, Pencarian & Hapus Massal Riwayat, Benchmark 17 PDF Offline Emulator + Profiling Memori, 15 Screenshot UI, UAT Infinix Note 30 (`"buatkan rilis"`), & `aturan/simpan/tahap2.md` | `develop` & `main` (`v1.4.0`) | **RELEASED (`v1.4.0`)** | 100% |
| **Tahap 03** | Uji Perangkat Fisik Infinix Note 30 + Kamera Langsung + Poles Kontras UI (`[OBS-UI-001]`), Rasio Surat Pendek (`[OBS-NLP-001]`), + Opsional Paket 1B Akurasi Pembacaan Teks PDF/OCR & Bahan Bab IV | `feature/03_uji_perangkat_fisik_dan_polish` | **APPROVED (Konfirmasi Opsi Paket 1B)** | 0% |

---

## Rincian Paket Kerja Tahap 02 (`100% Selesai`)

| Paket | Deliverables | Bukti Verifikasi | Status |
| :---: | :--- | :--- | :---: |
| **Paket 1** | Penutupan Celah Ilmiah `A–D`: fakta `mengindikasikanKonsep` + `ArtikelJurnalIlmiah` (`RULE_14..16`), pelestarian kata negasi/modalitas (`tidak`, `bukan`, `belum`, `wajib`, `dilarang`, `harus`), pemetaan eksplisit Ontologi–SPO (`spoPropertyMappings`), & pemisahan *Document Summarization* vs *Rule-Based Key Points*. | `NlpLexiconAndPreprocessorTest` (`3/3`), `SpoPatternAndRuleEngineTest` (`10/10`), `FactBasedSummarizerTest` (`3/3`) | **PASS** |
| **Paket 2** | Fitur Pencarian Riwayat (`AskVelaScreen`) & Mode `"Tandai"` Multi-Select + Hapus Massal ter-batch (`AnalysisHistoryDao.deleteByIds`, `deleteAnalysesBatched(batchSize = 500)`) + fix `UseKtx` Lint. | `HistorySearchAndBatchDeleteTest` (`3/3`), `lintDebug` (`0 errors`), visual UI [`11`..`14`](Evidence/SS/valid/14_ask_vela_after_delete.png) | **PASS** |
| **Paket 3** | Benchmark otomatis `androidTest` atas seluruh 17 PDF nyata (`SK-01..05`, Jurnal `12..14`, `Roadmap.pdf`, `next.pdf`, `KHS1..5`, `Transkip1..2`) dalam **Airplane Mode ON** + profiling latensi & Java Heap / Native / PSS. | `TestbedPipelineBenchmarkTest` (`3/3 PASS`, `344.329s`), [`benchmark_results_tahap2.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap2.csv) | **PASS** |
| **Paket 4** | Verifikasi visual interaktif UI emulator (**15 PNG** terinspeksi), pembaruan `aturan/Docs/`, `aturan/alatuji/`, arsip rencana Tahap 02, rencana berantai Tahap 03, & **`aturan/simpan/tahap2.md`** (`Panduan_Belajar_Dosen.md`). | [`Test_Report.md`](Test_Report.md), [`tahap2.md`](../simpan/tahap2.md), & [`valid/`](Evidence/SS/valid) | **PASS** |

---

## Tindakan Menunggu Konfirmasi Yogi Ario (`@useripx`)

1. Konfirmasi atas pertanyaan **"perangkuman kadang salah membaca teks apakah ini masuk batasan masalah atau kita perbaiki?"** (apakah **Tahap 03** langsung menyertakan **Paket 1B: Hybrid iText7 Native PDF Text + ML Kit Fallback & OCR Cleanup**, sambil tetap mencatat batas kualitas foto kamera/tabel kompleks di `batasan_masalah.md`).
2. Bila `gh` CLI belum login di Windows, unggah `app-debug.apk` ke GitHub Release `v1.4.0` memakai catatan [`Release_Notes_v1.4.0.md`](Release_Notes_v1.4.0.md).
3. Persetujuan pencadangan transkrip sesi ini ke `aturan/Docs/chat/riwayat12112026.md`.
