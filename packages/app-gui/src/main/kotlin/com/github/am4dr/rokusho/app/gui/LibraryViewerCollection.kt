package com.github.am4dr.rokusho.app.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import java.lang.ref.SoftReference
import java.util.*

class LibraryViewerCollection(private val libraryCollection: LibraryCollection,
                              private val viewerFactory: LibraryViewerFactory) {

    val currentLibraryViewer: ObservableValue<Node?> = Bindings.createObjectBinding({
        libraryCollection.selectedProperty().get()?.let(this::getOrCreateLibraryViewer)?.node
    }, arrayOf(libraryCollection.selectedProperty()))

    private fun getOrCreateLibraryViewer(library: RokushoLibrary<*>): LibraryViewer<*> =
            getOrNull(library) ?: createLibraryViewerAndBindRecords(library)

    private fun createLibraryViewerAndBindRecords(library: RokushoLibrary<*>): LibraryViewer<*> =
            getOrCreate(library).also {
                Bindings.bindContent(it.records, library.records)
            }


    private val libraryViewCache = WeakHashMap(mutableMapOf<RokushoLibrary<*>, SoftReference<out LibraryViewer<*>>>())

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getOrNull(library: RokushoLibrary<T>) =
            libraryViewCache[library]?.get()?.let { it as? LibraryViewer<T> }

    private fun <T : Any> getOrCreate(library: RokushoLibrary<T>): LibraryViewer<T> =
            getOrNull(library) ?: createLibraryViewAndCache(library)

    private fun <T : Any> createLibraryViewAndCache(library: RokushoLibrary<T>): LibraryViewer<T> =
            viewerFactory.create(library).also { libraryViewCache[library] = SoftReference(it) }
}