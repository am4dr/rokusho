package com.github.am4dr.rokusho.gui2

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.gui.FittingTextField
import com.github.am4dr.rokusho.gui.TagNode
import com.github.am4dr.rokusho.util.ConcatenatedList
import com.github.am4dr.rokusho.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

// TODO 続けてタグを入力するときに入力を終了するまで外部に変更を通知しないようにする
class Thumbnail(
        val image: Image,
        initialTags: List<ItemTag>,
        private val tagParser: (String) -> ItemTag,
        private val tagNodeFactory: (ItemTag) -> TagNode) : StackPane() {

    val imageLoadedProperty: ReadOnlyBooleanProperty = SimpleBooleanProperty(false).apply {
        bind(image.widthProperty().isNotEqualTo(0).and(image.heightProperty().isNotEqualTo(0)))
    }
    val inputModeProperty: ReadOnlyBooleanProperty

    val tags: ObservableList<ItemTag> = FXCollections.observableArrayList(initialTags)
    private val tagNodes = TransformedList(this.tags) { tag ->
        tagNodeFactory(tag).apply { onRemovedProperty.set({ this@Thumbnail.tags.remove(tag) }) }
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
            this@Thumbnail.tags.add(tagParser(text))
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
        inputModeProperty = SimpleBooleanProperty().apply {
            bind(tagInput.focusedProperty())
        }
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
