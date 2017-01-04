package com.github.am4dr.image.tagger.core

// TODO 他の種類のタグも追加する
interface Tag {
    val name: String
    val data: Map<String, Any>
}
data class TextTag(
        override val name: String,
        override val data: Map<String, Any> = mapOf()
        ) : Tag

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