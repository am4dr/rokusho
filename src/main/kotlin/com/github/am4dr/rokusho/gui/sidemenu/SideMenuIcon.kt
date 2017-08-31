package com.github.am4dr.rokusho.gui.sidemenu

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Pane

class SideMenuIcon : Pane() {
    val size: DoubleProperty = SimpleDoubleProperty(10.0)

    init {
        maxWidthProperty().bind(size)
        minWidthProperty().bind(size)
        maxHeightProperty().bind(size)
        minHeightProperty().bind(size)
    }
}