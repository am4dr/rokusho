package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyMapProperty

interface Library<T> {
    fun getRecord(key: T): Record<T>
    fun getRecordList(list: Iterable<T>): ObservableRecordList<T>
    fun getTags(): ReadOnlyMapProperty<String, Tag>
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}