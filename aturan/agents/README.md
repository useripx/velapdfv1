# DIREKTORI MASTER PROFIL AGEN AI (AI AGENTS REPOSITORY)

Selamat datang di repositori operasional **AI Agent Profiles**. Direktori ini menyimpan seluruh spesifikasi profil resmi, batas kewenangan (*authority limits*), tanggung jawab, alat teknis terotorisasi, dan aturan eskalasi dari setiap entitas agen AI yang beroperasi dalam ekosistem *Universal AI Engineering Standard*.

---

## 1. Filosofi & Prinsip Tata Kelola Agen

- **Prinsip Utama:**  
  $$\mathbf{Every\ Agent\ Has\ a\ Role,\ Every\ Role\ Has\ Authority\ Limits,\ Every\ Action\ Has\ Accountability.}$$
- **Pemisahan Peran Tegas (*Role Separation*):**  
  Setiap agen wajib mematuhi batas perannya masing-masing. Dilarang keras mengambil alih wewenang agen lain, dilarang meloloskan review mandiri tanpa pengujian independen, dan dilarang memodifikasi berkas di luar ruang lingkup yang disetujui.
- **Hierarki Otoritas:**  
  $$\text{Level 1 Universal Governance} \longrightarrow \text{Project Owner (Yogi Ario)} \longrightarrow \text{AI Manager} \longrightarrow \text{Executing Agents}$$

---

## 2. Struktur Direktori Profil Agen

```text
agents/
├── README.md                      <- Berkas panduan ini
├── lead-developer.md              <- Profil AGT-PRD-DEV-001 (Bagus Restu / Lead Developer)
├── ai-manager.md                  <- Profil AGT-MGT-MGR-001 (AI Project Manager)
├── qa-agent.md                    <- Profil AGT-QLT-QAE-001 (AI QA Engineer)
└── reviewer-agent.md              <- Profil AGT-QLT-REV-001 (AI Technical Reviewer)
```

---

## 3. Kewajiban Operasional bagi Setiap Agen AI

Sebelum mengeksekusi tugas apa pun dalam repositori ini, agen **MUST**:
1. **Baca Profil Anda Sendiri:** Pahami batasan wewenang Anda pada berkas profil terkait di direktori ini.
2. **Konsultasikan Matriks Kewenangan:** Periksa [`Docs/Agent_Authority_Matrix.md`](../Docs/Agent_Authority_Matrix.md) untuk memastikan tindakan yang hendak diambil sah.
3. **Patuhi Protokol Serah Terima:** Gunakan formulir [`Docs/Agent_Handoff_Template.md`](../Docs/Agent_Handoff_Template.md) saat mendelegasikan atau menerima pekerjaan dari agen lain.
4. **Tegakkan Bounded Recovery:** Jika terjadi error, ikuti prosedur pada [`Docs/Standards/Error_Handling_Recovery.md`](../Docs/Standards/Error_Handling_Recovery.md) (maksimal 3x percobaan, catat di `Docs/Agent_Incident_Log.md`, jangan sembunyikan kegagalan).

---

## 4. Rujukan Standar Terkait
- Master Arsitektur Agen: [`Docs/Standards/Agent_Architecture_Governance.md`](../Docs/Standards/Agent_Architecture_Governance.md)
- Matriks Hak Akses: [`Docs/Agent_Authority_Matrix.md`](../Docs/Agent_Authority_Matrix.md)
- Orkestrasi & Handoff: [`Docs/Standards/Agent_Orchestration_Handoff.md`](../Docs/Standards/Agent_Orchestration_Handoff.md)
- Tata Kelola Alat: [`Docs/Standards/Tool_Governance_Execution.md`](../Docs/Standards/Tool_Governance_Execution.md)
- Penanganan Cacat & Pemulihan: [`Docs/Standards/Error_Handling_Recovery.md`](../Docs/Standards/Error_Handling_Recovery.md)
- Template Profil Baru: [`Docs/Agent_Definition_Template.md`](../Docs/Agent_Definition_Template.md)
