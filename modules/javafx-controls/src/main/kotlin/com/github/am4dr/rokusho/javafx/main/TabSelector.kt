package com.github.am4dr.rokusho.javafx.main

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import java.util.*

class TabSelector {

    private val tabs = IdentityHashMap<TabbedNodeContainerViewModel.TabbedNode, Tab>()

    val selectedTab: ReadOnlyObjectProperty<Tab?> = SimpleObjectProperty()
    val selectedNode: ReadOnlyObjectProperty<Node?> = SimpleObjectProperty()


    fun add(node: TabbedNodeContainerViewModel.TabbedNode, tab: Tab) {
        tabs[node] = tab
        checkSelection()
    }

    fun remove(node: TabbedNodeContainerViewModel.TabbedNode) {
        tabs.remove(node)
        checkSelection()
    }

    fun select(node: TabbedNodeContainerViewModel.TabbedNode) {
        select(node, tabs[node] ?: return)
    }

    private fun select(node: TabbedNodeContainerViewModel.TabbedNode?, tab: Tab?) {
        (selectedTab as SimpleObjectProperty).value = tab
        (selectedNode as SimpleObjectProperty).value = node?.node
    }

    private fun clearSelection() {
        select(null, null)
    }

    private fun selectFirst() {
        select(tabs.keys.first())
    }

    private fun checkSelection() {
        when (tabs.size) {
            0 -> clearSelection()
            1 -> selectFirst()
            else -> {}
        }
    }
}
