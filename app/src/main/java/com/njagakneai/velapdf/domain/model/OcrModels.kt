package com.njagakneai.velapdf.domain.model

import android.net.Uri

data class ScannedPage(
    val pageNumber: Int,
    val imageUri: Uri
)

data class ExtractedText(
    val pageNumber: Int,
    val rawText: String,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)
