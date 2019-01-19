package com.github.am4dr.rokusho.presenter.viewer.multipane

import kotlin.reflect.KClass

interface PaneFactory {

    fun isAcceptable(type: KClass<*>): Boolean
    fun create(): MultiPaneViewer.Pane?
}