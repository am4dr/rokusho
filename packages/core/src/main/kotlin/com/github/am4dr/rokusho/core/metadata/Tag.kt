package com.github.am4dr.rokusho.core.metadata

data class Tag(val name: TagName, val defaultData: TagData) {
    constructor(name: String, defaultData: Map<String, String>) : this(TagName(name), TagData(defaultData))

    override fun equals(other: Any?): Boolean = other is Tag && other.name == name
    override fun hashCode(): Int = name.hashCode()
}