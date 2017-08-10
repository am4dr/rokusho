package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.javafx.collection.toObservableMap
import javafx.beans.property.*
import javafx.collections.FXCollections

class SimplifiedLibrary<E, K>(val getItemSequence: () -> Sequence<E>, val idExtractor: (E) -> K) {
    val tags: SetProperty<Tag> = SimpleSetProperty(FXCollections.observableSet())
    val indexedTags: ReadOnlyMapProperty<String, Tag> = SimpleMapProperty(toObservableMap(tags, Tag::id))
    val itemTags: MapProperty<K, List<ItemTag>> = SimpleMapProperty(FXCollections.observableHashMap())
}