package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.old.core.library.Library
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import java.lang.ref.SoftReference
import java.util.*

class LibraryViewerCollection(private val librarySelector: LibrarySelector,
                              private val viewerFactory: LibraryViewerFactory) {

    val currentLibraryViewer: ObservableValue<Node?> = Bindings.createObjectBinding({
        librarySelector.selectedProperty().get()?.let(this::getOrCreateLibraryViewer)?.node
    }, arrayOf(librarySelector.selectedProperty()))

    private fun getOrCreateLibraryViewer(library: Library<*>): LibraryViewer<*> =
            getOrNull(library) ?: createLibraryViewerAndBindRecords(library)

    private fun createLibraryViewerAndBindRecords(library: Library<*>): LibraryViewer<*> =
            getOrCreate(library).also {
                Bindings.bindContent(it.records, library.records)
            }


    private val libraryViewCache = WeakHashMap(mutableMapOf<Library<*>, SoftReference<out LibraryViewer<*>>>())

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getOrNull(library: Library<T>) =
            libraryViewCache[library]?.get()?.let { it as? LibraryViewer<T> }

    private fun <T : Any> getOrCreate(library: Library<T>): LibraryViewer<T> =
            getOrNull(library) ?: createLibraryViewAndCache(library)

    private fun <T : Any> createLibraryViewAndCache(library: Library<T>): LibraryViewer<T> =
            viewerFactory.create(library).also { libraryViewCache[library] = SoftReference(it) }
}