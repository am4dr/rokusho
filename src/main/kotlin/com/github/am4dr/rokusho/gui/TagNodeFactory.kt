package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType
import javafx.beans.binding.StringBinding
import javafx.beans.property.MapProperty

class TagNodeFactory(val prototypes: MapProperty<String, out Tag>) {
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
            TagType.TEXT, null -> tag.data["value"]?.toString() ?: tag.id
            TagType.VALUE -> "${tag.id} | ${tag.data["value"] ?: "-"}"
            TagType.SELECTION -> "${tag.id} | ${tag.data["value"] ?: "-"}"
            TagType.OTHERS -> tag.id
        }
}