package com.njagakneai.velapdf.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.njagakneai.velapdf.domain.model.DocumentSummary
import com.njagakneai.velapdf.ui.viewmodel.DocumentAnalysisViewModel
import com.njagakneai.velapdf.ui.viewmodel.PdfExportState
import com.njagakneai.velapdf.utils.FileShareHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DocumentSummaryScreen(
    onNavigateBack: () -> Unit,
    viewModel: DocumentAnalysisViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val summary by viewModel.activeSummary.collectAsState()
    val exportState by viewModel.pdfExportState.collectAsState()

    var showFactsSection by remember { mutableStateOf(false) }
    var showRulesSection by remember { mutableStateOf(false) }
    var exportedReadyUri by remember { mutableStateOf<android.net.Uri?>(null) }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is PdfExportState.Exported -> {
                viewModel.consumeExportState()
                if (state.shareImmediately) {
                    FileShareHelper.shareFile(context, state.pdfUri)
                } else {
                    exportedReadyUri = state.pdfUri
                }
            }
            is PdfExportState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.consumeExportState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ringkasan & Kesimpulan", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Dihasilkan Luring (Bahasa Indonesia)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentSummary = summary
        if (currentSummary == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Belum ada hasil ringkasan yang aktif.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onNavigateBack) {
                    Text("Kembali")
                }
            }
            return@Scaffold
        }

        val plainFormattedText = remember(currentSummary) {
            buildShareableSummaryText(currentSummary)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Metadata & Klasifikasi Dokumen
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = currentSummary.detectedDocumentType,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentSummary.metadata.fileName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Topik Utama: ${currentSummary.dominantTopic}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cakupan: ${currentSummary.metadata.pageRangeLabel} • Penerbit/Sumber: ${currentSummary.documentIssuer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (currentSummary.totalOriginalWords > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
                            ) {
                                Text(
                                    text = "Pemadatan Teks: ${currentSummary.compressionRatioPercentage}% (${currentSummary.totalOriginalWords} -> ${currentSummary.summaryWordsCount} kata)",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // 2. RINGKASAN ISI DOKUMEN (DOCUMENT SUMMARIZATION)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ringkasan Isi Dokumen (Document Summarization)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentSummary.conclusionParagraph,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // 3. Kata Kunci Dominan
            if (currentSummary.topKeywords.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Kata Kunci Dominan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            currentSummary.topKeywords.forEach { kw ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(kw) }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Ringkasan Perwakilan Halaman / Bagian (Stratified Document Summarization)
            val representativePoints = currentSummary.contentSummaryPoints.ifEmpty { currentSummary.keyPoints }
            if (representativePoints.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ringkasan Perwakilan Halaman / Bagian",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            representativePoints.forEachIndexed { idx, point ->
                                val pageRef = currentSummary.pageReferences[point]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${idx + 1}.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = point,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (pageRef != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "Hal. $pageRef",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Poin Penting Berbasis Aturan (Rule-Based Key-Point Extraction)
            val ruleKeyPoints = currentSummary.ruleBasedKeyPoints.ifEmpty { currentSummary.obligationsAndDeadlines }
            if (ruleKeyPoints.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Poin Penting Berbasis Aturan (Rule-Based Key Points)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            ruleKeyPoints.distinct().forEach { bullet ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircleOutline,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = bullet,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Accordion Fakta Semantik SPO & Pemetaan Properti Ontologi
            if (currentSummary.extractedSpoFacts.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showFactsSection = !showFactsSection },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fakta Semantik SPO & Ontologi (${currentSummary.extractedSpoFacts.size})",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = if (showFactsSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                            if (showFactsSection) {
                                Spacer(modifier = Modifier.height(8.dp))
                                currentSummary.extractedSpoFacts.forEach { fact ->
                                    val ontologyBadge = if (fact.ontologyClass != null && fact.ontologyProperty != null) {
                                        " -> [${fact.ontologyClass}.${fact.ontologyProperty}]"
                                    } else {
                                        ""
                                    }
                                    Text(
                                        text = "• [${fact.subject}] ${fact.predicate} -> ${fact.objectValue}$ontologyBadge (Hal. ${fact.pageNumber})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                                if (currentSummary.ontologyPropertyBindings.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Pemetaan properti Ontologi terisi: " +
                                            currentSummary.ontologyPropertyBindings.joinToString(", ") {
                                                "${it.className}.${it.propertyName}"
                                            },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (currentSummary.missingRequiredProperties.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Properti wajib Ontologi belum terdeteksi: ${currentSummary.missingRequiredProperties.joinToString(", ")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Accordion Jejak Forward Chaining
            if (currentSummary.inferenceResult.inferenceTraces.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showRulesSection = !showRulesSection },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Rule,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Jejak Rule-Based Inference (${currentSummary.inferenceResult.firedRulesCount}/${currentSummary.inferenceResult.executedRulesCount} Aturan)",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = if (showRulesSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                            if (showRulesSection) {
                                Spacer(modifier = Modifier.height(8.dp))
                                currentSummary.inferenceResult.inferenceTraces.forEach { trace ->
                                    Text(
                                        text = "• ${trace.firedRuleId} (${trace.firedRuleName}): ${trace.deductionResult}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 8. Tombol Aksi (UX #3 Opsi A): Salin, Bagikan Teks, Ekspor PDF
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(plainFormattedText))
                                Toast.makeText(
                                    context,
                                    "Ringkasan disalin ke papan klip",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salin Teks")
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Ringkasan: ${currentSummary.metadata.fileName}")
                                    putExtra(Intent.EXTRA_TEXT, plainFormattedText)
                                }
                                context.startActivity(
                                    Intent.createChooser(sendIntent, "Bagikan Ringkasan via")
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bagikan Teks")
                        }
                    }

                    Button(
                        onClick = { viewModel.exportCurrentSummaryToPdf(shareImmediately = false) },
                        enabled = exportState !is PdfExportState.Exporting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (exportState is PdfExportState.Exporting) {
                                "Membuat Berkas PDF..."
                            } else {
                                "Ekspor Ringkasan ke PDF"
                            }
                        )
                    }
                }
            }
        }
    }

    exportedReadyUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { exportedReadyUri = null },
            title = { Text("PDF Ringkasan Siap", fontWeight = FontWeight.Bold) },
            text = { Text("Ringkasan dan kesimpulan Bahasa Indonesia telah diekspor ke PDF secara offline.") },
            confirmButton = {
                Button(
                    onClick = {
                        val openIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        runCatching {
                            context.startActivity(Intent.createChooser(openIntent, "Buka PDF"))
                        }.onFailure {
                            FileShareHelper.shareFile(context, uri)
                        }
                        exportedReadyUri = null
                    }
                ) {
                    Text("Buka PDF")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        FileShareHelper.shareFile(context, uri)
                        exportedReadyUri = null
                    }
                ) {
                    Text("Bagikan PDF")
                }
            }
        )
    }
}

private fun buildShareableSummaryText(summary: DocumentSummary): String {
    return buildString {
        appendLine("=== RINGKASAN & KESIMPULAN VELA PDF (OFFLINE) ===")
        appendLine("Dokumen: ${summary.metadata.fileName}")
        appendLine("Jenis: ${summary.detectedDocumentType}")
        appendLine("Topik: ${summary.dominantTopic}")
        appendLine("Cakupan: ${summary.metadata.pageRangeLabel}")
        appendLine()
        appendLine("RINGKASAN ISI DOKUMEN (DOCUMENT SUMMARIZATION):")
        appendLine(summary.conclusionParagraph)
        val representativePoints = summary.contentSummaryPoints.ifEmpty { summary.keyPoints }
        if (representativePoints.isNotEmpty()) {
            appendLine()
            appendLine("RINGKASAN PERWAKILAN HALAMAN / BAGIAN:")
            representativePoints.forEachIndexed { idx, p ->
                val pg = summary.pageReferences[p]?.let { " [Hal. $it]" } ?: ""
                appendLine("${idx + 1}. $p$pg")
            }
        }
        val ruleKeyPoints = summary.ruleBasedKeyPoints.ifEmpty { summary.obligationsAndDeadlines }
        if (ruleKeyPoints.isNotEmpty()) {
            appendLine()
            appendLine("POIN PENTING BERBASIS ATURAN (RULE-BASED KEY POINTS):")
            ruleKeyPoints.distinct().forEach { b ->
                appendLine("- $b")
            }
        }
    }
}
