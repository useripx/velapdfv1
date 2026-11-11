-- ============================================================================
-- METODOLOGI PENELITIAN TA 2026/2027 - PROYEK VELAPDF
-- Mahasiswa  : Yogi Ario Pratama (NPM: 2313020004)
-- Rumpun     : Sains Data & Kecerdasan Buatan (DS & AI) - Jalur Rekayasa (R&D)
-- Dosen Pemb.: Rony Heri Irawan, M.Kom.
-- 
-- SKEMA DATA FISIK (PHYSICAL DATA MODEL / PDM) BASIS DATA RELASIONAL
-- Target DBMS: SQLite 3.x / Android Room Persistence Library
-- Asas Kunci : Integritas Referensial, Pencegahan Orphan Records,
--              dan Keselarasan dengan Pemodelan OOAD (Class Diagram).
-- ============================================================================

-- Bersihkan tabel terdahulu jika sudah ada (Idempotensi Eksekusi)
DROP TABLE IF EXISTS tb_ringkasan_inferensi;
DROP TABLE IF EXISTS tb_fakta_semantik;
DROP TABLE IF EXISTS tb_dokumen;

-- ----------------------------------------------------------------------------
-- TABEL 1: tb_dokumen (Tabel Entitas Master Dokumen)
-- Deskripsi: Menyimpan metadata berkas fisik dokumen (PDF/Citra) yang dipindai,
--            identifikasi waktu, ukuran byte, serta nilai otentisitas SHA-256.
-- ----------------------------------------------------------------------------
CREATE TABLE tb_dokumen (
    dokumen_id          VARCHAR(64)     NOT NULL,
    nama_berkas         VARCHAR(255)    NOT NULL,
    uri_lokal           TEXT            NOT NULL,
    tipe_mime           VARCHAR(50)     NOT NULL DEFAULT 'application/pdf',
    ukuran_byte         INTEGER         NOT NULL CHECK(ukuran_byte > 0),
    checksum_sha256     VARCHAR(64)     NOT NULL,
    jumlah_halaman      INTEGER         NOT NULL DEFAULT 1 CHECK(jumlah_halaman >= 1),
    status_pemrosesan   VARCHAR(30)     NOT NULL DEFAULT 'MENUNGGU_ANALISIS'
                                        CHECK(status_pemrosesan IN ('MENUNGGU_ANALISIS', 'OCR_BERJALAN', 'RINGKASAN_SELESAI', 'GAGAL')),
    waktu_unggah        INTEGER         NOT NULL, -- Unix Epoch Timestamp (milidetik)
    waktu_analisis_ms   INTEGER         DEFAULT 0 CHECK(waktu_analisis_ms >= 0),
    
    -- Batasan Kunci & Keunikan
    CONSTRAINT pk_tb_dokumen PRIMARY KEY (dokumen_id),
    CONSTRAINT uq_tb_dokumen_checksum UNIQUE (checksum_sha256)
);

-- Indeks performa penelusuran status dan riwayat dokumen
CREATE INDEX idx_dokumen_status ON tb_dokumen (status_pemrosesan);
CREATE INDEX idx_dokumen_waktu ON tb_dokumen (waktu_unggah DESC);

