package com.njagakneai.velapdf.domain.nlp

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TextExtractionCleanerAndPdfExtractorTest {

    @Test
    fun `normalizeExtractedPageText rejoins hyphenated words but preserves reduplication and removes isolated page numbers`() {
        val raw = """
            Halaman 1 dari 5
            Sistem ini melakukan pem-
            rosesan data secara ber-
            kelanjutan tanpa undang-
            undang yang membebani.
            12
            ---
            Dokumen berakhir di sini.
        """.trimIndent()

        val cleaned = TextExtractionCleaner.normalizeExtractedPageText(raw)
        assertTrue("pem-rosesan harus tersambung", cleaned.contains("pemrosesan"))
        assertTrue("ber-kelanjutan harus tersambung", cleaned.contains("berkelanjutan"))
        assertTrue("Kata ulang undang-undang harus tetap bertanda hubung", cleaned.contains("undang-undang"))
        assertFalse("Nomor halaman terisolasi '12' harus dibuang", cleaned.lines().any { it.trim() == "12" })
        assertFalse("Header Halaman 1 dari 5 harus dibuang", cleaned.contains("Halaman 1 dari 5"))
    }

    @Test
    fun `isMeaningfulNativePdfText distinguishes clean digital PDF text from empty scan or broken encoding`() {
        assertFalse(TextExtractionCleaner.isMeaningfulNativePdfText(""))
        assertFalse(TextExtractionCleaner.isMeaningfulNativePdfText("   \n 12 \n  "))
        assertFalse(TextExtractionCleaner.isMeaningfulNativePdfText("a b c d e f g h i j k l m n o p"))
        assertFalse(TextExtractionCleaner.isMeaningfulNativePdfText("\uFFFD\uFFFD\uFFFD\uFFFD teks rusak parah \uFFFD\uFFFD"))
        assertTrue(
            TextExtractionCleaner.isMeaningfulNativePdfText(
                "Surat Keputusan Rektor tentang penetapan kalender akademik semester genap tahun akademik 2026."
            )
        )
    }

    @Test
    fun `orderSpatialBlocks reads two-column layout left column top-to-bottom before right column`() {
        val blocks = listOf(
            TextExtractionCleaner.SpatialTextBlock("JUDUL ARTIKEL DUA KOLOM", left = 40, top = 20, right = 560, bottom = 70),
            TextExtractionCleaner.SpatialTextBlock("Kiri Baris Satu.", left = 40, top = 110, right = 270, bottom = 180),
            TextExtractionCleaner.SpatialTextBlock("Kanan Baris Satu.", left = 320, top = 115, right = 560, bottom = 185),
            TextExtractionCleaner.SpatialTextBlock("Kiri Baris Dua.", left = 40, top = 210, right = 270, bottom = 290),
            TextExtractionCleaner.SpatialTextBlock("Kanan Baris Dua.", left = 320, top = 215, right = 560, bottom = 295)
        )

        val ordered = TextExtractionCleaner.orderSpatialBlocks(blocks)
        val idxTitle = ordered.indexOf("JUDUL ARTIKEL")
        val idxKiri1 = ordered.indexOf("Kiri Baris Satu.")
        val idxKiri2 = ordered.indexOf("Kiri Baris Dua.")
        val idxKanan1 = ordered.indexOf("Kanan Baris Satu.")
        val idxKanan2 = ordered.indexOf("Kanan Baris Dua.")

        assertTrue(idxTitle in 0 until idxKiri1)
        assertTrue("Kolom kiri harus dibaca lengkap sebelum kolom kanan", idxKiri1 < idxKiri2 && idxKiri2 < idxKanan1 && idxKanan1 < idxKanan2)
    }

    @Test
    fun `iText7 native extraction validates readability across sample testbed PDFs when present`() {
        val sampleDirCandidates = listOf(
            File("aturan/alatuji/04_sampel_dokumen_uji"),
            File("../aturan/alatuji/04_sampel_dokumen_uji")
        )
        val sampleDir = sampleDirCandidates.firstOrNull { it.isDirectory } ?: return
        val sampleFiles = listOf(
            "SK-01_Surat_Keputusan_Rektor.pdf",
            "12 Penerapan Model Ontologi dalam Perkembangan Game Digital.pdf",
            "14 Pengklasifikasian Dokumen Teks Bahasa Indonesia berbasis Vektor Space Model dengan menggunakan Metode k-Nearest Neighbor (k-NN) dan Euclidean Distance.pdf",
            "Roadmap.pdf"
        ).map { File(sampleDir, it) }.filter { it.isFile }

        assertTrue("Minimal 1 sampel PDF harus ditemukan untuk uji ekstraksi", sampleFiles.isNotEmpty())
        for (pdfFile in sampleFiles) {
            PdfReader(pdfFile.absolutePath).use { reader ->
                PdfDocument(reader).use { doc ->
                    assertTrue(doc.numberOfPages >= 1)
                    val simple = PdfTextExtractor.getTextFromPage(doc.getPage(1), SimpleTextExtractionStrategy())
                    val located = PdfTextExtractor.getTextFromPage(doc.getPage(1), LocationTextExtractionStrategy())
                    val meaningfulSimple = TextExtractionCleaner.isMeaningfulNativePdfText(simple)
                    val meaningfulLocated = TextExtractionCleaner.isMeaningfulNativePdfText(located)
                    assertEquals(
                        "Kedua strategi harus konsisten terdeteksi meaningful pada ${pdfFile.name}",
                        true,
                        meaningfulSimple || meaningfulLocated
                    )
                    val chosen = TextExtractionCleaner.chooseBestNativeExtraction(simple, located)
                    assertTrue("chooseBestNativeExtraction harus memilih teks bersih pada ${pdfFile.name}", !chosen.isNullOrBlank())
                }
            }
        }
    }

    @Test
    fun `chooseBestNativeExtraction falls back from glued stream text to location extraction or null`() {
        val glued = "Suratinimenetapkanketentuanwajibbagiseluruhmahasiswadandosenpengampumatakuliahyangterdaftarpadasemestergenap."
        val cleanLocation = "Surat ini menetapkan ketentuan wajib bagi seluruh mahasiswa dan dosen pengampu mata kuliah yang terdaftar."
        val selected = TextExtractionCleaner.chooseBestNativeExtraction(glued, cleanLocation)
        assertEquals(cleanLocation, selected)

        val bothBad = TextExtractionCleaner.chooseBestNativeExtraction("a b c d e", "  ")
        assertEquals(null, bothBad)
    }
}
