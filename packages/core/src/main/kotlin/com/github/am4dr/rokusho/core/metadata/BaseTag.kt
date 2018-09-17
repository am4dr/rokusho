package com.github.am4dr.rokusho.core.metadata

data class BaseTag(val name: BaseTagName, val data: TagData) {
    constructor(name: String, defaultData: Map<String, String>) : this(BaseTagName(name), TagData(defaultData))

    override fun equals(other: Any?): Boolean = other is BaseTag && other.name == name
    override fun hashCode(): Int = name.hashCode()
}