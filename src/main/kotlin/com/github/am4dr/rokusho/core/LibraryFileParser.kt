package com.github.am4dr.rokusho.core

import java.nio.file.Path

interface LibraryFileParser {
    fun parse(path: Path): Library
}
class DefaultLibraryFileParser : LibraryFileParser {
    override fun parse(path: Path): ParsedLibrary {
        val s = YamlSaveFileParser.parse(path.toFile().readText())
        val tags = s.tags.map { e: Map.Entry<String, Tag> -> SimpleTag(e.key, e.value.type, e.value.data) }
        val items = s.metaData.map { e -> SimpleLibraryItemMetaData(e.key.joinToString("/"), e.value.tags) }
        return SimpleParsedLibrary(path, SimpleLibrary(tags, items))
    }
}
