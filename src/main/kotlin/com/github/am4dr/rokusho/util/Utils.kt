package com.github.am4dr.rokusho.util

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.*

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