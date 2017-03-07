package com.github.am4dr.rokusho.gui

import javafx.beans.binding.When
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableObjectValue
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font

abstract class TagNode : Pane() {
    open val onRemovedProperty: ObjectProperty<(TagNode) -> Unit> = SimpleObjectProperty({ _ -> })
}
class TextTagNode(text: ObservableObjectValue<String>? = null) : TagNode() {
    constructor(text: String) : this(ReadOnlyObjectWrapper(text).readOnlyProperty)
    companion object {
        private val buttonBackground = Background(BackgroundFill(Color.rgb(60, 50, 50), CornerRadii(2.0), null))
        private val buttonBackgroundHovered = Background(BackgroundFill(Color.DARKRED, CornerRadii(2.0), null))
    }
    private val label = Label().apply {
        textFill = Color.rgb(200, 200, 200)
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
    }
    val textProperty: StringProperty = label.textProperty()
    override val onRemovedProperty: ObjectProperty<(TagNode) -> Unit> = super.onRemovedProperty

    private val removeButton = Button(" × ").apply {
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
        onAction = EventHandler { onRemovedProperty.get().invoke(this@TextTagNode) }
    }
    init {
        if (text != null) { textProperty.bind(text) }
        children.addAll(
                Pane(removeButton).apply {
                    managedProperty().set(false)
                    visibleProperty().bind(this@TextTagNode.hoverProperty())
                    layoutXProperty().bind(label.layoutXProperty().subtract(removeButton.widthProperty().subtract(2)))
                },
                label
        )

    }
}