package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.adapter.OldLibraryWrapper
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.old.core.library.Library
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList
import com.github.am4dr.rokusho.app.LibraryCollection as CoreLibraryCollection

class RokushoLibraryCollection(private val libraryCollection: CoreLibraryCollection,
                               private val libraryPathProvider: LibraryPathProvider) : LibraryCollection {

    private val libs = libraryCollection.getLibraries()
    override val libraries: ObservableList<Library<*>> = TransformedList(libs) { OldLibraryWrapper(it) }
    private val selected = ReadOnlyObjectWrapper<Library<*>?>()

    init {
        libraries.addListener(InvalidationListener {
            when (libraries.size) {
                1 -> select(0)
                0 -> clearSelection()
            }
        })
    }

    override fun addPathLibraryViaGUI() {
        libraryPathProvider.get()?.let {
            libraryCollection.loadPathLibrary(it)
        }
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