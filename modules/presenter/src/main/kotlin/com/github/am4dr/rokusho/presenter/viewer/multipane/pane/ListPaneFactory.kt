package com.github.am4dr.rokusho.presenter.viewer.multipane.pane

import com.github.am4dr.rokusho.presenter.ItemViewModel
import com.github.am4dr.rokusho.presenter.viewer.multipane.MultiPaneViewer
import com.github.am4dr.rokusho.presenter.viewer.multipane.PaneFactory
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback
import kotlin.reflect.KClass

class ListPaneFactory : PaneFactory {

    override fun isAcceptable(type: KClass<*>): Boolean = true

    override fun create(): MultiPaneViewer.Pane {
        val view = ListView<ItemViewModel<*>>()
        view.cellFactory = Callback { object : ListCell<ItemViewModel<*>>() {
            override fun updateItem(item: ItemViewModel<*>?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (item == null || empty) {
                    ""
                } else {
                    "${item.item}: ${item.tags.joinToString(", ")}"
                }
            }
        } }
        return MultiPaneViewer.Pane("リスト", view, view.items)
    }
}
