package com.github.am4dr.rokusho.util

import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.collections.WeakMapChangeListener

class ObservableSubMap<K, V>(source: ObservableMap<K, V>, val keyList: List<K>, val destination: ObservableMap<K, V> = FXCollections.observableHashMap()) : ObservableMap<K, V> by destination {
    private val listener = MapChangeListener<K, V> {
        if (!keyList.contains(it.key)) return@MapChangeListener
        if (it.wasRemoved()) {
            destination.remove(it.key)
        }
        if (it.wasAdded()) {
            destination.put(it.key, it.valueAdded)
        }
    }
    private val weakListener = WeakMapChangeListener<K, V>(listener)
    init {
        source.addListener(weakListener)
    }
}