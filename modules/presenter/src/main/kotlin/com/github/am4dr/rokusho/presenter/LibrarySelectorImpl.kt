package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class LibrarySelectorImpl : LibrarySelector {

    override val libraries: ObservableList<Library<*>> = FXCollections.observableArrayList()
    private val selected = ReadOnlyObjectWrapper<Library<*>?>()

    init {
        libraries.addListener(InvalidationListener {
            when (libraries.size) {
                1 -> select(0)
                0 -> clearSelection()
            }
        })
    }

    override fun selectedProperty(): ReadOnlyObjectProperty<Library<*>?> = selected.readOnlyProperty

    override fun select(library: Library<*>) {
        libraries.find { it === library }?.let { selected.set(it) }
    }
    override fun select(index: Int) {
        if (0 <= index && index <= libraries.lastIndex) selected.set(libraries[index])
    }
    override fun clearSelection() {
        selected.value = null
    }
}