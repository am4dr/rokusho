package com.github.am4dr.rokusho.gui

import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.layout.BorderPane

class MainScene(val filer: ImageFiler, val directorySelectorPane: Node) : BorderPane() {
    val librariesNotSelectedProperty: BooleanProperty = SimpleBooleanProperty(true)
    init {
        centerProperty().bind(
                When(librariesNotSelectedProperty)
                        .then<Node>(directorySelectorPane)
                        .otherwise(filer))
    }
}