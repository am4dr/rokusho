package com.github.am4dr.rokusho.javafx.binding

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class ObservableSelector<T>(
    val list: ObservableList<T> = FXCollections.observableArrayList()
) {

    private val _selected = ReadOnlyObjectWrapper<T?>()
    val selected: ReadOnlyObjectProperty<T?> = _selected.readOnlyProperty

    init {
        list.addListener(ListChangeListener { c ->
            while (c.next()) {
                when {
                    c.list.size == 0 -> {
                        clearSelection()
                    }
                    c.list.size == 1 -> {
                        select(0)
                    }
                    c.wasRemoved() -> {
                        if (c.removed.any { it === _selected.get() }) {
                            if (c.from <= list.lastIndex) select(c.from)
                            else select(list.lastIndex)
                        }
                    }
                }
            }
        })
    }

    fun select(item: T) {
        list.find { it === item }?.let { _selected.set(it) }
    }
    fun select(index: Int) {
        if (0 <= index && index <= list.lastIndex) _selected.set(list[index])
    }
    fun clearSelection() {
        _selected.value = null
    }
}