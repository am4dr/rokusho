package com.github.am4dr.rokusho.app.gui.viewer.multipane.pane

import com.github.am4dr.rokusho.app.gui.viewer.multipane.MultiPaneLibraryViewer
import com.github.am4dr.rokusho.app.gui.viewer.multipane.PaneFactory
import com.github.am4dr.rokusho.old.core.library.Library
import com.github.am4dr.rokusho.old.core.library.Record
import javafx.scene.control.ListView
import kotlin.reflect.KClass

class ListPaneFactory : PaneFactory {

    override fun isAcceptable(type: KClass<*>): Boolean = true

    override fun create(library: Library<*>): MultiPaneLibraryViewer.Pane<*> {
        val view = ListView<Record<*>>()
        return MultiPaneLibraryViewer.Pane("リスト", view, view.items)
    }
}
