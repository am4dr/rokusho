package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.node.TagNode
import com.github.am4dr.image.tagger.node.TextTagNode
import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType
import javafx.beans.binding.StringBinding
import javafx.beans.property.MapProperty

class TagNodeFactory(val prototypes: MapProperty<String, Tag>) {
    fun createTagNode(tag: Tag): TagNode =
        TextTagNode(object : StringBinding() {
            init {
                super.bind(
                        if (prototypes.containsKey(tag.id)) prototypes.valueAt(tag.id)
                        else prototypes)
            }
            override fun computeValue(): String = toTextFormat(tag)
        })
    fun toTextFormat(tag: Tag): String =
        when (prototypes[tag.id]?.let(Tag::type)) {
            TagType.TEXT, null -> tag.id
            TagType.VALUE -> "${tag.id} | ${tag.data["value"]?.let { it } ?: "-"}"
            TagType.SELECTION -> "${tag.id} | ${tag.data["value"]?.let {it} ?: "-"}"
            TagType.OTHERS -> tag.id
        }
}
