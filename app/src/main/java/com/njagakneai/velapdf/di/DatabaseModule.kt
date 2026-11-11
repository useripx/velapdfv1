package com.njagakneai.velapdf.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.njagakneai.velapdf.data.database.AnalysisHistoryDao
import com.njagakneai.velapdf.data.database.AppDatabase
import com.njagakneai.velapdf.data.database.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `analysis_history_table` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `documentTitle` TEXT NOT NULL,
                    `documentType` TEXT NOT NULL,
                    `dominantTopic` TEXT NOT NULL,
                    `summaryText` TEXT NOT NULL,
                    `conclusionText` TEXT NOT NULL,
                    `pageRangeLabel` TEXT NOT NULL,
                    `factsCount` INTEGER NOT NULL,
                    `sourceUri` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "velapdf_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    fun provideAnalysisHistoryDao(appDatabase: AppDatabase): AnalysisHistoryDao {
        return appDatabase.analysisHistoryDao()
    }
}
