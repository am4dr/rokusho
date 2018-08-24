package com.github.am4dr.rokusho.gui.old.sidemenu

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Pane

open class SideMenuIcon : Pane() {

    val size: DoubleProperty = SimpleDoubleProperty(10.0)
    val selectedProperty: BooleanProperty = SimpleBooleanProperty(false)

    init {
        maxWidthProperty().bind(size)
        minWidthProperty().bind(size)
        maxHeightProperty().bind(size)
        minHeightProperty().bind(size)
    }
}