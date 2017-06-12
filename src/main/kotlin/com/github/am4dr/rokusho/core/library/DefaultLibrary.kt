package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.app.savefile.Tag
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakMapChangeListener

class DefaultLibrary<T>(
        private val tags: MutableList<Tag> = mutableListOf(),
        private val itemTagDB: ItemTagDB<T> = SimpleItemTagDB()) : Library<T> {

    private val watchedItems = observableHashMap<T, Item<T>>()
    private fun watchIfNotWatched(item: Item<T>) {
        if (!watchedItems.containsKey(item.key)) { watchedItems[item.key] = item }
    }

    override fun getItemSet(list: Iterable<T>): ItemSet<T> {
        val items = list.mapTo(observableArrayList(), this::getOrCreateEmptyItemOf)
        items.forEach(this::watchIfNotWatched)
        val itemSet = DefaultLibraryItemSet(this, items)
        watchedItems.addListener(WeakMapChangeListener(itemSet))
        return itemSet
    }
    private fun updateItem(item: Item<T>) {
        itemTagDB.set(item.key, item.itemTags)
        watchedItems[item.key]?.takeIf { it != item }
                ?.let{ watchedItems[item.key] = item }
    }

    override fun getItem(key: T): Item<T>?                  = watchedItems[key] ?: itemTagDB.get(key).takeIf { it.isNotEmpty() }?.let { Item(key, it) }
    override fun getOrCreateEmptyItemOf(key: T): Item<T>    = getItem(key) ?: Item(key, listOf())
    // TODO どう実装すべきか、あるいは削除するべきか
    override fun addItem(key: T, tags: Iterable<ItemTag>) {
        throw NotImplementedError()
        updateItemTags(key, tags)
    }
    override fun removeItem(key: T) {
        throw NotImplementedError()
        itemTagDB.remove(key)
        watchedItems.remove(key)
    }
    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        updateItem(Item(key, tags.toList()))
    }
}
class DefaultLibraryItemSet<T>(override val library: Library<T>, target: ObservableList<Item<T>>) : ItemSet<T>, MapChangeListener<T, Item<T>> {

    private val values = target.map(Item<T>::key)
    private val _items = observableArrayList(target)
    override val items: ReadOnlyListProperty<Item<T>> = ReadOnlyListWrapper(_items).readOnlyProperty

    // TODO 更新(つまり削除と追加が同時に行われるもの)ではなくただの削除に対応する
    override fun onChanged(change: MapChangeListener.Change<out T, out Item<T>>?) {
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
