package com.njagakneai.velapdf.testutil

import com.njagakneai.velapdf.domain.model.AssetTextProvider
import java.io.File

/**
 * Pembaca berkas JSON asli dari app/src/main/assets/ saat JVM unit test berjalan tanpa Android Context.
 */
object TestAssetProvider : AssetTextProvider {

    override fun readAsset(fileName: String): String {
        val candidates = listOf(
            File("src/main/assets/$fileName"),
            File("app/src/main/assets/$fileName")
        )
        val resolved = candidates.firstOrNull { it.exists() }
            ?: error("Berkas aset '$fileName' tidak ditemukan pada kandidat: ${candidates.map { it.absolutePath }}")
        return resolved.readText(Charsets.UTF_8)
    }
}
