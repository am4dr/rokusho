package com.github.am4dr.rokusho.gui.control

import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font

class RemovableTag : AnchorPane() {

    private val text: StringProperty = SimpleStringProperty()
    fun textProperty(): StringProperty = text

    val onRemoved: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()

    val label = createLabel().also { it.textProperty().bind(text) }
    val button = createRemoveButton()

    private val buttonVisibility = SimpleBooleanProperty(true)
    fun buttonVisibilityProperty() = buttonVisibility

    companion object {
        private val buttonBackground = Background(BackgroundFill(Color.rgb(60, 50, 50), CornerRadii(2.0), null))
        private val buttonBackgroundHovered = Background(BackgroundFill(Color.DARKRED, CornerRadii(2.0), null))
    }

    init {
        button.visibleProperty().bind(buttonVisibility)
        prefWidthProperty().bind(label.widthProperty().add(
                When(button.visibleProperty())
                        .then(button.widthProperty().subtract(2))
                        .otherwise(0)))

        setRightAnchor(button, 0.0)
        setLeftAnchor(label, 0.0)
        children.addAll(button, label)
    }

    private fun createLabel(): Label = Label().apply {
        textFill = Color.rgb(200, 200, 200)
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
    }

    private fun createRemoveButton(): Button = Button(" × ").apply {
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
        setOnAction { onRemoved.get()?.invoke() }
    }
}