package com.github.am4dr.rokusho.presenter.viewer.multipane

import com.github.am4dr.rokusho.presenter.ItemListViewer
import com.github.am4dr.rokusho.presenter.ItemListViewerFactory2

class MultiPaneViewerFactory(private val paneFactories: List<PaneFactory>) : ItemListViewerFactory2 {

    override fun invoke(): ItemListViewer {
        return createViewer()
    }

    private fun createViewer(): ItemListViewer {
        val panes = paneFactories.mapNotNull { it.create() }
        return MultiPaneViewer(panes)
    }
}