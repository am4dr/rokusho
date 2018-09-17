package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.util.DataObject

data class RecordTag(val base: BaseTagName, val data: DataObject) {
    constructor(name: String, data: Map<String, String>) : this(BaseTagName(name), DataObject(data))

    override fun equals(other: Any?): Boolean = other is RecordTag && other.base == base
    override fun hashCode(): Int = base.hashCode()
}