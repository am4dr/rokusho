package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList

interface LibraryCollection {
    val libraries: ObservableList<RokushoLibrary<*>>
    fun addLibraryViaGUI()
    fun selectedProperty(): ReadOnlyObjectProperty<RokushoLibrary<*>?>
    fun select(library: RokushoLibrary<*>)
    fun select(index: Int)
    fun clearSelection()
}