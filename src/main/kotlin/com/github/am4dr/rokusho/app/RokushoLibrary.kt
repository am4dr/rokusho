package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.RecordListWatcher
import com.github.am4dr.rokusho.core.library.Tag
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty

interface RokushoLibrary<T> {
    val tags: ReadOnlyMapProperty<String, Tag>
    val itemTags: ReadOnlyMapProperty<T, List<ItemTag>>
    val recordLists: ReadOnlyListProperty<RecordListWatcher<T>.Records>

    fun createRecordList(list: Iterable<T>): RecordListWatcher<T>.Records
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}
