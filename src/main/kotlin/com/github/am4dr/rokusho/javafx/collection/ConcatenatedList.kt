package com.github.am4dr.rokusho.javafx.collection

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.collections.transformation.TransformationList

class ConcatenatedList<E>(lists: ObservableList<ObservableList<E>>) : TransformationList<E, ObservableList<E>>(lists) {

    companion object {
        @JvmStatic
        fun <T> concat(vararg lists: ObservableList<T>): ConcatenatedList<T> = ConcatenatedList(observableArrayList<ObservableList<T>>(*lists))
    }

    private val _lists = ReadOnlyListWrapper<ObservableList<E>>(lists)
    val lists: ReadOnlyListProperty<ObservableList<E>> = _lists.readOnlyProperty

    private val listeners: MutableMap<ObservableList<E>, WeakListChangeListener<E>> = mutableMapOf()
    private val listenerHolder = mutableMapOf<WeakListChangeListener<E>, ListChangeListener<E>>()

    init {
        lists.forEach(this::addChangeListener)
    }

    private fun addChangeListener(list: ObservableList<E>) {
        val listener = createListChangeListener(list)
        val weak = WeakListChangeListener(listener)
        list.addListener(weak)
        listeners[list] = weak
        listenerHolder[weak] = listener
    }
    private fun removeChangeListener(list: ObservableList<E>) {
        val weak = listeners.remove(list)
        list.removeListener(weak)
        listenerHolder.remove(weak)
    }
    private fun createListChangeListener(list: ObservableList<E>): ListChangeListener<E> = ListChangeListener { c ->
        val offset = getListOffset(list)
        beginChange()
        while (c.next()) {
            if (c.wasPermutated()) {
                throw NotImplementedError("permutation is not implemented yet")
            }
            else {
                if (c.wasRemoved()) {
                    nextRemove(offset + c.from, c.removed)
                }
                if (c.wasAdded()) {
                    nextAdd(offset + c.from, offset + c.to)
                }
            }
        }
        endChange()
    }
    private fun getListOffset(list: ObservableList<E>): Int = _lists.take(_lists.indexOfFirst { it === list }).sumBy(List<E>::size)
    private fun getListOffset(sourceIndex: Int): Int = _lists.take(sourceIndex).sumBy(List<E>::size)

    override val size: Int get() = lists.sumBy(List<E>::size)

    override fun sourceChanged(c: ListChangeListener.Change<out ObservableList<E>>?) {
        c ?: return
        beginChange()
        while (c.next()) {
            if (c.wasPermutated()) {
                throw NotImplementedError("permutation is not implemented yet")
            }
            else {
                if (c.wasRemoved()) {
                    c.removed.forEach(this::removeChangeListener)
                    val removedElements = mutableListOf<E>()
                    c.removed.forEach { removedElements.addAll(it) }
                    nextRemove(getListOffset(c.from), removedElements)
                }
                if (c.wasAdded()) {
                    c.addedSubList.forEach(this::addChangeListener)
                    val from = getListOffset(c.from)
                    val to = from + c.addedSubList.sumBy(List<E>::size)
                    nextAdd(from, to)
                }
            }
        }
        endChange()
    }

    override fun getSourceIndex(index: Int): Int = getSourceIndex(index, 0)

    private tailrec fun getSourceIndex(index: Int, listIndex: Int): Int {
        val list = _lists[listIndex]
        return if (index < list.size) listIndex else getSourceIndex(index - list.size, listIndex + 1)
    }

    override fun get(index: Int): E = get(index, 0)

    private tailrec fun get(index: Int, listIndex: Int): E {
        val list = _lists[listIndex]
        return if (index < list.size) list[index] else get(index - list.size, listIndex + 1)
    }
}