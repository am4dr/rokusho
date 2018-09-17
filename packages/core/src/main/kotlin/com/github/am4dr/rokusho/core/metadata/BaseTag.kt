package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.util.DataObject

data class BaseTag(val name: BaseTagName, val data: DataObject) {
    constructor(name: String, defaultData: Map<String, String>) : this(BaseTagName(name), DataObject(defaultData))

    override fun equals(other: Any?): Boolean = other is BaseTag && other.name == name
    override fun hashCode(): Int = name.hashCode()
}