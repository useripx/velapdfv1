package com.njagakneai.velapdf.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.njagakneai.velapdf.ui.viewmodel.OcrState
import com.njagakneai.velapdf.ui.viewmodel.OcrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrPreviewScreen(
    viewModel: OcrViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAnalysis: () -> Unit
) {
    val ocrState by viewModel.ocrState.collectAsState()
    val extractedTexts by viewModel.extractedTexts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pratinjau Hasil OCR") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (ocrState is OcrState.Success) {
                        IconButton(onClick = onNavigateToAnalysis) {
                            Icon(Icons.Default.Check, contentDescription = "Lanjut Analisis")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (ocrState) {
                is OcrState.Idle -> {
                    // Placeholder
                }
                is OcrState.Processing -> {
                    val state = ocrState as OcrState.Processing
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Mengekstrak teks halaman ${state.currentPage} dari ${state.totalPages}...")
                    }
                }
                is OcrState.Error -> {
                    val state = ocrState as OcrState.Error
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Terjadi Kesalahan:", color = MaterialTheme.colorScheme.error)
                        Text(state.message)
                        Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Kembali")
                        }
                    }
                }
                is OcrState.Success -> {
                    if (extractedTexts.isEmpty()) {
                        Text(
                            text = "Tidak ada teks yang berhasil diekstrak.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(extractedTexts) { textData ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Halaman ${textData.pageNumber}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        var textFieldValue by remember { mutableStateOf(textData.rawText) }
                                        
                                        OutlinedTextField(
                                            value = textFieldValue,
                                            onValueChange = { 
                                                textFieldValue = it
                                                viewModel.updateText(textData.pageNumber, it)
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            minLines = 3
                                        )
                                        
                                        if (!textData.isSuccess) {
                                            Text(
                                                text = "Error: ${textData.errorMessage}",
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
