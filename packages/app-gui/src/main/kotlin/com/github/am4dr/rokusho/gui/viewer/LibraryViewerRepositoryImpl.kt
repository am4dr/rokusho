package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.gui.LibraryViewerRepository
import javafx.scene.Node
import java.lang.ref.SoftReference
import java.util.*

class LibraryViewerRepositoryImpl(private val viewerFactory: LibraryViewerFactory) : LibraryViewerRepository {

    private val libraryViewCache = WeakHashMap(mutableMapOf<RokushoLibrary<*>, SoftReference<Node>>())

    override fun get(library: RokushoLibrary<*>): Node =
            libraryViewCache[library]?.get() ?: createLibraryViewAndCache(library)

    private fun createLibraryViewAndCache(library: RokushoLibrary<*>): Node =
            viewerFactory.create(library).also { libraryViewCache[library] = SoftReference(it) }
}