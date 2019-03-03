package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.rokusho.javafx.main.TabbedNodeContainer
import com.github.am4dr.rokusho.javafx.main.TabbedNodeContainerViewModel
import com.github.am4dr.rokusho.javafx.util.Dummy
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane

class MainViewSample : StackPane() {

    init {
        padding = Insets(20.0)
        val vm = TabbedNodeContainerViewModel()
        vm.tabs.addAll(
            TabbedNodeContainerViewModel.TabbedNode(
                labeledDummy("List View"),
                "List"
            ),
            TabbedNodeContainerViewModel.TabbedNode(
                labeledDummy("Thumbnail View"),
                "Thumbnail"
            )
        )
        vm.onAddClicked.set {
            val dummy = labeledDummy("Generated")
            val node = TabbedNodeContainerViewModel.TabbedNode(
                dummy,
                System.identityHashCode(dummy).toString()
            )
            vm.tabs.add(node)
        }
        val viewer = TabbedNodeContainer().apply {
            emptyPane.value = labeledDummy("EMPTY PANE")
        }
        vm.injectTo(viewer)
        children.addAll(viewer)
    }

    fun labeledDummy(label: String): StackPane {
        return StackPane(
            Dummy(),
            FlowPane(Label(label)).apply {
                padding = Insets(20.0)
            })
    }
}