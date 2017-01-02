package com.github.am4dr.image.tagger.core

// TODO 他の種類のタグも追加する
interface Tag {
    val name: String
    val text: String
    val type: String    // TODO Enumにする
    val data: Map<String, Any>
}
data class TextTag(
        override val name: String,
        override val type: String = "text",
        override val data: Map<String, Any> = mapOf()
        ) : Tag {
    override val text: String = name
}

class TagParser {
    // TODO erase companion
    companion object {
        fun parse(string: String): Tag = TextTag(string)
    }
}