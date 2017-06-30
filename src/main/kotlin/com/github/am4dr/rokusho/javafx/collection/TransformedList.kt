package com.github.am4dr.rokusho.javafx.collection

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList

class TransformedList<E, F>(source: ObservableList<out F>, private val transform: (F) -> E)
    : TransformationList<E, F>(source) {
    private val transformed: MutableList<E>  = source.map(transform).toMutableList()

    override val size: Int get() = transformed.size
    override fun get(index: Int): E = transformed[index]
    override fun getSourceIndex(index: Int) = index
    override fun sourceChanged(change: ListChangeListener.Change<out F>?) {
        val c = change ?: return
        beginChange()
        while (c.next()) {
            if (c.wasPermutated()) { permutate(c) }
            else { removeOrAdd(c) }
        }
        endChange()
    }
    private fun  permutate(c: ListChangeListener.Change<out F>) {
        throw UnsupportedOperationException("observing permutation is not support yet: arg=$c")
    }
    private fun  removeOrAdd(c: ListChangeListener.Change<out F>) {
        if (c.wasRemoved()) {
            nextRemove(c.from, transformed.slice(c.from..c.from+c.removedSize-1))
            kotlin.repeat(c.removedSize) { transformed.removeAt(c.from) }
        }
        if (c.wasAdded()) {
            (c.from..c.to-1).forEach { transformed.add(it, transform(source[it])) }
            nextAdd(c.from, c.to)
        }
    }
}