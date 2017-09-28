package com.github.am4dr.rokusho.gui.tag

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Tag

class TagNode(itemTag: ItemTag) {
    companion object {
        fun ItemTag.toTextFormat(): String =
                tag.let { pt ->
                    when (pt.type) {
                        Tag.Type.TEXT -> value
                        Tag.Type.VALUE     -> "${tag.id} | ${value?.takeIf { it.isNotBlank() } ?: "-" }"
                        Tag.Type.SELECTION -> "${tag.id} | ${value?.takeIf { it.isNotBlank() } ?: "-" }"
                        Tag.Type.OTHERS -> tag.id
                    }
                } ?: tag.id
    }
    val view = TagView(itemTag.toTextFormat())
}