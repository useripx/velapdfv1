package com.njagakneai.velapdf.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.njagakneai.velapdf.domain.model.DocumentSummary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mengekspor hasil Ringkasan & Kesimpulan Bahasa Indonesia ASK Vela ke berkas PDF offline via iText7.
 */
@Singleton
class SummaryPdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun exportSummaryToPdf(summary: DocumentSummary): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val exportDir = File(context.cacheDir, "pdf_exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val sanitizedTitle = summary.metadata.fileName
                .substringBeforeLast(".")
                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                .take(36)
                .ifBlank { "Dokumen" }

            val dateStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val outputFile = File(exportDir, "Ringkasan_${sanitizedTitle}_$dateStamp.pdf")

            PdfWriter(outputFile).use { writer ->
                PdfDocument(writer).use { pdfDoc ->
                    Document(pdfDoc).use { document ->
                        val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
                        val regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

                        document.add(
                            Paragraph("LAPORAN RINGKASAN & KESIMPULAN OFFLINE — VELA PDF (ASK VELA)")
                                .setFont(boldFont)
                                .setFontSize(14f)
                        )

                        document.add(
                            Paragraph(
                                "Dokumen: ${summary.metadata.fileName}\n" +
                                    "Jenis Dokumen: ${summary.detectedDocumentType}\n" +
                                    "Topik Dominan: ${summary.dominantTopic}\n" +
                                    "Cakupan Halaman: ${summary.metadata.pageRangeLabel} (${summary.metadata.pageCount} halaman)\n" +
                                    "Sumber/Penulis: ${summary.documentIssuer} | Tanggal Tertera: ${summary.documentDate}\n" +
                                    "Rasio Pemadatan: ${summary.compressionRatioPercentage}% (${summary.totalOriginalWords} -> ${summary.summaryWordsCount} kata)"
                            )
                                .setFont(regularFont)
                                .setFontSize(10f)
                        )

                        document.add(
                            Paragraph("\nKESIMPULAN UTAMA (BAHASA INDONESIA)")
                                .setFont(boldFont)
                                .setFontSize(12f)
                        )
                        document.add(
                            Paragraph(summary.conclusionParagraph)
                                .setFont(regularFont)
                                .setFontSize(11f)
                        )

                        document.add(
                            Paragraph("\nPOIN-POIN INTISARI")
                                .setFont(boldFont)
                                .setFontSize(12f)
                        )
                        if (summary.keyPoints.isEmpty()) {
                            document.add(
                                Paragraph("- Tidak ada kalimat intisari tambahan yang diekstrak.")
                                    .setFont(regularFont)
                                    .setFontSize(10f)
                            )
                        } else {
                            summary.keyPoints.forEachIndexed { idx, point ->
                                val pageNo = summary.pageReferences[point]
                                val pageSuffix = if (pageNo != null) " [Hal. $pageNo]" else ""
                                document.add(
                                    Paragraph("${idx + 1}. $point$pageSuffix")
                                        .setFont(regularFont)
                                        .setFontSize(10.5f)
                                )
                            }
                        }

                        if (summary.obligationsAndDeadlines.isNotEmpty()) {
                            document.add(
                                Paragraph("\nBUTIR KEWAJIBAN, ATURAN & TEMUAN INFERENSI")
                                    .setFont(boldFont)
                                    .setFontSize(12f)
                            )
                            summary.obligationsAndDeadlines.forEach { item ->
                                document.add(
                                    Paragraph("• $item")
                                        .setFont(regularFont)
                                        .setFontSize(10f)
                                )
                            }
                        }

                        if (summary.extractedSpoFacts.isNotEmpty()) {
                            document.add(
                                Paragraph("\nFAKTA SEMANTIK SPO TERDETEKSI (${summary.extractedSpoFacts.size} Fakta)")
                                    .setFont(boldFont)
                                    .setFontSize(11f)
                            )
                            summary.extractedSpoFacts.take(15).forEach { fact ->
                                document.add(
                                    Paragraph("• [${fact.predicate}] ${fact.objectValue} (Hal. ${fact.pageNumber})")
                                        .setFont(regularFont)
                                        .setFontSize(9.5f)
                                )
                            }
                        }

                        document.add(
                            Paragraph("\nDihasilkan secara 100% offline oleh Mesin Rule-Based Inference & NLP Lokal VelaPDF.")
                                .setFont(regularFont)
                                .setFontSize(8.5f)
                        )
                    }
                }
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                outputFile
            )
            Result.success(contentUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
