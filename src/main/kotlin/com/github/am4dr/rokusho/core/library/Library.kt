package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty

interface Library<T> {
    val metaDataRegistry: MetaDataRegistry<T>
    val tagRegistry: TagRegistry
    val recordLists: ReadOnlyListProperty<ObservableRecordList<T>>

    fun createRecordList(list: Iterable<T>): ObservableRecordList<T>
}
