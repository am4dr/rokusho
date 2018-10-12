package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.old.core.library.Library
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList

interface LibraryCollection {

    val libraries: ObservableList<Library<*>>
    fun addPathLibraryViaGUI()
    fun selectedProperty(): ReadOnlyObjectProperty<Library<*>?>
    fun select(library: Library<*>)
    fun select(index: Int)
    fun clearSelection()
}