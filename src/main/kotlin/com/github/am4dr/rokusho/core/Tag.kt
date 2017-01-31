package com.github.am4dr.rokusho.core

interface Tag {
    val id: String
    val type: TagType
    val data: Map<String, Any>
}

data class SimpleTag(
        override val id: String,
        override val type: TagType,
        override val data: Map<String, Any>) : Tag

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
    companion object {
        fun parse(string: String): Tag = TextTag(string)
    }
}
