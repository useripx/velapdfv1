package com.njagakneai.velapdf.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import com.njagakneai.velapdf.data.model.HistoryEntity

@Database(
    entities = [HistoryEntity::class, AnalysisHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun analysisHistoryDao(): AnalysisHistoryDao
}
