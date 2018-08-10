package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class LibraryCollection {

    val libraries: ObservableList<RokushoLibrary<*>> = FXCollections.synchronizedObservableList(FXCollections.observableArrayList())
    private val selected = ReadOnlyObjectWrapper<RokushoLibrary<*>?>()


    init {
        libraries.addListener(InvalidationListener {
            when (libraries.size) {
                1 -> select(0)
                0 -> clearSelection()
            }
        })
    }

    fun selectedProperty(): ReadOnlyObjectProperty<RokushoLibrary<*>?> = selected.readOnlyProperty

    fun select(library: RokushoLibrary<*>) {
        libraries.find { it === library }?.let { selected.set(it) }
    }
    fun select(index: Int) {
        if (0 <= index && index <= libraries.lastIndex) selected.set(libraries[index])
    }
    fun clearSelection() {
        selected.value = null
    }
}