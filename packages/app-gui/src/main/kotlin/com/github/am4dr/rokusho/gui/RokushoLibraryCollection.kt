package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageLibraryLoader
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList

class RokushoLibraryCollection(private val rokusho: Rokusho,
                               private val libraryPathProvider: LibraryPathProvider) : LibraryCollection {

    override val libraries: ObservableList<RokushoLibrary<*>> get() = rokusho.libraries
    private val selected = ReadOnlyObjectWrapper<RokushoLibrary<*>?>()

    init {
        libraries.addListener(InvalidationListener {
            when (libraries.size) {
                1 -> select(0)
                0 -> clearSelection()
            }
        })
    }

    override fun addLibraryViaGUI() {
        libraryPathProvider.get()?.let {
            rokusho.loadAndAddLibrary(ImageLibraryLoader::class, it)
        }
    }

    override fun selectedProperty(): ReadOnlyObjectProperty<RokushoLibrary<*>?> = selected.readOnlyProperty

    override fun select(library: RokushoLibrary<*>) {
        libraries.find { it === library }?.let { selected.set(it) }
    }
    override fun select(index: Int) {
        if (0 <= index && index <= libraries.lastIndex) selected.set(libraries[index])
    }
    override fun clearSelection() {
        selected.value = null
    }
}