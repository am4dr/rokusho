package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.javafx.collection.ObservableSubMap
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.MapChangeListener
import javafx.collections.WeakMapChangeListener
import javafx.collections.transformation.SortedList

class DefaultMetaDataRegistry<T>(
        private val tags: ReadOnlyMapProperty<String, Tag>,
        private val itemTags: ReadOnlyMapProperty<T, List<ItemTag>>) : MetaDataRegistry<T> {

    override fun getAllRecords(): Set<Record<T>> = itemTags.keys.map(this::getRecord).toSet()

    private val watchedItems = observableHashMap<T, Record<T>>()
    private fun watchIfNotWatched(record: Record<T>) {
        if (!watchedItems.containsKey(record.key)) { watchedItems[record.key] = record
        }
    }

    private val itemTagsListener = MapChangeListener<T, List<ItemTag>> { c ->
        c ?: return@MapChangeListener
        if (watchedItems.containsKey(c.key) && c.wasAdded() && watchedItems[c.key] != c.valueAdded) {
            watchedItems[c.key] = Record(c.key, c.valueAdded)
        }
    }
    init {
        itemTags.addListener(WeakMapChangeListener(itemTagsListener))
    }

    override fun getRecordList(list: Iterable<T>): ObservableRecordList<T> {
        list.map(this::getRecord).forEach(this::watchIfNotWatched)
        return DefaultMetaDataRegistryObservableRecordList(list.toList())
    }

    override fun getRecord(key: T): Record<T> = watchedItems[key] ?: Record(key, itemTags[key] ?: listOf())

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
