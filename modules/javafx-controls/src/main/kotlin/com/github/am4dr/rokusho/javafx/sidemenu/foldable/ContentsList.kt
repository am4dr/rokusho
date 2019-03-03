package com.github.am4dr.rokusho.javafx.sidemenu.foldable

import com.github.am4dr.rokusho.javafx.util.Dummy
import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment

class ContentsList : VBox() {

    val folded: BooleanProperty = SimpleBooleanProperty(true)

    init {
        paddingProperty().bind(When(folded)
            .then(Insets(10.0, 0.0, 10.0, 0.0))
            .otherwise(Insets(10.0, 0.0, 10.0, 10.0)))
        val libraryLabel = Label("Libraries").apply {
            textAlignment = TextAlignment.RIGHT
            minWidth = 0.0
            font = Font(20.0)
            managedProperty().bind(folded.not())
            visibleProperty().bind(folded.not())
        }
        val dummy = Dummy()
        VBox.setVgrow(dummy, Priority.ALWAYS)
        val separator = Separator().apply {
            visibleProperty().bind(folded.not())
        }
        children.addAll(libraryLabel, separator, dummy)
    }
}