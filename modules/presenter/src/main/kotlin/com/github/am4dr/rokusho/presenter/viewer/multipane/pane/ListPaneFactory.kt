package com.github.am4dr.rokusho.presenter.viewer.multipane.pane

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.LibraryItem
import com.github.am4dr.rokusho.presenter.viewer.multipane.MultiPaneLibraryViewer
import com.github.am4dr.rokusho.presenter.viewer.multipane.PaneFactory
import javafx.scene.control.ListView
import kotlin.reflect.KClass

class ListPaneFactory : PaneFactory {

    override fun isAcceptable(type: KClass<*>): Boolean = true

    override fun create(library: Library<*>): MultiPaneLibraryViewer.Pane<*> {
        val view = ListView<LibraryItem<Any>>()
        return MultiPaneLibraryViewer.Pane("リスト", view, view.items)
    }
}
