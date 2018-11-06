package com.github.am4dr.rokusho.presenter.viewer.multipane

import com.github.am4dr.rokusho.core.library.Library
import kotlin.reflect.KClass

interface PaneFactory {

    fun isAcceptable(type: KClass<*>): Boolean

    fun create(library: Library<*>): MultiPaneLibraryViewer.Pane<*>?
}