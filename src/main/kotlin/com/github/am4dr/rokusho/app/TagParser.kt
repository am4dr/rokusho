package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType

interface TagStringParser {
    fun parse(string: String): Tag
}
class DefaultTagStringParser(val base: Map<String, ObservableTag> = mapOf()) : TagStringParser {
    override fun parse(string: String): ObservableTag {
        return base[string]
                ?.let { DerivedObservableTag(it, mapOf()) }
                ?: SimpleObservableTag(string, TagType.TEXT, mapOf("value" to string))
    }
}
class BaseUpdatingTagStringParser(
        val base: Map<String, ObservableTag> = mutableMapOf(),
        private val addToBase: (ObservableTag) -> ObservableTag) : TagStringParser {
    override fun parse(string: String): ObservableTag {
        val baseTag = base[string] ?: addToBase(SimpleObservableTag(string, TagType.TEXT, mapOf("value" to string)))
        return DerivedObservableTag(baseTag, mapOf())
    }
}