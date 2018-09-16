package com.github.am4dr.rokusho.gui.viewer.multipane.pane

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.viewer.multipane.RecordsViewer
import com.github.am4dr.rokusho.gui.viewer.multipane.RecordsViewerFactory
import javafx.scene.control.ListView
import kotlin.reflect.KClass

class ListRecordsViewerFactory : RecordsViewerFactory {

    override fun isAcceptable(type: KClass<*>): Boolean = true

    override fun create(library: RokushoLibrary<*>): RecordsViewer<*> {
        val view = ListView<Record<*>>()
        return RecordsViewer("リスト", view, view.items)
    }
}
