package com.njagakneai.velapdf.di

import android.content.Context
import com.njagakneai.velapdf.domain.model.AssetTextProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalysisModule {

    @Provides
    @Singleton
    fun provideAssetTextProvider(
        @ApplicationContext context: Context
    ): AssetTextProvider {
        return AssetTextProvider { assetFileName ->
            context.assets.open(assetFileName).bufferedReader(Charsets.UTF_8).use { reader ->
                reader.readText()
            }
        }
    }
}
