package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.ListBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class Thumbnail(
        private val image: Image,
        private val tags: ObservableList<Tag>,
        private val tagParser: (String) -> Tag,
        private val tagNodeFactory: (Tag) -> TagNode) : StackPane() {
    val imageProperty: ObjectProperty<Image> = ReadOnlyObjectWrapper(image)
    private val tagNodes = TransformedList(tags) { tag ->
        tagNodeFactory(tag).apply { onRemovedProperty.set({ tags.remove(tag) }) }
    }
    private val tagInput = FittingTextField().apply {
        font = Font(14.0)
        background = Background(BackgroundFill(Color.WHITE, CornerRadii(2.0), null))
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        visibleProperty().set(false)
        managedProperty().bind(visibleProperty())
        focusedProperty().addListener { _, _, new ->
            if (new == false) { visibleProperty().set(false) }
        }
        onAction = EventHandler {
            when (text) { null, "" -> return@EventHandler }
            tags.add(tagParser(text))
            text = ""
        }
    }
    val tagInputFocusedProperty: ReadOnlyBooleanProperty = tagInput.focusedProperty()
    private val addTagButton = Button(" + ").apply {
        textFill = Color.rgb(200, 200, 200)
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        font = Font(14.0)
        background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
        onAction = EventHandler {
            tagInput.visibleProperty().set(true)
            tagInput.requestFocus()
        }
    }
    private val fpContents = object : ListBinding<Node>() {
        init { super.bind(tagNodes) }
        override fun computeValue(): ObservableList<Node> =
                FXCollections.observableList(tagNodes + tagInput + addTagButton)
    }
    init {
        maxWidthProperty().bind(imageProperty.get().widthProperty())
        maxHeightProperty().bind(imageProperty.get().heightProperty())
        val imageView = ImageView(image)
        val overlay = FlowPane(7.5, 5.0).apply {
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
            Bindings.bindContent(children, fpContents)
            visibleProperty().bind(this@Thumbnail.hoverProperty().or(tagInputFocusedProperty))
        }
        children.setAll(imageView, overlay)
    }
}