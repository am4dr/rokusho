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

    private val itemCache = observableHashMap<T, Item<T>>()
    private fun cacheItem(item: Item<T>) {
        itemCache[item.key] = item
    }

    override fun getItemSet(list: Iterable<T>): ItemSet<T> {
        val itemSet = DefaultLibraryItemSet(this, list.mapTo(observableArrayList(), this::getOrCreateEmptyItemOf))
        itemCache.addListener(WeakMapChangeListener(itemSet))
        return itemSet
    }

    private  fun createItem(key: T, tags: Iterable<ItemTag>): Item<T>? = Item(key, tags.toList()).apply(this::cacheItem)
    override fun getItem(key: T): Item<T>?                  = itemCache[key] ?: itemTagDB.get(key).takeIf { it.isNotEmpty() }?.let { createItem(key, it) }
    override fun getOrCreateEmptyItemOf(key: T): Item<T>    = getItem(key) ?: createItem(key, listOf())!!
    override fun addItem(key: T, tags: Iterable<ItemTag>)   = updateItemTags(key, tags)
    override fun removeItem(key: T) {
        itemTagDB.remove(key)
        itemCache.remove(key)
    }
    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        itemTagDB.set(key, tags.toList())
        createItem(key, tags.toList())
    }
}
class DefaultLibraryItemSet<T>(override val library: Library<T>, target: ObservableList<Item<T>>) : ItemSet<T>, MapChangeListener<T, Item<T>> {

    private val values = target.map(Item<T>::key)
    private val _items = observableArrayList(target)
    override val items: ReadOnlyListProperty<Item<T>> = ReadOnlyListWrapper(_items).readOnlyProperty

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
