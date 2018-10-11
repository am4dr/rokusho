package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.provider.LibraryProvider
import com.github.am4dr.rokusho.core.library.provider.LibraryProviderCollection
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


            val libraryProvider = LibraryProviderCollection(loadProviders())

            val descriptor = FileSystemBasedLibraryProvider.createDescriptor(Paths.get(targetDir).toUri())
            val lib = libraryProvider.get(descriptor)

            lib?.let {
                println(lib)
                println("""
                    |TAGS::
                    |${lib.getTags()}
                    |
                    |ITEMS::
                    |${lib.getIDs().mapNotNull(lib::get)}
                    """.trimMargin())
            } ?: println("lib is null")
        }

        private fun loadProviders(): Set<LibraryProvider<*>> {
            val providers = setOf(FileSystemBasedLibraryProvider())
            if (providers.isEmpty()) {
                println("providers not found!")
            }
            else {
                providers.forEach { println("${it.javaClass.name}: ${it.description}") }
            }
            return providers
        }
    }
}