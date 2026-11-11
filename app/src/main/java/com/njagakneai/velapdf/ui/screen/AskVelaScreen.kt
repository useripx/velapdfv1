package com.njagakneai.velapdf.ui.screen

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import com.njagakneai.velapdf.ui.components.AnalysisProgressOverlay
import com.njagakneai.velapdf.ui.viewmodel.AnalysisTaskState
import com.njagakneai.velapdf.ui.viewmodel.DocumentAnalysisViewModel
import com.njagakneai.velapdf.ui.viewmodel.PdfInspectionInfo
import com.njagakneai.velapdf.utils.FileUriHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskVelaScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSummary: () -> Unit,
    onNavigateToOcrPreview: () -> Unit,
    viewModel: DocumentAnalysisViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val taskState by viewModel.taskState.collectAsState()
    val inspectedPdf by viewModel.inspectedPdf.collectAsState()
    val inspectError by viewModel.inspectError.collectAsState()
    val analysisHistory by viewModel.analysisHistory.collectAsState()
    val localPdfHistory by viewModel.localPdfHistory.collectAsState()

    var pendingImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pendingImageTitle by remember { mutableStateOf("Dokumen Pindai") }

    LaunchedEffect(taskState) {
        when (taskState) {
            is AnalysisTaskState.SuccessSummary -> {
                viewModel.consumeTaskState()
                onNavigateToSummary()
            }
            is AnalysisTaskState.ReadyForOcrPreview -> {
                viewModel.consumeTaskState()
                onNavigateToOcrPreview()
            }
            else -> Unit
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = FileUriHelper.getFileName(context, uri).ifBlank { "Dokumen_PDF.pdf" }
            viewModel.inspectPdf(uri, fileName)
        }
    }

    val galleryImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            pendingImageTitle = "Foto_Dokumen_${uris.size}_Halaman"
            pendingImageUris = uris
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val pageUris = scanResult?.pages?.mapNotNull { it.imageUri }.orEmpty()
            if (pageUris.isNotEmpty()) {
                pendingImageTitle = "Pindai_Kamera_${pageUris.size}_Halaman"
                pendingImageUris = pageUris
            } else {
                val scannedPdfUri = scanResult?.pdf?.uri
                if (scannedPdfUri != null) {
                    viewModel.inspectPdf(scannedPdfUri, "Hasil_Pindai.pdf")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ASK Vela",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ringkasan & Kesimpulan Dokumen (100% Luring)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Asisten Ringkasan & Kesimpulan Bahasa Indonesia",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Unggah PDF (seperti buku IoT/ajar atau surat resmi), pindai halaman buku dengan kamera, atau pilih gambar. Mesin Rule-Based Inference lokal bekerja 100% offline.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Pilih Sumber Dokumen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SourceOptionCard(
                        icon = Icons.Default.UploadFile,
                        title = "Unggah Berkas PDF",
                        subtitle = "Pilih PDF & tentukan semua atau rentang halaman (mis. Hal. 23–35)",
                        onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SourceOptionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.DocumentScanner,
                            title = "Pindai Kamera",
                            subtitle = "Foto halaman buku langsung",
                            onClick = {
                                val options = GmsDocumentScannerOptions.Builder()
                                    .setGalleryImportAllowed(true)
                                    .setPageLimit(35)
                                    .setResultFormats(
                                        GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                                        GmsDocumentScannerOptions.RESULT_FORMAT_PDF
                                    )
                                    .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                                    .build()
                                val activity = context as? Activity
                                if (activity != null) {
                                    GmsDocumentScanning.getClient(options)
                                        .getStartScanIntent(activity)
                                        .addOnSuccessListener { sender ->
                                            scannerLauncher.launch(
                                                IntentSenderRequest.Builder(sender).build()
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Gagal membuka pemindai: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        )

                        SourceOptionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Collections,
                            title = "Pilih Gambar",
                            subtitle = "Multi-foto dari galeri",
                            onClick = { galleryImagesLauncher.launch("image/*") }
                        )
                    }
                }
            }

            if (inspectError != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = inspectError.orEmpty(),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            IconButton(onClick = { viewModel.clearInspectedPdf() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            inspectedPdf?.let { pdfInfo ->
                item {
                    PdfPageRangeCard(
                        pdfInfo = pdfInfo,
                        onDismiss = { viewModel.clearInspectedPdf() },
                        onRunAnalysis = { startPage, endPage, previewOcr ->
                            viewModel.analyzePdf(
                                pdfUri = pdfInfo.uri,
                                fileName = pdfInfo.displayName,
                                startPage = startPage,
                                endPage = endPage,
                                previewOcrBeforeSummary = previewOcr
                            )
                        }
                    )
                }
            }

            val availableLocalPdfs = localPdfHistory.filter { entity ->
                val path = entity.filePath
                path.startsWith("content://") || File(path).exists()
            }.take(5)

            if (availableLocalPdfs.isNotEmpty()) {
                item {
                    Text(
                        text = "Pilih dari PDF Lokal Terakhir",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(availableLocalPdfs, key = { "local-${it.id}" }) { entity ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val resolvedUri = if (entity.filePath.startsWith("content://")) {
                                    Uri.parse(entity.filePath)
                                } else {
                                    FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.provider",
                                        File(entity.filePath)
                                    )
                                }
                                viewModel.inspectPdf(resolvedUri, entity.fileName)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entity.fileName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Ketuk untuk pilih rentang halaman & ringkas",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Riwayat Ringkasan Tersimpan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (analysisHistory.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada riwayat ringkasan dokumen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(analysisHistory, key = { "hist-${it.id}" }) { itemEntity ->
                    AnalysisHistoryCard(
                        entity = itemEntity,
                        onOpen = { viewModel.loadSavedAnalysis(itemEntity.id) },
                        onDelete = { viewModel.deleteSavedAnalysis(itemEntity.id) }
                    )
                }
            }
        }
    }

    if (pendingImageUris.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { pendingImageUris = emptyList() },
            title = {
                Text(
                    text = "${pendingImageUris.size} Halaman Siap Dianalisis",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Pilih cara memproses halaman yang telah dipindai: langsung buat ringkasan AI luring atau periksa teks OCR terlebih dahulu."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uris = pendingImageUris
                        val title = pendingImageTitle
                        pendingImageUris = emptyList()
                        viewModel.analyzeImages(uris, title, previewOcrBeforeSummary = false)
                    }
                ) {
                    Text("Ringkasan Langsung")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val uris = pendingImageUris
                        val title = pendingImageTitle
                        pendingImageUris = emptyList()
                        viewModel.analyzeImages(uris, title, previewOcrBeforeSummary = true)
                    }
                ) {
                    Text("Edit Teks OCR")
                }
            }
        )
    }

    AnalysisProgressOverlay(
        taskState = taskState,
        onRetry = { viewModel.retryLastAnalysis() },
        onDismissError = { viewModel.consumeTaskState() }
    )
}

