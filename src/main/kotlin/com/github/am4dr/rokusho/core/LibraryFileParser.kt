package com.github.am4dr.rokusho.core

import com.github.am4dr.image.tagger.core.SaveFile
import com.github.am4dr.image.tagger.core.TagInfo
import com.github.am4dr.image.tagger.core.TagType
import java.nio.file.Path
import com.github.am4dr.image.tagger.core.Tag as OldTag
import com.github.am4dr.rokusho.core.Tag as NewTag

interface LibraryFileParser {
    fun parse(path: Path): Library
}
class DefaultLibraryFileParser : LibraryFileParser {
    override fun parse(path: Path): PathLibrary {
        val s = SaveFile.parse(path.toFile().readText())
        val tags = s.tags.map { e: Map.Entry<String, TagInfo> -> SimpleTag(e.key, e.value.type, e.value.data) }
        val items = s.metaData.map { e -> SimpleLibraryItemMetaData(e.key.joinToString("/"), e.value.tags.map(::TagAdaptor)) }
        return SimplePathLibrary(path.parent, SimpleLibrary(tags, items))
    }
}

// TODO remove adaptors
class TagAdaptor(id: String, type: TagType, data: Map<String, Any>) : NewTag, OldTag {
    constructor(tag: NewTag) : this(tag.id, tag.type, toOldDataFormat(tag.data, tag.type))
    constructor(tag: OldTag) : this(tag.name, tag.data["type"] as? TagType ?: TagType.TEXT, tag.data)
    override val name: String = id
    override val id: String = id
    override val type: TagType = type
    override val data: Map<String, Any> = data
}
private fun toOldDataFormat(data: Map<String, Any>, type: TagType): Map<String, Any> =
        mutableMapOf<String, Any>().apply {
            putAll(data)
            put("type", type)
        }
