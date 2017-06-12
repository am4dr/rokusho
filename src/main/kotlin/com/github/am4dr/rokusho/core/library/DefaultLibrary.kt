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

    private val items = observableHashMap<T, Item<T>>()

    override fun getItemSet(list: Iterable<T>): ItemSet<T> {
        val itemSet = DefaultLibraryItemSet(this, list.mapTo(observableArrayList(), this::getOrCreateItemOf))
        items.addListener(WeakMapChangeListener(itemSet))
        return itemSet
    }

    override fun getItemOf(value: T): Item<T>?          = items[value]
    override fun getOrCreateItemOf(value: T): Item<T>   = getItemOf(value) ?: (Item(value, itemTagDB.get(value)).also { addItem(it) })
    override fun addItem(item: Item<T>): Boolean        = updateItem(item).let { true }
    override fun removeItem(item: Item<T>): Boolean     = items.remove(item.value, item)

    private  fun updateItem(item: Item<T>) {
        updateItemTags(item.value, item.itemTags)
    }

    override fun updateItemTags(itemValue: T, itemTags: List<ItemTag>) {
        itemTagDB.set(itemValue, itemTags)
        items.put(itemValue, Item(itemValue, itemTags))
    }
}
class DefaultLibraryItemSet<T>(override val library: Library<T>, target: ObservableList<Item<T>>) : ItemSet<T>, MapChangeListener<T, Item<T>> {

    private val values = target.map(Item<T>::value)
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
