package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty

interface Library<T> {
    val metaDataRegistry: MetaDataRegistry<T>
    val recordLists: ReadOnlyListProperty<ObservableRecordList<T>>
}