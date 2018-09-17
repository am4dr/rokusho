package com.github.am4dr.rokusho.gui.viewer.multipane

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.gui.LibraryViewer
import com.github.am4dr.rokusho.gui.LibraryViewerFactory

class MultiPaneLibraryViewerFactory(private val paneFactories: List<PaneFactory>) : LibraryViewerFactory {

    override fun <T : Any> create(library: RokushoLibrary<T>): LibraryViewer<T> = createViewer(library)

    private fun <T : Any> createViewer(library: RokushoLibrary<T>): LibraryViewer<T> {
        @Suppress("UNCHECKED_CAST")
        val panes = paneFactories
                .filter { it.isAcceptable(library.type) }
                .mapNotNull { it.create(library) as? MultiPaneLibraryViewer.Pane<T> }
        return MultiPaneLibraryViewer(panes)
    }
}