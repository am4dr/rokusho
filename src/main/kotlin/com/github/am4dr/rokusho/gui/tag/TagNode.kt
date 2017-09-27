package com.github.am4dr.rokusho.gui.tag

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.TagType

class TagNode(itemTag: ItemTag) {
    companion object {
        fun ItemTag.toTextFormat(): String =
                tag.let { pt ->
                    when (pt.type) {
                        TagType.TEXT -> value
                        TagType.VALUE     -> "${tag.id} | ${value?.takeIf { it.isNotBlank() } ?: "-" }"
                        TagType.SELECTION -> "${tag.id} | ${value?.takeIf { it.isNotBlank() } ?: "-" }"
                        TagType.OTHERS -> tag.id
                    }
                } ?: tag.id
    }
    val view = TagView(itemTag.toTextFormat())
}