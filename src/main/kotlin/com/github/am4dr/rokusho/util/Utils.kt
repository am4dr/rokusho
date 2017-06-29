package com.github.am4dr.rokusho.util

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.*
import javafx.collections.FXCollections.observableArrayList

fun <T> createEmptyListProperty(): ListProperty<T> =
        SimpleListProperty(FXCollections.observableList(mutableListOf<T>()))

fun <T> toObservableList(observableSet: ObservableSet<T>): ObservableList<T> {
    val list = object : ObservableList<T> by FXCollections.observableList(mutableListOf()), SetChangeListener<T> {
        override fun onChanged(change: SetChangeListener.Change<out T>?) {
            change?.run {
                remove(elementRemoved)
                add(elementAdded)
            }
        }
    }
    observableSet.addListener(WeakSetChangeListener(list))
    return list
}

fun <K, V> toObservableList(map: ObservableMap<K, V>): ObservableList<V> {
    val list = object : ObservableList<V> by observableArrayList<V>(), MapChangeListener<K, V> {
        val mapReference = map
        val index: MutableMap<K, Int> = mutableMapOf()
        init {
            map.toList().forEachIndexed { i, (k, v) ->
                index[k] = i
                add(i, v)
            }
        }
        override fun onChanged(change: MapChangeListener.Change<out K, out V>?) {
            change ?: return
            val i = index[change.key]
            if (i == null) {
                if (change.wasAdded()) {
                    add(change.valueAdded)
                    index[change.key] = lastIndex
                }
            }
            else {
                if (change.wasAdded()) {
                    set(i, change.valueAdded)
                }
                else if (change.wasRemoved()) {
                    removeAt(i)
                    index.remove(change.key)
                }
            }
        }
    }
    map.addListener(WeakMapChangeListener(list))
    return list
}
