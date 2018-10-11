package com.github.am4dr.rokusho.app.gui.viewer.multipane

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import kotlin.reflect.KClass

interface PaneFactory {

    fun isAcceptable(type: KClass<*>): Boolean

    fun create(library: RokushoLibrary<*>): MultiPaneLibraryViewer.Pane<*>?
}