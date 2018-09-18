package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.util.DataObject

data class RecordTag(val base: BaseTagName, val patchData: DataObject) {
    constructor(name: String, data: Map<String, String>) : this(BaseTagName(name), DataObject(data))
}