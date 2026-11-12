# Panduan Belajar & Tanya Jawab Dosen — Indeks Dokumentasi VelaPDF

> **STATUS DOKUMEN:** INDEKS MATERI PEMBELAJARAN & PERSIAPAN BIMBINGAN / SEMPRO  
> **PEMILIK PROYEK / MAHASISWA:** Yogi Ario Pratama (`NPM: 2313020004` / `@useripx`)  
> **PEMBIMBING AKADEMIK:** Rony Heri Irawan, M.Kom. (Universitas Nusantara PGRI Kediri)  
> **PARTNER AI PENGEMBANG:** Carl (`AGT-PRD-DEV-001`)  
> **ATURAN TERKAIT:** [`AGENTS.md`](../AGENTS.md), [`GEMINI.md`](../GEMINI.md), [`organization.md`](../config/organization.md), & [`batasan_masalah.md`](batasan_masalah.md)

---

## 1. Tujuan Dokumentasi Ini

Folder `aturan/simpan/` menyimpan panduan belajar mendalam per tahapan pengembangan **VelaPDF** agar pengembang/mahasiswa memahami **mengapa** kode ditulis demikian, **bagaimana** alur data dari kamera/PDF hingga kesimpulan Bahasa Indonesia bekerja secara matematis dan algoritmik (*NLP + Ontologi + SPO + Forward Chaining*), serta **siap menjawab pertanyaan kritis dosen pembimbing dan penguji** saat bimbingan, Seminar Proposal (Sempro), maupun Sidang Akhir.

---

## 2. Daftar Berkas Materi per Tahap (`aturan/simpan/`)

| Tahap | Judul & Fokus Teknis | Lokasi Berkas Materi & Bank Q&A | Status |
| :---: | :--- | :--- | :---: |
| **Tahap 01** | **Pipeline OCR Luring + 4 Kamus JSON + Rule-Based Inference (Forward Chaining) + Menu ASK Vela & Tombol Ringkasan + Room DB v2** | [`aturan/simpan/tahap1.md`](../simpan/tahap1.md) | **TERSEDIA** |
| **Tahap 02** | **Penyempurnaan Celah A–D (Ontologi-SPO, Negasi, Summarization vs Rule Key-Points), Pencarian & Hapus Massal Riwayat, Testbed Empiris 17 PDF Offline & Profiling Memori** | [`aturan/simpan/tahap2.md`](../simpan/tahap2.md) | **TERSEDIA** |
| **Tahap 03** | **Uji Kamera Langsung & Perangkat Fisik Infinix Note 30 + Poles Kontras UI + Checklist Rilis GitHub & Bahan Bab IV** | `aturan/simpan/tahap3.md` *(Akan dibuat setelah Tahap 3 selesai)* | **PLANNED** |

---

## 3. Referensi Pendamping di `aturan/Docs/`

- [`batasan_masalah.md`](batasan_masalah.md) — Batasan ruang lingkup resmi (khusus Bahasa Indonesia, tanpa chatbot Q&A, tanpa Cloud LLM, non-password PDF/foto, rentang halaman maks. 35 hal./sesi).
- [`Database_Design.md`](Database_Design.md) — Kamus data Room SQLite `velapdf_database` v1 &rarr; v2 (`history_table` & `analysis_history_table`).
- [`alatuji/`](../alatuji/README.md) — Sampel dokumen uji otentik `SK-01` s.d. `SK-05`, spesifikasi testbed, dan `ground_truth_facts.json`.
