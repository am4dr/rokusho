package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library
import javafx.beans.binding.Bindings
import java.lang.ref.SoftReference
import java.util.*

class CachedLibraryItemListViewerFactory(private val viewerFactory: LibraryItemListViewerFactory): LibraryItemListViewerFactory {

    override fun <T : Any> create(library: Library<T>): LibraryItemListViewer<T> {
        return getOrNull(library) ?: createLibraryViewerAndBindRecords(library)
    }

    private fun <T : Any> createLibraryViewerAndBindRecords(library: Library<T>): LibraryItemListViewer<T> =
            getOrCreate(library).also {
                Bindings.bindContent(it.items, library.getItems())
            }

    private val libraryViewCache = WeakHashMap(mutableMapOf<Library<*>, SoftReference<out LibraryItemListViewer<*>>>())

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getOrNull(library: Library<T>) =
            libraryViewCache[library]?.get()?.let { it as? LibraryItemListViewer<T> }

    private fun <T : Any> getOrCreate(library: Library<T>): LibraryItemListViewer<T> =
            getOrNull(library) ?: createLibraryViewAndCache(library)

    private fun <T : Any> createLibraryViewAndCache(library: Library<T>): LibraryItemListViewer<T> =
            viewerFactory.create(library).also { libraryViewCache[library] = SoftReference(it) }
}