package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library.LoadedLibrary
import java.lang.ref.SoftReference
import java.util.*

class CachedLibraryViewerFactory(private val viewerFactory: ItemListViewerFactory2) {

    private val libraryViewCache = WeakHashMap(mutableMapOf<LoadedLibrary, SoftReference<out ItemListViewer>>())

    fun getOrNull(library: LoadedLibrary) =
        libraryViewCache[library]?.get()?.let { it as? ItemListViewer }

    fun getOrCreate(library: LoadedLibrary): ItemListViewer =
        getOrNull(library) ?: createLibraryViewAndCache(library)

    private fun createLibraryViewAndCache(library: LoadedLibrary): ItemListViewer =
        viewerFactory().also { libraryViewCache[library] = SoftReference(it) }
}