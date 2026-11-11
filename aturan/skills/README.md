# DIREKTORI MASTER SKILL KARYAWAN AI (AI EMPLOYEE SKILLS REPOSITORY)

Selamat datang di repositori operasional **AI Employee Skills**. Repositori ini menyimpan seluruh modul kemampuan prosedural (*Standard Operating Procedures / SOP*) yang dapat dijalankan secara konsisten oleh AI Agent dalam ekosistem *Universal AI Engineering Standard*.

---

## 1. Filosofi & Prinsip Utama Tata Kelola Skill

- **Filosofi:**  
  $$\mathbf{Consistent\ Standards,\ Variable\ Intelligence.}$$  
  Setiap model AI dapat memiliki variasi parameter dan kecerdasan, namun seluruh skill wajib beroperasi di bawah standar format, pengujian, kontrol akses, dan pembuktian yang seragam.
- **Prinsip Mutlak:**  
  $$\mathbf{No\ Validated\ Skill,\ No\ Production\ Use.}$$  
  Skill yang belum tervalidasi secara teknis (100% PASS pada 6 dimensi validasi) dan belum disetujui tertulis oleh Yogi Ario (Project Owner) **DILARANG KERAS** digunakan pada pekerjaan lingkungan produksi.
- **Hierarki Status:**  
  $$\text{Draft} \longrightarrow \text{Testing} \longrightarrow \mathbf{Validated} \longrightarrow \mathbf{Approved} \longrightarrow \mathbf{Production}$$

---

## 2. Struktur Direktori Taksonomi Skill

```text
skills/
├── README.md                                  <- Berkas panduan ini
├── template/
│   └── SKILL.md                               <- Master template rujukan SKILL.md (18 Atribut)
├── general/                                   <- Kemampuan umum (riset, summarization, planning)
├── software-engineering/                      <- Kemampuan siklus hidup rekayasa perangkat lunak
│   └── requirement-analysis/
│       └── SKILL.md                           <- Modul SKL-ENG-REQ-001 (Production)
├── business/                                  <- Kemampuan analisis pasar & bisnis
├── creative/                                  <- Kemampuan konten, visual, dan presentasi
└── domain-specific/                           <- Kemampuan dengan kepatuhan industri khusus
```

---

## 3. Aturan Operasional bagi AI Agent

Sebelum mengeksekusi modul skill apa pun, AI Agent **MUST** mematuhi ketentuan berikut:

1. **Konsultasikan Registri Terlebih Dahulu:**  
   Buka [`Docs/Skill_Registry.md`](../Docs/Skill_Registry.md) untuk memastikan modul skill berstatus **PRODUCTION**.
2. **Cek Izin Peran Agen:**  
   Pastikan peran Anda tercantum pada atribut `Intended Agent Roles` dalam `SKILL.md`.
3. **Disiplin Terhadap SOP:**  
   Ikuti langkah demi langkah pada `Execution Procedure` secara presisi. Jangan melakukan modifikasi alur kerja tanpa proses *Change Request*.
4. **Sertakan Bukti Nyata (Evidence):**  
   Setiap hasil eksekusi skill wajib didukung oleh bukti nyata (potongan log, screenshot di `Docs/Evidence/SS/`, atau diff kode).

---

## 4. Rujukan Standar & Template Terkait
- Arsitektur Skill: [`Docs/Standards/Skill_Architecture.md`](../Docs/Standards/Skill_Architecture.md)
- Tata Kelola & Siklus Hidup: [`Docs/Standards/Skill_Lifecycle_Governance.md`](../Docs/Standards/Skill_Lifecycle_Governance.md)
- Validasi & Pengujian Skill: [`Docs/Standards/Skill_Validation_Testing.md`](../Docs/Standards/Skill_Validation_Testing.md)
- Registri Skill Terpusat: [`Docs/Skill_Registry.md`](../Docs/Skill_Registry.md)
- Formulir Spesifikasi Skill Baru: [`Docs/Skill_Specification_Template.md`](../Docs/Skill_Specification_Template.md)
