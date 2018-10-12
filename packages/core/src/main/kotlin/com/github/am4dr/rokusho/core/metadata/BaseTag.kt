package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.util.DataObject

data class BaseTag(val name: Name, val data: DataObject) {
    constructor(name: String, defaultData: Map<String, String>) : this(Name(name), DataObject(defaultData))

    override fun equals(other: Any?): Boolean = other is BaseTag && other.name == name
    override fun hashCode(): Int = name.hashCode()
    override fun toString(): String = "BaseTag($name, $data)"

    data class Name(val name: String) {
        override fun toString(): String = "Name($name)"
    }
}