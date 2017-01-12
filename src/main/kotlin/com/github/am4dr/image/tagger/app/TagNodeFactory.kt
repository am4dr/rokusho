package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Tag
import com.github.am4dr.image.tagger.core.TagInfo
import com.github.am4dr.image.tagger.core.TagType
import com.github.am4dr.image.tagger.node.TagNode
import com.github.am4dr.image.tagger.node.TextTagNode
import javafx.beans.binding.StringBinding
import javafx.beans.property.MapProperty

class TagNodeFactory(val prototypes: MapProperty<String, TagInfo>) {
    fun createTagNode(tag: Tag): TagNode =
        TextTagNode(object : StringBinding() {
            init {
                super.bind(
                        if (prototypes.containsKey(tag.name)) prototypes.valueAt(tag.name)
                        else prototypes)
            }
            override fun computeValue(): String = toTextFormat(tag)
        })
    fun toTextFormat(tag: Tag): String =
        when (prototypes[tag.name]?.let(TagInfo::type)) {
            TagType.TEXT, null -> tag.name
            TagType.VALUE -> "${tag.name} | ${tag.data["value"]?.let { it } ?: "-"}"
            TagType.SELECTION -> "${tag.name} | ${tag.data["value"]?.let {it} ?: "-"}"
            TagType.OTHERS -> tag.name
        }
}
