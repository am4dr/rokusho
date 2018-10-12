package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList

interface LibraryCollection {
    val libraries: ObservableList<RokushoLibrary<*>>
    fun addPathLibraryViaGUI()
    fun selectedProperty(): ReadOnlyObjectProperty<RokushoLibrary<*>?>
    fun select(library: RokushoLibrary<*>)
    fun select(index: Int)
    fun clearSelection()
}