package com.github.am4dr.rokusho.gui.sidemenu

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane

class SideMenuPane : SplitPane() {
    val menu: ExpandableSideMenu = ExpandableSideMenu()
    val content: ObjectProperty<Node> = SimpleObjectProperty(Label("Dummy"))

    init {
        menu.onClose.set {
            setDividerPosition(0, menu.iconSizeProperty.value / width)
        }
        menu.onExpand.set {
            setDividerPosition(0, menu.expandedWidth.value / width)
        }

        SplitPane.setResizableWithParent(menu, false)
        items.addAll(menu, BorderPane().apply { centerProperty().bind(content) })
    }
}