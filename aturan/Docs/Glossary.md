# GLOSARIUM RESMI TATA KELOLA & REKAYASA SISTEM (GLOSSARY)

> **DOCUMENT ID:** DOC-REF-GLO-001  
> **VERSION:** v2.0.0  
> **STATUS:** ACTIVE / MANDATORY  
> **AUTHORITY LEVEL:** Level 5 — Reference & Terminology Standard  
> **OWNER:** Designated Approver & AI Engineering Team (Rujuk [config/organization.md](../config/organization.md))  
> **LAST UPDATED:** 07/11/2026  
> **AUTHORITATIVE SOURCE (SSOT):** [Docs/universal-rule.md](universal-rule.md) & [Docs/Standards/Documentation_Standards.md](Standards/Documentation_Standards.md)  
> **RELATED DOCUMENTS:** [Docs/Governance_Hierarchy.md](Governance_Hierarchy.md), [Docs/Approval_Governance.md](Approval_Governance.md), [Docs/Standards/QA_Framework.md](Standards/QA_Framework.md)

---

## 1. Pendahuluan

Glosarium ini mendefinisikan istilah teknis baku, terminologi tata kelola, dan konsep rekayasa yang digunakan di seluruh repositori **Universal AI Engineering Standard**. 

Seluruh dokumen kebijakan, instruksi agen, modul skill, dan komunikasi kerja **WAJIB** merujuk pada definisi resmi di bawah ini guna mencegah kesalahpahaman, ambiguitas semantik, atau multi-tafsir.

---

## 2. Istilah Inti Tata Kelola (Core Governance Terms)

- **Universal Governance (Tata Kelola Universal):** Tingkatan aturan tertinggi (Level 1) yang mengikat seluruh manusia, AI Agent, alat teknis, modul skill, dan alur kerja proyek tanpa pengecualian. Aturan di level ini bersifat agnostik domain/bahasa pemrograman.
- **Enam Prinsip Tata Kelola (Six Governance Principles):**
  1. *No Understanding, No Execution:* Larangan memulai pekerjaan tanpa audit konteks dan pemahaman menyeluruh terhadap kebutuhan.
  2. *No Plan, No Code:* Larangan menulis kode atau mengubah berkas sebelum dokumen rencana kerja bertahap selesai disusun.
  3. *No Approval, No Code:* Larangan memodifikasi berkas kerja sebelum rencana kerja disetujui secara eksplisit oleh Designated Approver.
  4. *No Verification, No Completion:* Larangan mengklaim tugas selesai tanpa pengujian dan verifikasi bukti faktual.
  5. *No Evidence, No Completion:* Setiap klaim keberhasilan wajib didukung bukti nyata yang dapat direproduksi (log test, output CLI, ringkasan eksekusi).
  6. *No Validated Skill, No Production Use:* Larangan menggunakan modul kemampuan AI di lingkungan produksi sebelum lulus uji 6 dimensi validasi.
- **Approval Gate (Gerbang Persetujuan):** Titik kendali formal di mana AI Agent wajib berhenti (*Hold/Wait*) dan meminta persetujuan eksplisit dari manusia (Designated Approver) sebelum melanjutkan ke fase eksekusi atau rilis.
- **Human-in-the-Loop (HITL):** Pola tata kelola di mana keputusan penting, tindakan berisiko tinggi, dan pengesahan rilis dipegang secara eksklusif oleh manusia berwenang.
- **Clean Main Policy:** Kebijakan isolasi mutlak cabang produksi (`main`), di mana cabang `main` hanya boleh memuat kode produksi stabil yang telah disahkan dan bersih dari artefak sementara, draf kerja, tangkapan layar evidensi uji sementara, atau log percakapan internal.
- **Zero Secret Leakage:** Larangan keras mencetak, menulis, atau mengunggah kredensial, token API, password, dan kunci privat ke dalam repositori git, berkas kode, atau pesan log.
- **Single Source of Truth (SSOT):** Prinsip arsitektur di mana satu kategori aturan atau fakta sistem hanya memiliki satu dokumen otoritatif resmi tunggal; dokumen turunan hanya boleh mereferensikan tanpa mendefinisikan ulang aturan yang berpotensi kontradiktif.
- **Designated Approver:** Individu atau peran manusia yang memiliki wewenang formal tunggal untuk menyetujui rencana kerja, perubahan arsitektur, dan otorisasi produksi sebagaimana ditetapkan dalam `config/organization.md`.
- **RFC 2119 Keywords:** Standar klasifikasi derajat keharusan instruksi menggunakan kata kunci normatif: *MUST, MUST NOT, SHOULD, SHOULD NOT, MAY*.

---

## 3. Taksonomi Pembuktian & Otorisasi (Proof & Authorization Taxonomy)

Guna mengeliminasi kerancuan semantik, istilah-istilah berikut dibedakan secara tegas:

