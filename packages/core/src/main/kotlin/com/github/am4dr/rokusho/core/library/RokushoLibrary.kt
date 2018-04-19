package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.collections.ObservableList

interface RokushoLibrary<T> {
    val tags: ReadOnlyMapProperty<String, Tag>
    val records: ReadOnlyListProperty<Record<T>>
    val recordLists: ReadOnlyListProperty<ObservableList<Record<T>>>

    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}
