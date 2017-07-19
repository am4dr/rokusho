package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections

class DefaultItemTagDB<T>(initial: Map<T, List<ItemTag>> = mapOf()) : ItemTagDB<T> {
    private val _itemTags = ReadOnlyMapWrapper(FXCollections.observableMap(initial.toMutableMap()))
    override val itemTags: ReadOnlyMapProperty<T, List<ItemTag>> = _itemTags.readOnlyProperty

    override fun getKeys(): Set<T> = _itemTags.keys.toSet()
    override fun get(key: T): List<ItemTag> = _itemTags.getOrDefault(key, listOf())
    override fun set(key: T, itemTags: List<ItemTag>) {
        _itemTags[key] = itemTags
    }
    override fun remove(key: T) {
        _itemTags.remove(key)
    }
}