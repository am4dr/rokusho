package com.github.am4dr.rokusho.presenter.viewer.multipane

import com.github.am4dr.rokusho.presenter.ItemListViewer
import com.github.am4dr.rokusho.presenter.ItemListViewerFactory
import kotlin.reflect.KClass

class MultiPaneViewerFactory(private val paneFactories: List<PaneFactory>) : ItemListViewerFactory {

    override fun invoke(type: KClass<out Any>): ItemListViewer {
        return createViewer(type)
    }

    private fun createViewer(type: KClass<*>): ItemListViewer {
        val panes = paneFactories
                .filter { it.isAcceptable(type) }
                .mapNotNull { it.create() }
        return MultiPaneViewer(panes)
    }
}