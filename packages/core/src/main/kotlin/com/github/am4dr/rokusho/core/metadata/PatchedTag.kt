package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.util.DataObject

data class PatchedTag(val base: BaseTag, val patchData: DataObject) {

    val data: DataObject by lazy { base.data.merge(patchData) }

    override fun toString(): String = "PatchedTag($base, patch=$patchData)"
}