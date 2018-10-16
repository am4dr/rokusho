package com.github.am4dr.rokusho.app.gui.viewer.multipane

import com.github.am4dr.rokusho.app.gui.LibraryViewer
import com.github.am4dr.rokusho.app.gui.LibraryViewerFactory
import com.github.am4dr.rokusho.core.library.Library

class MultiPaneLibraryViewerFactory(private val paneFactories: List<PaneFactory>) : LibraryViewerFactory {

    override fun <T : Any> create(library: Library<T>): LibraryViewer<T> = createViewer(library)

    private fun <T : Any> createViewer(library: Library<T>): LibraryViewer<T> {
        @Suppress("UNCHECKED_CAST")
        val panes = paneFactories
                .filter { it.isAcceptable(library.type) }
                .mapNotNull { it.create(library) as? MultiPaneLibraryViewer.Pane<T> }
        return MultiPaneLibraryViewer(panes)
    }
}