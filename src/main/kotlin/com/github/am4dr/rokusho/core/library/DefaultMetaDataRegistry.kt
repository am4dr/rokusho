package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.javafx.collection.ObservableSubMap
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap
import javafx.collections.transformation.SortedList

class DefaultMetaDataRegistry<T>(
        tags: ObservableMap<String, Tag> = FXCollections.observableHashMap(),
        private val itemTagDB: ItemTagDB<T> = DefaultItemTagDB()) : MetaDataRegistry<T> {

    private val _tags = ReadOnlyMapWrapper<String, Tag>(tags)
    override fun getTags(): ReadOnlyMapProperty<String, Tag> = _tags.readOnlyProperty
    override fun getAllItems(): Set<Record<T>> = itemTagDB.getKeys().map(this::getRecord).toSet()

    private val watchedItems = observableHashMap<T, Record<T>>()
    private fun watchIfNotWatched(record: Record<T>) {
        if (!watchedItems.containsKey(record.key)) { watchedItems[record.key] = record
        }
    }

    override fun getRecordList(list: Iterable<T>): ObservableRecordList<T> {
        list.map(this::getRecord).forEach(this::watchIfNotWatched)
        return DefaultMetaDataRegistryObservableRecordList(list.toList())
    }

    override fun getRecord(key: T): Record<T> = watchedItems[key] ?: Record(key, itemTagDB.get(key))

    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        updateMetaData(Record(key, tags.toList()))
    }
    private fun updateMetaData(record: Record<T>) {
        itemTagDB.set(record.key, record.itemTags)
        watchedItems[record.key]?.takeIf { it != record }
                ?.let{ watchedItems[record.key] = record }
    }

    private inner class DefaultMetaDataRegistryObservableRecordList(keys: List<T>) : ObservableRecordList<T> {
        override val metaDataRegistry: MetaDataRegistry<T> = this@DefaultMetaDataRegistry

        override val records: ReadOnlyListProperty<Record<T>>
        init {
            val subMap = ObservableSubMap(watchedItems, keys)
            val list = SortedList(toObservableList(subMap), compareBy { it.key as? Comparable<*> ?: it.key.toString() })
            records = ReadOnlyListWrapper(list).readOnlyProperty
        }
    }
}
