package com.github.am4dr.rokusho.javafx.main

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node

class TabbedNodeContainerViewModel {

    val tabHeight: DoubleProperty = SimpleDoubleProperty(30.0)
    val tabs: ObservableList<TabbedNode> = FXCollections.observableArrayList()
    val onAddClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()


    private val selector = TabSelector()

    private val tabViews: ObservableList<Node> = TransformedList(tabs) { node ->
        Tab().apply {
            title.value = node.title
            selector.add(this, node.node)
            selected.bind(selector.selectedTab.isEqualTo(this))
            onClicked.set { selector.select(this) }
            onCloseClicked.set {
                selector.remove(this)
                tabs.remove(node)
            }
        }
    }

    fun injectTo(container: TabbedNodeContainer) {
        container.apply {
            bindContent(tabs, tabViews)
            tabHeight.bind(this@TabbedNodeContainerViewModel.tabHeight)
            content.bind(selector.selectedNode)
            onAddClicked.bind(this@TabbedNodeContainerViewModel.onAddClicked)
        }
    }

    class TabbedNode(val node: Node, val title: String)
}