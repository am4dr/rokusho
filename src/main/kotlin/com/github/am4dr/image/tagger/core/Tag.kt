package com.github.am4dr.image.tagger.core

// TODO 他の種類のタグも追加する
interface Tag {
    val name: String
    val data: Map<String, Any>
}
data class TextTag(
        override val name: String,
        override val data: Map<String, Any> = mapOf()
        ) : Tag {
}

class TagParser {
    // TODO erase companion
    companion object {
        fun parse(string: String): Tag = TextTag(string)
    }
}
data class TagInfo(val type: String, val data: Map<String, Any>)