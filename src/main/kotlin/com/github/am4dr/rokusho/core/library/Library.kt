package com.github.am4dr.rokusho.core.library

import javafx.beans.property.MapProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections

class Library<E, K>(val getItemSequence: () -> Sequence<E>, val idExtractor: (E) -> K) {
    val tags: ReadOnlyMapProperty<String, Tag> = SimpleMapProperty(FXCollections.observableHashMap())
    val itemTags: MapProperty<K, List<ItemTag>> = SimpleMapProperty(FXCollections.observableHashMap())
    val items: List<E> = getItemSequence().toList()
}