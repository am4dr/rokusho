package com.github.am4dr.rokusho.app.gui.viewer.multipane

import com.github.am4dr.rokusho.old.core.library.Library
import kotlin.reflect.KClass

interface PaneFactory {

    fun isAcceptable(type: KClass<*>): Boolean

    fun create(library: Library<*>): MultiPaneLibraryViewer.Pane<*>?
}