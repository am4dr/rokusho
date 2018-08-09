package com.github.am4dr.rokusho.gui.old.tag

import javafx.beans.binding.When
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font

class TagView(text: ObservableStringValue) : Pane() {

    constructor(text: String) : this(SimpleStringProperty(text))

    companion object {
        private val buttonBackground = Background(BackgroundFill(Color.rgb(60, 50, 50), CornerRadii(2.0), null))
        private val buttonBackgroundHovered = Background(BackgroundFill(Color.DARKRED, CornerRadii(2.0), null))
    }

    var onRemoved: (() -> Unit)? = null

    init {
        val label =  createLabel().apply { textProperty().bind(text) }
        val removeButton = createRemoveButton()
        children.addAll(
                Pane(removeButton).apply {
                    managedProperty().set(false)
                    visibleProperty().bind(this@TagView.hoverProperty())
                    layoutXProperty().bind(label.layoutXProperty().subtract(removeButton.widthProperty().subtract(2)))
                },
                label)
    }

    private fun createLabel(): Label = Label().apply {
        textFill = Color.rgb(200, 200, 200)
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
    }

    private fun createRemoveButton(): Button= Button(" × ").apply {
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        textFillProperty().bind(
                When(hoverProperty())
                        .then(Color.WHITE)
                        .otherwise(Color.rgb(200, 200, 200)))
        backgroundProperty().bind(
                When(hoverProperty())
                        .then(buttonBackgroundHovered)
                        .otherwise(buttonBackground))
        setOnAction { onRemoved?.invoke() }
    }
}