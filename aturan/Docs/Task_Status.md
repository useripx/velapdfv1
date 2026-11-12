# Task & Progress Tracker — VelaPDF (`com.njagakneai.velapdf`)

> **DESIGNATED APPROVER:** Yogi Ario (`@useripx`)  
> **AGEN PENGEMBANG:** Carl (`AGT-PRD-DEV-001`)  
> **BRANCH AKTIF:** `feature/02_evaluasi_testbed_dan_ui` *(terpisah dari `feature/01_ocr_dan_ask_vela` & belum digabung ke `develop`/`main`)*  
> **TERAKHIR DIPERBARUI:** 2026-11-12 09:05 WIB

---

## Ringkasan Progres Lintas Tahap

| Tahap | Deskripsi Pekerjaan | Branch | Status | Progres |
| :---: | :--- | :--- | :---: | :---: |
| **Tahap 00** | Penyelarasan Aturan (`aturan/`), Sterilisasi Legacy Stub (`DEF-LEG-004`), & Fix 2 Lint Error (`DEF-LEG-009`) | `develop` | **COMPLETED** | 100% |
| **Tahap 01** | Implementasi Pipeline OCR Luring + 4-JSON (`NLP + SPO + Ontologi + Rules`) + Menu `ASK Vela` + Room DB `v2` + Ekspor PDF + `aturan/simpan/tahap1.md` | `feature/01_ocr_dan_ask_vela` | **COMPLETED** | 100% |
| **Tahap 02** | Perbaikan Celah `A–D`, Pencarian & Hapus Massal Riwayat, Benchmark 17 PDF Offline Emulator + Profiling Memori, 15 Screenshot UI, & `aturan/simpan/tahap2.md` | `feature/02_evaluasi_testbed_dan_ui` | **COMPLETED (Menunggu ACC Merge Yogi)** | **100%** |
| **Tahap 03** | Uji Perangkat Fisik Xiaomi Redmi Note 11 + Uji Kamera Langsung + Poles Kontras UI (`[OBS-UI-001]`) & Rasio Surat Pendek (`[OBS-NLP-001]`) + Persiapan Merge `develop` & Bahan Bab IV | `feature/03_uji_perangkat_fisik_dan_polish` | **PLANNED** | 0% |

---

## Rincian Paket Kerja Tahap 02 (`100% Selesai`)

| Paket | Deliverables | Bukti Verifikasi | Status |
| :---: | :--- | :--- | :---: |
| **Paket 1** | Penutupan Celah Ilmiah `A–D`: fakta `mengindikasikanKonsep` + `ArtikelJurnalIlmiah` (`RULE_14..16`), pelestarian kata negasi/modalitas (`tidak`, `bukan`, `belum`, `wajib`, `dilarang`, `harus`), pemetaan eksplisit Ontologi–SPO (`spoPropertyMappings`), & pemisahan *Document Summarization* vs *Rule-Based Key Points*. | `NlpLexiconAndPreprocessorTest` (`3/3`), `SpoPatternAndRuleEngineTest` (`10/10`), `FactBasedSummarizerTest` (`3/3`) | **PASS** |
| **Paket 2** | Fitur Pencarian Riwayat (`AskVelaScreen`) & Mode `"Tandai"` Multi-Select + Hapus Massal ter-batch (`AnalysisHistoryDao.deleteByIds`, `deleteAnalysesBatched(batchSize = 500)`) + fix `UseKtx` Lint. | `HistorySearchAndBatchDeleteTest` (`3/3`), `lintDebug` (`0 errors`), visual UI [`11`..`14`](Evidence/SS/valid/14_ask_vela_after_delete.png) | **PASS** |
| **Paket 3** | Benchmark otomatis `androidTest` atas seluruh 17 PDF nyata (`SK-01..05`, Jurnal `12..14`, `Roadmap.pdf`, `next.pdf`, `KHS1..5`, `Transkip1..2`) dalam **Airplane Mode ON** + profiling latensi & Java Heap / Native / PSS. | `TestbedPipelineBenchmarkTest` (`3/3 PASS`, `344.329s`), [`benchmark_results_tahap2.csv`](../alatuji/05_spesifikasi_testbed/benchmark_results_tahap2.csv) | **PASS** |
| **Paket 4** | Verifikasi visual interaktif UI emulator (**15 PNG** terinspeksi), pembaruan `aturan/Docs/`, `aturan/alatuji/`, arsip rencana Tahap 02, rencana berantai Tahap 03, & **`aturan/simpan/tahap2.md`** (`Panduan_Belajar_Dosen.md`). | [`Test_Report.md`](Test_Report.md), [`tahap2.md`](../simpan/tahap2.md), & [`valid/`](Evidence/SS/valid) | **PASS** |

---

## Tindakan Menunggu Persetujuan Yogi Ario (`@useripx`)

1. Persetujuan rencana **Tahap 03** ([`Implementation_plan.md`](Implementation_plan.md)) dan/atau izin merge `feature/01_ocr_dan_ask_vela` & `feature/02_evaluasi_testbed_dan_ui` ke `develop` (tetap **tanpa** `aturan/` menyentuh `main`).
2. Persetujuan pencadangan transkrip sesi ini ke `aturan/Docs/chat/riwayat12112026.md`.
