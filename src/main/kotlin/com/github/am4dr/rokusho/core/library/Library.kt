package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlySetProperty

interface Library<T> {
    val tags: ReadOnlySetProperty<Tag>
    val itemTags: ReadOnlyMapProperty<T, List<ItemTag>>
    val recordLists: ReadOnlyListProperty<ObservableRecordList<T>>

    fun createRecordList(list: Iterable<T>): ObservableRecordList<T>
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}
