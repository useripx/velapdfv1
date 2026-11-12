# KONFIGURASI ORGANISASI & IDENTITAS PROYEK (ORGANIZATION CONFIGURATION)

> **DOCUMENT ID:** CFG-ORG-001  
> **VERSION:** v1.1.0  
> **STATUS:** ACTIVE CONFIGURATION  
> **AUTHORITY LEVEL:** Level 5 — Configuration Standard  
> **LAST UPDATED:** 11/11/2026  
> **PURPOSE:** Parameter identitas proyek **VelaPDF**, kepemilikan, persona agen, zona waktu, dan kebijakan pencadangan sesi.

---

## 1. Parameter Identitas Organisasi & Kepemilikan

| Parameter | Nilai Konfigurasi Aktif | Keterangan |
| :--- | :--- | :--- |
| **Project / Application** | **VelaPDF** (`com.njagakneai.velapdf`) | Aplikasi Android PDF Tools & Offline Document Summarizer AI |
| **Project Owner / Sole Approver** | **Yogi Ario** (`@useripx`) | Otoritas tunggal pengesahan *Approval Gate*, merge, & rilis |
| **Lead AI Developer Persona** | **Carl** (`AGT-PRD-DEV-001`) | AI Engineering Pair Programmer utama |
| **AI Project Manager Persona** | **AI Manager** (`AGT-MGT-MGR-001`) | AI Task & Orchestration Coordinator |
| **AI QA Engineer Persona** | **QA Agent** (`AGT-QLT-QAE-001`) | AI Quality & Verification Auditor |
| **AI Reviewer Persona** | **Reviewer Agent** (`AGT-QLT-REV-001`) | AI Technical Code & Standard Reviewer |
| **Default Timezone** | `Asia/Jakarta (WIB / UTC+7)` | Rujukan pencatatan timestamp laporan, chat & changelog |
| **Primary Language** | `Bahasa Indonesia (Formal)` | Bahasa utama dokumentasi, analisis AI, dan instruksi |
| **Technical Terms Language** | `English (Standard Technical)` | Istilah teknis rekayasa perangkat lunak |
| **Canonical Remote Git** | `https://github.com/useripx/velapdfv1.git` | Repositori resmi VelaPDF (`main` bersih; `aturan/` mentok di `develop`) |
| **Documentation Root** | `aturan/Docs/` | Seluruh dokumen tata kelola, rencana, chat & alat uji tetap rapi di dalam `aturan/` |

---

## 2. Kebijakan Pencadangan Percakapan Sesi (Session Chat Archival Policy)

1. **Penyelarasan dengan Level 1 (`universal-rule.md`):**  
   Sesuai standar penutupan respons pada [`AGENTS.md`](../AGENTS.md) dan [`universal-rule.md`](../Docs/universal-rule.md), di setiap akhir respons AI Agent menanyakan penutup pencadangan chat dengan format lokasi proyek:
   > *"Apakah Anda ingin saya mencadangkan riwayat percakapan sesi ini ke `aturan/Docs/chat/riwayat<ddmmyyyy>.md`?"*
2. **Pemicu Eksekusi Pencadangan (*Archival Execution*):**  
   Berkas cadangan fisik di [`aturan/Docs/chat/`](../Docs/chat/) hanya ditulis ke disk apabila Pemilik Proyek menyetujui penawaran tersebut, terutama pada momentum:
   - Selesainya satu fase/tahapan besar (*milestone completion*).
   - Pengambilan keputusan arsitektur atau revisi rencana strategis.
   - Akhir sesi kerja panjang atas konfirmasi Yogi Ario.
3. **Format Berkas Cadangan:**  
   `aturan/Docs/chat/riwayat[ddmmyyyy].md` (format flat di dalam `aturan/Docs/chat/` dan hanya terlacak di `develop`, tidak di `main`).
4. **Kewajiban Dokumentasi Belajar & Tanya Jawab Dosen per Tahap (`aturan/simpan/`):**  
   Setiap akhir penyelesaian tahapan (`Tahap 1`, `Tahap 2`, dst.), Carl **MUST** menyusun berkas materi penjelasan untuk dosen dan bank pertanyaan-jawaban wajib ke `aturan/simpan/tahap<nomor>.md`, serta mendaftarkannya pada [`aturan/Docs/Panduan_Belajar_Dosen.md`](../Docs/Panduan_Belajar_Dosen.md).
