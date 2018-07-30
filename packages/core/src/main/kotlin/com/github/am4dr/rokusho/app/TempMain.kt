package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.LibraryProviderCollection
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
        @JvmStatic
        fun main(args: Array<String>) {
            val providers = setOf(FileSystemBasedLibraryProvider())
            val libraryProviders = LibraryProviderCollection(providers)

            if (libraryProviders.providers.isEmpty()) {
                println("providers not found!")
            }
            else {
                libraryProviders.providers.forEach { println("${it.javaClass.name}: ${it.description}") }
            }
            val homeURI = Paths.get(System.getenv("TEMP_MAIN_TARGET_DIR")).toUri()
            val lib = libraryProviders.get(FileSystemBasedLibraryProvider.createDescriptor(homeURI))
            lib?.let {
                println(lib)
                println("""
                    |TAGS::
                    |${lib.getTags()}
                    |
                    |RECORDS::
                    |${lib.getRecords()}
                    |
                    |ITEMS::
                    |${lib.items}
                    """.trimMargin())
            } ?: println("lib is null")
        }
    }
}