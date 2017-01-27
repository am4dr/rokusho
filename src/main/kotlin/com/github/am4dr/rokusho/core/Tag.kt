package com.github.am4dr.rokusho.core

import com.github.am4dr.image.tagger.core.TagType

interface Tag {
    val id: String
    val type: TagType
    val data: Map<String, Any>
}

data class SimpleTag(
        override val id: String,
        override val type: TagType,
        override val data: Map<String, Any>) : Tag