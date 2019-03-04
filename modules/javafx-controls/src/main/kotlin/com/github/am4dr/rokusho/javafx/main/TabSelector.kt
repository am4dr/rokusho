package com.github.am4dr.rokusho.javafx.main

import com.github.am4dr.rokusho.javafx.binding.ObservableSelector
import com.github.am4dr.rokusho.javafx.binding.createBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.Node
import java.util.*

class TabSelector {

    private val tabToNode = IdentityHashMap<Tab, Node>()
    private val selector = ObservableSelector<Tab>()

    val selectedTab: ReadOnlyObjectProperty<Tab?> = selector.selected
    val _selectedNode = ReadOnlyObjectWrapper<Node?>().apply { bind(createBinding({ tabToNode[selectedTab.value]}, selectedTab)) }
    val selectedNode: ReadOnlyObjectProperty<Node?> = _selectedNode.readOnlyProperty

    fun add(tab: Tab, node: Node) {
        tabToNode[tab] = node
        selector.list.add(tab)
    }

    fun remove(tab: Tab) {
        tabToNode.remove(tab)
        selector.list.remove(tab)
    }

    fun select(tab: Tab) {
        selector.select(tab)
    }

    fun select(index: Int) {
        selector.select(index)
    }
}
