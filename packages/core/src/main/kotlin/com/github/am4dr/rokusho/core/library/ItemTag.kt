package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.metadata.BaseTag
import com.github.am4dr.rokusho.core.util.DataObject

data class ItemTag(val base: BaseTag, val patch: DataObject) {
    val data: DataObject by lazy { base.data.merge(patch) }
}