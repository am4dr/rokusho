package com.github.am4dr.rokusho.core.metadata

data class RecordTag(val base: BaseTagName, val data: TagData) {
    constructor(name: String, data: Map<String, String>) : this(BaseTagName(name), TagData(data))

    override fun equals(other: Any?): Boolean = other is RecordTag && other.base == base
    override fun hashCode(): Int = base.hashCode()
}