@Composable
private fun SourceOptionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PdfPageRangeCard(
    pdfInfo: PdfInspectionInfo,
    onDismiss: () -> Unit,
    onRunAnalysis: (startPage: Int, endPage: Int, previewOcr: Boolean) -> Unit
) {
    var useCustomRange by remember(pdfInfo.uri) { mutableStateOf(pdfInfo.totalPages > 35) }
    var startText by remember(pdfInfo.uri) { mutableStateOf("1") }
    var endText by remember(pdfInfo.uri) {
        mutableStateOf(minOf(pdfInfo.totalPages, 35).toString())
    }

    val parsedStart = startText.toIntOrNull()
    val parsedEnd = endText.toIntOrNull()
    val rangeError: String? = when {
        !useCustomRange -> null
        parsedStart == null || parsedEnd == null -> "Masukkan nomor halaman berupa angka valid."
        parsedStart < 1 || parsedEnd > pdfInfo.totalPages -> "Rentang harus antara halaman 1 s.d. ${pdfInfo.totalPages}."
        parsedStart > parsedEnd -> "Halaman awal tidak boleh lebih besar dari halaman akhir."
        (parsedEnd - parsedStart + 1) > 35 -> "Maksimal 35 halaman per analisis agar perangkat tetap responsif."
        else -> null
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pdfInfo.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Total tersedia: ${pdfInfo.totalPages} halaman",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Batal")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { useCustomRange = false }
            ) {
                RadioButton(
                    selected = !useCustomRange,
                    onClick = { useCustomRange = false }
                )
                Text(
                    text = if (pdfInfo.totalPages <= 35) {
                        "Semua Halaman (Hal. 1 s.d. ${pdfInfo.totalPages})"
                    } else {
                        "Otomatis 35 Halaman Pertama (Hal. 1 s.d. 35)"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { useCustomRange = true }
            ) {
                RadioButton(
                    selected = useCustomRange,
                    onClick = { useCustomRange = true }
                )
                Text(
                    text = "Pilih Rentang Halaman Tertentu (mis. Hal. 23 s.d. 35)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (useCustomRange) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = startText,
                        onValueChange = { startText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Dari Hal.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endText,
                        onValueChange = { endText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Sampai Hal.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (rangeError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = rangeError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val finalStart = if (useCustomRange) (parsedStart ?: 1) else 1
            val finalEnd = if (useCustomRange) (parsedEnd ?: minOf(pdfInfo.totalPages, 35)) else minOf(pdfInfo.totalPages, 35)
            val isValid = rangeError == null

            Button(
                onClick = { onRunAnalysis(finalStart, finalEnd, false) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buat Ringkasan & Kesimpulan (Offline)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onRunAnalysis(finalStart, finalEnd, true) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pratinjau / Edit Teks OCR Dulu")
            }
        }
    }
}

@Composable
private fun AnalysisHistoryCard(
    entity: AnalysisHistoryEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val locale = LocalLocale.current.platformLocale
    val dateStr = remember(entity.timestamp, locale) {
        SimpleDateFormat("dd MMM yyyy, HH:mm", locale).format(Date(entity.timestamp))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = entity.documentType,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus Ringkasan",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = entity.documentTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Cakupan: ${entity.pageRangeLabel} • ${entity.factsCount} fakta SPO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = entity.conclusionText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
