package com.github.am4dr.rokusho.javafx.sidemenu

import com.github.am4dr.rokusho.javafx.util.Dummy
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

class RightTabColumn : StackPane() {

    val tabWidth: DoubleProperty = SimpleDoubleProperty(40.0)

    init {
        setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE)
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE)
        prefWidthProperty().bind(tabWidth)
        children.addAll(Dummy())
    }
}