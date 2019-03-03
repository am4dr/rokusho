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

    companion object {
        private fun createTabView(container: TabbedNodeContainer, title: String): Tab {
            return Tab().apply {
                tabHeight.bind(container.tabHeight)
                this.title.value = title
            }
        }
    }

    val tabHeight: DoubleProperty = SimpleDoubleProperty(30.0)
    val tabs: ObservableList<TabbedNode> = FXCollections.observableArrayList()
    val onAddClicked: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()

    private val tabSelector: TabSelector = TabSelector()


    fun injectTo(container: TabbedNodeContainer) {
        val tabViews: ObservableList<Node> = TransformedList(tabs) { node ->
            createTabView(container, node.title).apply {
                tabSelector.add(node, this)
                selected.bind(tabSelector.selectedTab.isEqualTo(this))
                onClicked.set { tabSelector.select(node) }
                onCloseClicked.set {
                    tabSelector.remove(node)
                    tabs.remove(node)
                }
            }
        }
        container.apply {
            bindContent(tabs, tabViews)
            tabHeight.bind(this@TabbedNodeContainerViewModel.tabHeight)
            content.bind(tabSelector.selectedNode)
            onAddClicked.bind(this@TabbedNodeContainerViewModel.onAddClicked)
        }
    }

    class TabbedNode(val node: Node, val title: String)
}