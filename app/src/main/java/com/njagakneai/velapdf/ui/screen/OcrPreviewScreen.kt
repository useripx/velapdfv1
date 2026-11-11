package com.njagakneai.velapdf.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.njagakneai.velapdf.ui.components.AnalysisProgressOverlay
import com.njagakneai.velapdf.ui.viewmodel.AnalysisTaskState
import com.njagakneai.velapdf.ui.viewmodel.DocumentAnalysisViewModel
import com.njagakneai.velapdf.ui.viewmodel.OcrState
import com.njagakneai.velapdf.ui.viewmodel.OcrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrPreviewScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSummary: () -> Unit,
    ocrViewModel: OcrViewModel = hiltViewModel(),
    analysisViewModel: DocumentAnalysisViewModel = hiltViewModel()
) {
    val ocrState by ocrViewModel.ocrState.collectAsState()
    val extractedTexts by ocrViewModel.extractedTexts.collectAsState()
    val documentTitle by ocrViewModel.documentTitle.collectAsState()
    val analysisTaskState by analysisViewModel.taskState.collectAsState()

    LaunchedEffect(Unit) {
        ocrViewModel.syncFromRepository()
    }

    LaunchedEffect(analysisTaskState) {
        if (analysisTaskState is AnalysisTaskState.SuccessSummary) {
            analysisViewModel.consumeTaskState()
            onNavigateToSummary()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pratinjau & Edit Teks OCR", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Koreksi hasil bacaan sebelum diringkas",
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
        },
        bottomBar = {
            if (extractedTexts.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                analysisViewModel.analyzePendingOrEditedPages(
                                    pages = ocrViewModel.toOcrPageTexts(),
                                    documentTitle = documentTitle
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Buat Ringkasan & Kesimpulan (Offline)",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (ocrState is OcrState.Processing) {
                val processing = ocrState as OcrState.Processing
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Membaca Teks OCR Halaman ${processing.currentPage} dari ${processing.totalPages}...",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else if (extractedTexts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada teks OCR yang diekstrak. Silakan pindai atau unggah dokumen terlebih dahulu.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(extractedTexts, key = { it.pageNumber }) { textData ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "Halaman ${textData.pageNumber}",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${textData.rawText.split(Regex("\\s+")).count { it.isNotBlank() }} kata",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (!textData.isSuccess) {
                                    Text(
                                        text = "Gagal memindai teks: ${textData.errorMessage ?: "Kesalahan tidak diketahui"}",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    var textFieldValue by remember(textData.pageNumber, textData.rawText) {
                                        mutableStateOf(textData.rawText)
                                    }
                                    OutlinedTextField(
                                        value = textFieldValue,
                                        onValueChange = { newText ->
                                            textFieldValue = newText
                                            ocrViewModel.updateTextForPage(textData.pageNumber, newText)
                                        },
                                        label = { Text("Teks Terekstrak (Dapat Diedit)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 4
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    AnalysisProgressOverlay(
        taskState = analysisTaskState,
        onRetry = { analysisViewModel.retryLastAnalysis() },
        onDismissError = { analysisViewModel.consumeTaskState() }
    )
}
