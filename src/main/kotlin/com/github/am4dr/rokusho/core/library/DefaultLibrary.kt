package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakMapChangeListener

class DefaultLibrary<T>(
        tags: MutableList<Tag> = mutableListOf(),
        private val itemTagDB: ItemTagDB<T> = SimpleItemTagDB()) : Library<T> {

    private val _tags = ReadOnlyMapWrapper<String, Tag>(observableHashMap()).apply {
        tags.map { it.id to it }.toMap(this)
    }
    override fun getTags(): ReadOnlyMapProperty<String, Tag> = _tags.readOnlyProperty

    private val watchedItems = observableHashMap<T, Record<T>>()
    private fun watchIfNotWatched(record: Record<T>) {
        if (!watchedItems.containsKey(record.key)) { watchedItems[record.key] = record
        }
    }

    override fun getItemSet(list: Iterable<T>): ItemSet<T> {
        val items = list.mapTo(observableArrayList(), this::getRecord)
        items.forEach(this::watchIfNotWatched)
        val itemSet = DefaultLibraryItemSet(this, items)
        watchedItems.addListener(WeakMapChangeListener(itemSet))
        return itemSet
    }

    override fun getRecord(key: T): Record<T> = watchedItems[key] ?: Record(key, itemTagDB.get(key))

    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        updateItem(Record(key, tags.toList()))
    }
    private fun updateItem(record: Record<T>) {
        itemTagDB.set(record.key, record.itemTags)
        watchedItems[record.key]?.takeIf { it != record }
                ?.let{ watchedItems[record.key] = record }
    }
}
class DefaultLibraryItemSet<T>(override val library: Library<T>, target: ObservableList<Record<T>>) : ItemSet<T>, MapChangeListener<T, Record<T>> {

    private val values = target.map(Record<T>::key)
    private val _items = observableArrayList(target)
    override val records: ReadOnlyListProperty<Record<T>> = ReadOnlyListWrapper(_items).readOnlyProperty

    // TODO 更新(つまり削除と追加が同時に行われるもの)ではなくただの削除に対応する
    override fun onChanged(change: MapChangeListener.Change<out T, out Record<T>>?) {
        change ?: return
        val idx = values.indexOf(change.key).takeIf { it >= 0 } ?: return
        if (change.wasRemoved()) {
            _items.removeAt(idx)
        }
        if (change.wasAdded()) {
            _items.add(idx, change.valueAdded)
        }
    }
}
