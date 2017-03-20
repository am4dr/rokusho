package com.github.am4dr.rokusho.util

import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class ConcatenatedList<T>(vararg lists: ObservableList<out T> = arrayOf()) : ObservableList<T> by ReadOnlyListWrapper(observableArrayList()) {
    private val lists: MutableList<ObservableList<out T>> = lists.toMutableList()
    init {
        lists.forEachIndexed { index, list ->
            list.addListener(ListChangeListener { c ->
                while (c.next()) {
                    if (c.wasPermutated()) { permutate(c) }
                    else { removeOrAdd(c, list, lists.take(index).sumBy(List<T>::size)) }
                }
            })
            addAll(list)
        }
    }
    private fun  permutate(c: ListChangeListener.Change<out T>) {
        throw UnsupportedOperationException("observing permutation is not support yet: arg=$c")
    }
    private fun  removeOrAdd(c: ListChangeListener.Change<out T>, source: ObservableList<out T>, offset: Int) {
        if (c.wasRemoved()) {
            kotlin.repeat(c.removedSize) { removeAt(offset + c.from) }
        }
        if (c.wasAdded()) {
            (c.from..c.to-1).forEach { add(offset + it, source[it]) }
        }
    }
    fun concat(list: ObservableList<out T>) {
        val index = lists.size
        list.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasPermutated()) { permutate(c) }
                else { removeOrAdd(c, list, lists.take(index).sumBy(List<T>::size)) }
            }
        })
        lists.add(list)
        addAll(list)
    }
}