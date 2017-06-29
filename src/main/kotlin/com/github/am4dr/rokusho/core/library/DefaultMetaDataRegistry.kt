package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.util.ObservableSubMap
import com.github.am4dr.rokusho.util.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.transformation.SortedList

class DefaultMetaDataRegistry<T>(
        tags: MutableList<Tag> = mutableListOf(),
        private val itemTagDB: ItemTagDB<T> = SimpleItemTagDB()) : MetaDataRegistry<T> {

    private val _tags = ReadOnlyMapWrapper<String, Tag>(observableHashMap()).apply {
        tags.map { it.id to it }.toMap(this)
    }
    override fun getTags(): ReadOnlyMapProperty<String, Tag> = _tags.readOnlyProperty

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
