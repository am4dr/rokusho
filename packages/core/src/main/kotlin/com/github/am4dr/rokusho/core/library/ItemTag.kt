package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.metadata.BaseTag
import com.github.am4dr.rokusho.core.metadata.TagData

data class ItemTag(val base: BaseTag, val patch: TagData) {
    val data: TagData by lazy { base.data.merge(patch) }
}