- **Verification (Verifikasi):** Evaluasi teknis objektif untuk membuktikan bahwa suatu artefak, kode, atau hasil kerja memenuhi spesifikasi teknis dan kriteria penerimaan yang ditentukan (*"Are we building the product right?"*). Meliputi uji sintaks, unit test, build, linting, dan cek konsistensi.
- **Evidence (Evidensi / Bukti Faktual):** Berkas atau artefak rekaman data konkret, tidak dapat diubah (*immutable*), dan dapat direproduksi (seperti log keluaran CLI, hasil eksekusi pengujian, tangkapan layar terverifikasi, atau checksum kriptografis) yang membuktikan bahwa proses verifikasi benar-benar telah dijalankan dengan hasil valid.
- **Validation (Validasi):** Evaluasi holistik untuk memastikan bahwa artefak, modul skill, atau sistem yang dibangun benar-benar memenuhi kebutuhan fungsional pengguna, tujuan bisnis, dan beroperasi dengan benar dalam kondisi lingkungan nyata (*"Are we building the right product?"*).
- **Approval (Persetujuan):** Tindakan pemberian mandat formal oleh Designated Approver kepada AI Agent untuk mengeksekusi rencana kerja bertahap (*Implementation Plan*) yang telah disusun.
- **Production Authorization (Otorisasi Produksi):** Pengesahan rilis tingkat tertinggi oleh Designated Approver yang memberikan izin untuk melakukan merge ke cabang `main` atau deployment ke infrastruktur produksi setelah lolos seluruh Quality Gates.

---

## 4. Istilah Disiplin Rekayasa & Alur Kerja (Engineering & Workflow Terms)

- **Definition of Ready (DoR):** Kriteria prasyarat yang wajib dipenuhi sebelum suatu tugas pengembangan diizinkan masuk ke tahap perencanaan/eksekusi (masukan jelas, dependensi tersedia, ruang lingkup dibatasi).
- **Definition of Done (DoD):** Kriteria objektif kelengkapan kerja yang wajib dipenuhi sebelum suatu tugas dinyatakan selesai (kode lolos lint/test, tidak ada regresi, evidensi tercatat, dokumentasi diperbarui).
- **Surgical Coding (Koding Presisi / Bedah):** Praktik memodifikasi baris kode secara terarah, presisi, dan proporsional sesuai ruang lingkup tugas, tanpa memformat ulang seluruh berkas atau mengubah kode yang tidak relevan secara serampangan.
- **Code Smell:** Indikasi adanya kelemahan desain struktural pada kode yang berpotensi memicu cacat atau menyulitkan pemeliharaan di masa depan.
- **Technical Debt (Hutang Teknis):** Biaya pemeliharaan tambahan di masa depan yang timbul akibat memilih solusi pintas atau tidak memenuhi standar mutu saat ini.
- **Semantic Versioning (SemVer):** Format penomoran versi `vMAJOR.MINOR.PATCH` berbasis kompatibilitas perubahan perangkat lunak atau dokumen standar.
- **Idempotency (Idempotensi):** Karakteristik operasi di mana eksekusi berulang kali dengan parameter yang sama menghasilkan status luaran yang persis sama tanpa memicu efek samping tambahan (*side effects*).

---

## 5. Istilah Sistem Jaminan Mutu & Pengujian (Quality Assurance Terms)

- **Quality Gates (Gerbang Mutu):** Pos pemeriksaan verifikasi formal berjenjang (QG1: Syntax/Lint, QG2: Unit Test, QG3: Static Analysis/Security, QG4: Functional/Integration, QG5: Final Human Sign-off) yang wajib dilalui sebelum kode diizinkan rilis.
- **Flaky Test:** Pengujian otomatis yang menghasilkan status acak (kadang lulus, kadang gagal) pada basis kode yang sama tanpa adanya perubahan logika. Flaky test wajib diisolasi (karantina) dan tidak boleh diabaikan begitu saja.
- **Defect Management (Manajemen Cacat):** Proses formal pencatatan, klasifikasi keparahan (Critical, High, Medium, Low), perbaikan, dan verifikasi ulang terhadap cacat dengan format ID baku `DEF-[MOD]-[NUM]`.
- **Test Suite:** Kumpulan kasus uji terotomatisasi yang dirancang untuk memverifikasi perilaku modul atau sistem tertentu.
- **Negative Testing:** Pengujian yang sengaja memberikan masukan tidak valid atau kondisi batas untuk memastikan sistem menangani kesalahan secara anggun (*graceful error handling*).

---

## 6. Istilah Tata Kelola Agen & Eksekusi Tool (Agent Governance Terms)

- **Role Separation (Pemisahan Peran):** Pemisahan tegas tanggung jawab fungsional antar-agen cerdas buatan untuk mencegah konflik kepentingan (misal: agen pengembang tidak boleh mengesahkan kodenya sendiri tanpa review independen).
- **Authority Matrix (Matriks Kewenangan):** Tabel pembagian hak akses terhadap aksi teknis diskrit (Read, Analyze, Create, Modify, Delete, Execute, Commit, Push, Merge, Approve).
- **Handoff Protocol (Protokol Serah Terima):** Prosedur formal transfer kepemilikan tugas antar-agen menggunakan formulir bidang informasi wajib guna mengeliminasi kehilangan konteks.
- **Context Drift:** Fenomena di mana pemahaman agen bergeser secara perlahan dari tujuan utama proyek akibat obrolan yang panjang atau hilangnya riwayat instruksi awal.
- **Bounded Retry (Percobaan Ulang Terbatas):** Kebijakan pengulangan eksekusi yang sadar idempotensi (*idempotency-aware*) dan spesifik kategori kegagalan (misalnya: 0 retry untuk operasi non-idempotent seperti penghapusan, hingga 3x backoff untuk error transient/jaringan).
- **BLOCKED Status:** Status penghentian sementara eksekusi saat agen menemui kendala kritis, dependensi yang belum terpenuhi, atau kegagalan yang memerlukan intervensi manusia.
- **AI Employee Skill:** SOP operasional formal kemampuan kerja agen berbasis berkas 18 atribut `SKILL.md` yang telah divalidasi melalui 6 dimensi pengujian mutu sebelum digunakan di lingkungan produksi.
