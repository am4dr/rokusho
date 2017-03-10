package com.github.am4dr.rokusho.util

import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableList
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class ConcatenatedList<T>(
        val left: ObservableList<out T>,
        val right: ObservableList<out T>,
        private val list: ReadOnlyListWrapper<T> = ReadOnlyListWrapper(observableList(mutableListOf<T>()))) : ObservableList<T> by list.readOnlyProperty {
    init {
        list.addAll(left)
        list.addAll(right)
        left.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasPermutated()) { permutate(c) }
                else { removeOrAdd(c, left, 0) }
            }
        })
        right.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasPermutated()) { permutate(c) }
                else { removeOrAdd(c, right, left.size) }
            }
        })
    }
    private fun  permutate(c: ListChangeListener.Change<out T>) {
        throw UnsupportedOperationException("observing permutation is not support yet: arg=$c")
    }
    private fun  removeOrAdd(c: ListChangeListener.Change<out T>, source: ObservableList<out T>, offset: Int) {
        if (c.wasRemoved()) {
            kotlin.repeat(c.removedSize) { list.removeAt(offset + c.from) }
        }
        if (c.wasAdded()) {
            (c.from..c.to-1).forEach { list.add(offset + it, source[it]) }
        }
    }
}