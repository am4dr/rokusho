package com.github.am4dr.image.tagger.app

import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.layout.BorderPane

class MainFrame(val filer: ImageFiler, val directorySelectorPane: Node) : BorderPane() {
    val librariesNotSelectedProperty: BooleanProperty
    init {
        librariesNotSelectedProperty = SimpleBooleanProperty(true)
        centerProperty().bind(
                When(librariesNotSelectedProperty)
                        .then<Node>(directorySelectorPane)
                        .otherwise(filer))
    }
}