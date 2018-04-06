package com.github.am4dr.rokusho.javafx.collection

import javafx.collections.*
import javafx.collections.FXCollections.observableArrayList

fun <T> toObservableList(observableSet: ObservableSet<T>): ObservableList<T> {
    val list = object : ObservableList<T> by FXCollections.observableArrayList<T>(observableSet), SetChangeListener<T> {
        val source = observableSet
        override fun onChanged(change: SetChangeListener.Change<out T>?) {
            change ?: return
            if (change.wasRemoved()) remove(change.elementRemoved)
            if (change.wasAdded()) add(change.elementAdded)
        }
    }
    observableSet.addListener(WeakSetChangeListener(list))
    return list
}

fun <K, V> toObservableList(map: ObservableMap<K, V>): ObservableList<V> {
    val list = object : ObservableList<V> by observableArrayList<V>(), MapChangeListener<K, V> {
        val source = map
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

fun <T, K> toObservableMap(observableSet: ObservableSet<T>, keyExtractor: (T) -> K): ObservableMap<K, T> {
    val set = object : ObservableMap<K, T> by FXCollections.observableHashMap(), SetChangeListener<T> {
        init {
            putAll(observableSet.map { keyExtractor(it) to it })
        }
        val source = observableSet
        override fun onChanged(change: SetChangeListener.Change<out T>?) {
            change ?: return
            if (change.wasRemoved()) remove(keyExtractor(change.elementRemoved))
            if (change.wasAdded()) change.elementAdded.let { put(keyExtractor(it), it) }
        }
    }
    observableSet.addListener(WeakSetChangeListener(set))
    return set
}
