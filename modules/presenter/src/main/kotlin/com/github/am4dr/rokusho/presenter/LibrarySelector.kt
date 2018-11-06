package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList

interface LibrarySelector {

    val libraries: ObservableList<Library<*>>
    fun selectedProperty(): ReadOnlyObjectProperty<Library<*>?>
    fun select(library: Library<*>)
    fun select(index: Int)
    fun clearSelection()
}