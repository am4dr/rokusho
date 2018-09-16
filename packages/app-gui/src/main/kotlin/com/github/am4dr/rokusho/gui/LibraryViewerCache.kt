package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import java.lang.ref.SoftReference
import java.util.*

class LibraryViewerCache(private val viewerFactory: LibraryViewerFactory) {

    private val libraryViewCache = WeakHashMap(mutableMapOf<RokushoLibrary<*>, SoftReference<out LibraryViewer<*>>>())

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrNull(library: RokushoLibrary<T>) =
            libraryViewCache[library]?.get()?.let { it as? LibraryViewer<T> }

    fun <T : Any> getOrCreate(library: RokushoLibrary<T>): LibraryViewer<T> =
            getOrNull(library) ?: createLibraryViewAndCache(library)

    private fun <T : Any> createLibraryViewAndCache(library: RokushoLibrary<T>): LibraryViewer<T> =
            viewerFactory.create(library).also { libraryViewCache[library] = SoftReference(it) }
}