package com.github.am4dr.rokusho.core.library

import javafx.collections.FXCollections
import javafx.collections.ObservableMap

class Library<T>(records: List<Record<T>> = listOf(), tags: List<Tag> = listOf()) {
    val records: ObservableMap<T, Record<T>> = records.associateByTo(FXCollections.observableHashMap(), Record<T>::key)
    val tags: ObservableMap<String, Tag> = tags.associateByTo(FXCollections.observableHashMap(), Tag::id)
}