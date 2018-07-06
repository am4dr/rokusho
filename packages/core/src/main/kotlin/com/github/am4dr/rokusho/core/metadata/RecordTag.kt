package com.github.am4dr.rokusho.core.metadata

data class RecordTag(val name: TagName, val data: TagData) {
    constructor(name: String, data: Map<String, String>) : this(TagName(name), TagData(data))

    override fun equals(other: Any?): Boolean = other is RecordTag && other.name == name
    override fun hashCode(): Int = name.hashCode()
}