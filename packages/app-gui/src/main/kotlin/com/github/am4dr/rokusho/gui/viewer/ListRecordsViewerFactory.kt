package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.RecordsViewerFactory
import javafx.beans.binding.Bindings
import javafx.scene.Node
import javafx.scene.control.ListView
import kotlin.reflect.KClass

class ListRecordsViewerFactory : RecordsViewerFactory {

    override fun acceptable(type: KClass<*>): Boolean = true

    override fun create(library: RokushoLibrary<*>, container: RecordsViewerContainer<*>): RecordsViewer = RecordsViewer("リスト", createListRecordsViewer(container))
}

private fun <T> createListRecordsViewer(container: RecordsViewerContainer<T>): Node = ListView<Record<T>>().also { Bindings.bindContent(it.items, container.records) }