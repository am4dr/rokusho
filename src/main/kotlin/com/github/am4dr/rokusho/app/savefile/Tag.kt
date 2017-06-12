package com.github.am4dr.rokusho.app.savefile

interface Tag {
    val id: String
    val type: TagType
    val data: Map<String, Any>
}

data class SimpleTag(
        override val id: String,
        override val type: TagType,
        override val data: Map<String, Any> = mapOf()) : Tag

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
