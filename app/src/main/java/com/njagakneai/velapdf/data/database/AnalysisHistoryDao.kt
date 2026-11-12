package com.njagakneai.velapdf.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.njagakneai.velapdf.data.model.AnalysisHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(entity: AnalysisHistoryEntity): Long

    @Query("SELECT * FROM analysis_history_table ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<AnalysisHistoryEntity>>

    @Query("SELECT * FROM analysis_history_table WHERE id = :id LIMIT 1")
    suspend fun getAnalysisById(id: Int): AnalysisHistoryEntity?

    @Query("DELETE FROM analysis_history_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM analysis_history_table WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("DELETE FROM analysis_history_table")
    suspend fun clearAll()
}
