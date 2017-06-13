package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyMapProperty

interface Library<T> {
    fun getItem(key: T): Item<T>
    fun getItemSet(list: Iterable<T>): ItemSet<T>
    fun getTags(): ReadOnlyMapProperty<String, Tag>
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}