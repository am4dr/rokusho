package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library2.LoadedLibrary
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class LibrarySelector(
    val libraries: ObservableList<LoadedLibrary> = FXCollections.observableArrayList()
) {

    private val selected = ReadOnlyObjectWrapper<LoadedLibrary?>()

    init {
        libraries.addListener(ListChangeListener { c ->
            while (c.next()) {
                when {
                    c.list.size == 0 -> {
                        clearSelection()
                    }
                    c.wasUpdated() -> {
                        select(c.from)
                    }
                    c.wasAdded() -> {
                        select(c.from)
                    }
                    c.wasRemoved() -> {
                        if (c.removed.any { it === selected.get() }) {
                            select(c.from)
                        }
                    }
                }
            }
        })
    }

    fun selectedProperty(): ReadOnlyObjectProperty<LoadedLibrary?> = selected.readOnlyProperty

    fun select(library: LoadedLibrary) {
        libraries.find { it === library }?.let { selected.set(it) }
    }
    fun select(index: Int) {
        if (0 <= index && index <= libraries.lastIndex) selected.set(libraries[index])
    }
    fun clearSelection() {
        selected.value = null
    }
}