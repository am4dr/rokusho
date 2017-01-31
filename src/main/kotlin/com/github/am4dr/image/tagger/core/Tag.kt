package com.github.am4dr.image.tagger.core

import com.github.am4dr.rokusho.core.Tag

// TODO 他の種類のタグも追加する
/*
interface Tag {
    val id: String
    val data: Map<String, Any>
}
*/
data class TextTag(
        override val id: String,
        override val data: Map<String, Any> = mapOf()) : Tag {
    override val type: TagType = TagType.TEXT
}

enum class TagType {
    TEXT, VALUE, SELECTION, OTHERS;
    companion object {
        fun from(string: String): TagType =
                when (string) {
                    "text" -> TEXT
                    "value" -> VALUE
                    "selection" -> SELECTION
                    else -> OTHERS
                }
    }
}
class TagParser {
    // TODO erase companion
    companion object {
        fun parse(string: String): Tag = TextTag(string)
    }
}
data class TagInfo(val type: TagType, val data: Map<String, Any>)