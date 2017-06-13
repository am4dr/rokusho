package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.app.savefile.Tag
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

    private val watchedItems = observableHashMap<T, Item<T>>()
    private fun watchIfNotWatched(item: Item<T>) {
        if (!watchedItems.containsKey(item.key)) { watchedItems[item.key] = item }
    }

    override fun getItemSet(list: Iterable<T>): ItemSet<T> {
        val items = list.mapTo(observableArrayList(), this::getItem)
        items.forEach(this::watchIfNotWatched)
        val itemSet = DefaultLibraryItemSet(this, items)
        watchedItems.addListener(WeakMapChangeListener(itemSet))
        return itemSet
    }

    override fun getItem(key: T): Item<T> = watchedItems[key] ?: Item(key, itemTagDB.get(key))

    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        updateItem(Item(key, tags.toList()))
    }
    private fun updateItem(item: Item<T>) {
        itemTagDB.set(item.key, item.itemTags)
        watchedItems[item.key]?.takeIf { it != item }
                ?.let{ watchedItems[item.key] = item }
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
