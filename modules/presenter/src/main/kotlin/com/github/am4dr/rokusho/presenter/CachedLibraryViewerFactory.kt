package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library
import java.lang.ref.SoftReference
import java.util.*

class CachedLibraryViewerFactory(private val viewerFactory: ItemListViewerFactory) {

    private val libraryViewCache = WeakHashMap(mutableMapOf<Library<*>, SoftReference<out ItemListViewer>>())

    fun getOrNull(library: Library<*>) =
        libraryViewCache[library]?.get()?.let { it as? ItemListViewer }

    fun getOrCreate(library: Library<*>): ItemListViewer =
        getOrNull(library) ?: createLibraryViewAndCache(library)

    private fun createLibraryViewAndCache(library: Library<*>): ItemListViewer =
        viewerFactory(library.type).also { libraryViewCache[library] = SoftReference(it) }
}