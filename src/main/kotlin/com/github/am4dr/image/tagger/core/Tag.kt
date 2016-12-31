package com.github.am4dr.image.tagger.core

// TODO 他の種類のタグも追加する
interface Tag {
    val text: String
    val type: String    // TODO Enumにする
    val data: Map<String, Any>
}
data class DefaultTag(
        override val text: String,
        override val type: String = "text",
        override val data: Map<String, Any> = mapOf()
        ) : Tag {
}

class TagParser {
    // TODO erase companion
    companion object {
        fun parse(string: String): Tag = DefaultTag(string)
    }
}