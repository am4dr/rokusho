package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Tag
import com.github.am4dr.rokusho.core.library.TagType
import javafx.beans.binding.StringBinding
import javafx.beans.property.ReadOnlyMapProperty

class TagNodeFactory(val prototypes: ReadOnlyMapProperty<String, out Tag>) {
    fun createTagNode(tag: ItemTag): TagNode =
        TextTagNode(object : StringBinding() {
            init {
                super.bind(
                        if (prototypes.containsKey(tag.name)) prototypes.valueAt(tag.name)
                        else prototypes)
            }
            override fun computeValue(): String = toTextFormat(tag)
        })
    fun toTextFormat(tag: ItemTag): String =
        prototypes[tag.name]?.let { pt ->
            when (pt.type) {
                TagType.TEXT -> tag.value
                TagType.VALUE     -> "${tag.name} | ${tag.value?.takeIf { it.isNotBlank() } ?: "-" }"
                TagType.SELECTION -> "${tag.name} | ${tag.value?.takeIf { it.isNotBlank() } ?: "-" }"
                TagType.OTHERS -> tag.name
            }
        } ?: tag.name
}
