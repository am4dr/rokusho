package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.util.ConcatenatedList
import com.github.am4dr.rokusho.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

class Thumbnail(
        val image: Image,
        initialTags: List<ItemTag>,
        private val tagParser: (String) -> ItemTag,
        private val tagNodeFactory: (ItemTag) -> TagNode) : StackPane() {

    val imageLoadedProperty: ReadOnlyBooleanProperty = SimpleBooleanProperty(false).apply {
        bind(image.widthProperty().isNotEqualTo(0).and(image.heightProperty().isNotEqualTo(0)))
    }

    private val _tags: ObservableList<ItemTag> = observableArrayList(initialTags)
    val tags: ReadOnlyListProperty<ItemTag> = SimpleListProperty(observableArrayList(initialTags))
    private fun syncTags() = tags.setAll(_tags)

    private val tagNodes = TransformedList(_tags) { tag ->
        tagNodeFactory(tag).apply { onRemovedProperty.set({
            this@Thumbnail._tags.remove(tag)
            syncTags()
        }) }
    }
    private val tagInput = FittingTextField().apply {
        font = Font(14.0)
        background = Background(BackgroundFill(Color.WHITE, CornerRadii(2.0), null))
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        visibleProperty().set(false)
        managedProperty().bind(visibleProperty())
        focusedProperty().addListener { _, _, new ->
            if (new == false) {
                visibleProperty().set(false)
                syncTags()
            }
        }
        onAction = EventHandler {
            when (text) { null, "" -> return@EventHandler }
            this@Thumbnail._tags.add(tagParser(text))
            text = ""
        }
    }
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
    init {
        maxWidthProperty().bind(image.widthProperty())
        maxHeightProperty().bind(image.heightProperty())
        val imageView = ImageView(image)
        val overlay = FlowPane(7.5, 5.0).apply {
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
            Bindings.bindContent(children, ConcatenatedList(tagNodes, FXCollections.observableList(listOf(tagInput, addTagButton))))
            visibleProperty().bind(this@Thumbnail.hoverProperty().or(tagInput.focusedProperty()))
        }
        children.setAll(imageView, overlay)
    }
}
