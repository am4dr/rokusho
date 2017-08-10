package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.core.library.Tag
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty

interface RokushoLibrary<T> {
    val tags: ReadOnlyMapProperty<String, Tag>
    val itemTags: ReadOnlyMapProperty<T, List<ItemTag>>
    val recordLists: ReadOnlyListProperty<ObservableRecordList<T>>

    fun createRecordList(list: Iterable<T>): ObservableRecordList<T>
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}