-- ----------------------------------------------------------------------------
-- TABEL 2: tb_fakta_semantik (Tabel Fakta Semantik Berbasis Ontologi)
-- Deskripsi: Menyimpan atom fakta berstruktur Subjek-Predikat-Objek (SPO)
--            hasil pemetaan teks OCR dan NLP ke taksonomi ontologi dokumen.
-- ----------------------------------------------------------------------------
CREATE TABLE tb_fakta_semantik (
    fakta_id            VARCHAR(64)     NOT NULL,
    dokumen_id          VARCHAR(64)     NOT NULL,
    subjek              VARCHAR(128)    NOT NULL,
    predikat            VARCHAR(128)    NOT NULL,
    objek_teks          TEXT            NOT NULL,
    kategori_fakta      VARCHAR(50)     NOT NULL
                                        CHECK(kategori_fakta IN (
                                            'METADATA_DOKUMEN',
                                            'KLASIFIKASI_DOKUMEN',
                                            'KLAUSUL_KEWAJIBAN',
                                            'KLAUSUL_LARANGAN',
                                            'KLAUSUL_HAK',
                                            'BATAS_WAKTU_DEADLINE',
                                            'POIN_INTISARI'
                                        )),
    nomor_halaman       INTEGER         NOT NULL DEFAULT 1 CHECK(nomor_halaman >= 1),
    kalimat_sumber      TEXT            NOT NULL,
    skor_keyakinan      REAL            NOT NULL DEFAULT 1.0 CHECK(skor_keyakinan >= 0.0 AND skor_keyakinan <= 1.0),
    
    -- Batasan Kunci & Integritas Referensial
    CONSTRAINT pk_tb_fakta_semantik PRIMARY KEY (fakta_id),
    CONSTRAINT fk_fakta_ke_dokumen FOREIGN KEY (dokumen_id)
        REFERENCES tb_dokumen (dokumen_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Indeks relasional untuk mempercepat query fakta per dokumen dan per predikat
CREATE INDEX idx_fakta_dokumen_id ON tb_fakta_semantik (dokumen_id);
CREATE INDEX idx_fakta_kategori ON tb_fakta_semantik (kategori_fakta);

-- ----------------------------------------------------------------------------
-- TABEL 3: tb_ringkasan_inferensi (Tabel Luaran Intisari & Deduksi Aturan)
-- Deskripsi: Menyimpan hasil klasifikasi jenis dokumen, intisari penting,
--            klausul kewajiban/tenggat waktu, dan metrik pemadatan ringkasan.
-- ----------------------------------------------------------------------------
CREATE TABLE tb_ringkasan_inferensi (
    ringkasan_id        VARCHAR(64)     NOT NULL,
    dokumen_id          VARCHAR(64)     NOT NULL,
    jenis_dokumen       VARCHAR(128)    NOT NULL,
    judul_perihal       TEXT            NOT NULL,
    pihak_penerbit      VARCHAR(255)    NOT NULL,
    tanggal_dokumen     VARCHAR(100)    NOT NULL,
    poin_intisari_json  TEXT            NOT NULL, -- Format JSON Array string teks intisari
    klausul_aksi_json   TEXT            NOT NULL, -- Format JSON Array string kewajiban & batas waktu
    rasio_pemadatan     REAL            NOT NULL DEFAULT 0.0 CHECK(rasio_pemadatan >= 0.0 AND rasio_pemadatan <= 100.0),
    aturan_tertembak    INTEGER         NOT NULL DEFAULT 0 CHECK(aturan_tertembak >= 0),
    waktu_eksekusi      INTEGER         NOT NULL, -- Unix Epoch Timestamp (milidetik)
    
    -- Batasan Kunci & Integritas Referensial
    CONSTRAINT pk_tb_ringkasan_inferensi PRIMARY KEY (ringkasan_id),
    CONSTRAINT fk_ringkasan_ke_dokumen FOREIGN KEY (dokumen_id)
        REFERENCES tb_dokumen (dokumen_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Indeks relasional untuk pelaporan hasil ringkasan per dokumen
CREATE INDEX idx_ringkasan_dokumen_id ON tb_ringkasan_inferensi (dokumen_id);
CREATE INDEX idx_ringkasan_jenis_dokumen ON tb_ringkasan_inferensi (jenis_dokumen);

-- ============================================================================
-- CATATAN PENJELAS RELASI DAN PENCEGAHAN ORPHAN RECORDS:
-- 1. Relasi tb_dokumen (1) ke tb_fakta_semantik (N):
--    Satu berkas dokumen memuat banyak fakta atomik SPO. Penerapan 'ON DELETE CASCADE'
--    menjamin jika berkas induk dihapus dari riwayat oleh pengguna, seluruh fakta
--    ekstraksinya otomatis terhapus bersih dari basis data SQLite (mencegah orphan records).
--
-- 2. Relasi tb_dokumen (1) ke tb_ringkasan_inferensi (1):
--    Satu dokumen yang selesai dianalisis memiliki satu catatan ringkasan intisari
--    terpadu. Penerapan 'ON DELETE CASCADE' dan 'ON UPDATE CASCADE' menjamin
--    konsistensi transaksional ACID lokal pada SQLite Android.
-- ============================================================================
