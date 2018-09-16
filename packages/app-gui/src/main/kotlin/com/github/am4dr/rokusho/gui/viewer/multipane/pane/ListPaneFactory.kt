package com.github.am4dr.rokusho.gui.viewer.multipane.pane

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.viewer.multipane.MultiPaneLibraryViewer
import com.github.am4dr.rokusho.gui.viewer.multipane.PaneFactory
import javafx.scene.control.ListView
import kotlin.reflect.KClass

class ListPaneFactory : PaneFactory {

    override fun isAcceptable(type: KClass<*>): Boolean = true

    override fun create(library: RokushoLibrary<*>): MultiPaneLibraryViewer.Pane<*> {
        val view = ListView<Record<*>>()
        return MultiPaneLibraryViewer.Pane("リスト", view, view.items)
    }
}
