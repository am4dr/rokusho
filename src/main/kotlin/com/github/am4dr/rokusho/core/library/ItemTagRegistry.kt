package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyMapProperty

interface ItemTagRegistry<T> {
    fun getKeys(): Set<T>
    fun get(key: T): List<ItemTag>
    fun set(key: T, itemTags: List<ItemTag>)
    fun remove(key: T)
    val itemTags: ReadOnlyMapProperty<T, List<ItemTag>>
}
