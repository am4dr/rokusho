package com.github.am4dr.rokusho.javafx.control.sample

import com.github.am4dr.javafx.sample_viewer.RestorableNode
import com.github.am4dr.rokusho.javafx.main.TabbedNodeContainer
import com.github.am4dr.rokusho.javafx.sidemenu.RightTabColumn
import com.github.am4dr.rokusho.javafx.sidemenu.foldable.FoldableSideColumn
import javafx.geometry.Orientation
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane

class MainLayoutSample : StackPane(), RestorableNode {

    init {
        val left = FoldableSideColumn()
        val right = RightTabColumn()
        val center = TabbedNodeContainer()
        HBox.setHgrow(center, Priority.ALWAYS)
        val base = HBox(left, Separator(Orientation.VERTICAL), center, Separator(Orientation.VERTICAL), right)
        children.add(base)
    }

    override fun restore(states: MutableMap<String, Any>?) {
    }
}