package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.adapter.DataStoreConverter
import com.github.am4dr.rokusho.core.datastore.savedata.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.library.provider.LibraryProvider
import kotlinx.coroutines.Dispatchers
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
        private val eventDispatcherContext = Dispatchers.Default

        @JvmStatic
        fun main(args: Array<String>) {
            val targetDir = System.getenv(TEMP_MAIN_TARGET_DIR) ?: error("env $TEMP_MAIN_TARGET_DIR not found")

            val providers = loadProviders()
            println("""
                |PROVIDERS::
                |${providers.joinToString("\n") { "${it.javaClass.name}: ${it.description}" }}
                |""".trimMargin())
            val libraryCollection = LibraryCollection(providers)
            libraryCollection.loadPathLibrary(Paths.get(targetDir))
            libraryCollection.getLibraries().forEach { lib ->
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
            }
        }

        private fun loadProviders(): Set<LibraryProvider<*>> =
                setOf(FileSystemBasedLibraryProvider(FileBasedMetaDataRepositories { savefile ->
                    DataStoreConverter(YamlSaveDataStore(savefile))
                }, eventDispatcherContext))
    }
}