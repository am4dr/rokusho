package com.github.am4dr.rokusho.core

import java.nio.file.Path

interface ParsedLibrary : Library {
    val savefilePath: Path
    fun toLibraryItems(path: List<Path>): List<Pair<Path, LibraryItemMetaData>> {
        val m = getItemMetaData()
        return path.map {
            val id = toIdFormat(it)
            Pair(it, m.find { it.id == id } ?: SimpleLibraryItemMetaData(id))
        }
    }
    fun toIdFormat(path: Path): String = savefilePath.parent.relativize(path).joinToString("/")
}

class SimpleParsedLibrary(
        override val savefilePath: Path,
        private val library: Library)
    : ParsedLibrary, Library by library {
    constructor(savefilePath: Path, tags: List<Tag> = listOf(), itemMetaData: List<LibraryItemMetaData> = listOf())
            : this(savefilePath, SimpleLibrary(tags, itemMetaData))
}
