package com.github.am4dr.rokusho.presenter.viewer.multipane

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.presenter.LibraryItemListViewer
import com.github.am4dr.rokusho.presenter.LibraryItemListViewerFactory

class MultiPaneViewerFactory(private val paneFactories: List<PaneFactory>) : LibraryItemListViewerFactory {

    override fun <T : Any> create(library: Library<T>): LibraryItemListViewer<T> = createViewer(library)

    private fun <T : Any> createViewer(library: Library<T>): LibraryItemListViewer<T> {
        @Suppress("UNCHECKED_CAST")
        val panes = paneFactories
                .filter { it.isAcceptable(library.type) }
                .mapNotNull { it.create(library) as? MultiPaneViewer.Pane<T> }
        return MultiPaneViewer(panes)
    }
}