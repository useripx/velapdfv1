package com.njagakneai.velapdf.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history_table")
data class AnalysisHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val documentTitle: String,
    val documentType: String,
    val dominantTopic: String,
    val summaryText: String,
    val conclusionText: String,
    val pageRangeLabel: String,
    val factsCount: Int,
    val sourceUri: String,
    val timestamp: Long
)
