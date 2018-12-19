package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.adapter.DataStoreConverter
import com.github.am4dr.rokusho.core.datastore.savedata.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.core.library.provider.LibraryProvider
import java.nio.file.Paths

/**
 * 新しいcoreを実行してみるためのmainクラス
 *
 * TODO ブランチをマージする前には削除する
 * required envs:
 * TEMP_MAIN_TARGET_DIR: 対象のディレクトリ
 */

class TempMain {
    companion object {
        const val TEMP_MAIN_TARGET_DIR: String = "TEMP_MAIN_TARGET_DIR"
        @JvmStatic
        fun main(args: Array<String>) {
            val targetDir = System.getenv(TEMP_MAIN_TARGET_DIR) ?: error("env $TEMP_MAIN_TARGET_DIR not found")

            val providers = loadProviders()
            println("""
                |PROVIDERS::
                |${providers.mapNotNull { "${it.javaClass.name}: ${it.description}" }.joinToString("\n")}
                |""".trimMargin())
            val libraryCollection = LibraryCollection(providers)
            libraryCollection.loadPathLibrary(Paths.get(targetDir))
            libraryCollection.getLibraries().forEach { lib ->
                lib?.let {
                    println("""
                        |LIBRARY::
                        |$lib
                        |
                        |TAGS::
                        |${lib.getTags()}
                        |
                        |ITEMS::
                        |${lib.getItems().toList()}
                        |""".trimMargin())
                } ?: println("lib is null")
            }
        }

        private fun loadProviders(): Set<LibraryProvider<*>> =
                setOf(FileSystemBasedLibraryProvider(FileBasedMetaDataRepositories { savefile ->
                    DataStoreConverter(YamlSaveDataStore(savefile))
                }))
    }
}