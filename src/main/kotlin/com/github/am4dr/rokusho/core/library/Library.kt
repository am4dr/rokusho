package com.github.am4dr.rokusho.core.library

import javafx.beans.property.MapProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections

class Library<E, K>(val getItemSequence: () -> Sequence<E>,
                    val idExtractor: (E) -> K) {

    val tags: MapProperty<String, Tag> = SimpleMapProperty(FXCollections.observableHashMap())
    val itemTags: MapProperty<K, List<ItemTag>> = SimpleMapProperty(FXCollections.observableHashMap())
    val items: List<E> get() = getItemSequence().toList()
    val records: List<Record<K>> get() = items.map(this::getRecord)

    private val recordListWatcher = RecordListWatcher(tags, itemTags)
    fun getRecord(item: E): Record<K> = idExtractor(item).let { key -> Record(key, itemTags.getOrDefault(key, listOf())) }
    fun getRecordList(records: List<Record<K>>): RecordListWatcher<K>.Records = recordListWatcher.watch(records)
}