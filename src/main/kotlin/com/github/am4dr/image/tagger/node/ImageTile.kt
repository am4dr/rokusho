package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.core.Tag
import com.github.am4dr.image.tagger.core.TextTag
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections.observableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

const val thumbnailMaxWidth: Double = 500.0
const val thumbnailMaxHeight: Double = 200.0

class ImageTile(picture: Picture, tagNodeFactory : (Tag) -> Node) : StackPane() {
    constructor(picture: Picture) : this(picture,  ::createTagNode)
    val pictureProperty: ReadOnlyObjectProperty<Picture> = ReadOnlyObjectWrapper(picture)
    val imageProperty: ReadOnlyObjectProperty<Image> = ReadOnlyObjectWrapper(picture.loader.getImage(thumbnailMaxWidth, thumbnailMaxHeight, true))
    private val _metaDataProperty: ObjectProperty<ImageMetaData> = SimpleObjectProperty(picture.metaData)
    val metaDataProperty: ReadOnlyObjectProperty<ImageMetaData> = SimpleObjectProperty(picture.metaData)
    private fun updateMetaData() {
        metaDataProperty as SimpleObjectProperty
        metaDataProperty.set(_metaDataProperty.get())
    }
    init {
        maxWidthProperty().bind(imageProperty.get().widthProperty())
        maxHeightProperty().bind(imageProperty.get().heightProperty())
        val imageView = ImageView().apply { imageProperty().bind(imageProperty) }
        val tagInput = FittingTextField().apply {
            font = Font(14.0)
            background = Background(BackgroundFill(Color.WHITE, CornerRadii(2.0), null))
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            visibleProperty().set(false)
            managedProperty().bind(visibleProperty())
            focusedProperty().addListener { observableValue, old, new ->
                if (new == false) { visibleProperty().set(false); updateMetaData() }
            }
            onAction = EventHandler {
                when (text) { null, "" -> return@EventHandler }
                with(_metaDataProperty) {
                    val newTags = get().tags + TextTag(text)
                    set(get().copy(tags = newTags))
                }
                text = ""
            }
        }
        val addTagsButton = Button(" + ").apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
            onAction = EventHandler {
                tagInput.visibleProperty().set(true)
                tagInput.requestFocus()
            }
        }
        val tagLabelNodes = SimpleListProperty<Node>(observableList(
                _metaDataProperty.get().tags.map(tagNodeFactory) + tagInput + addTagsButton))
        _metaDataProperty.addListener { observableValue, old, new ->
            tagLabelNodes.setAll(observableList(
                    _metaDataProperty.get().tags.map(tagNodeFactory) + tagInput + addTagsButton))
        }
        val overlay = FlowPane(7.5, 5.0).apply {
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
            Bindings.bindContent(children, tagLabelNodes)
            visibleProperty().bind(this@ImageTile.hoverProperty().or(tagInput.focusedProperty()))
        }
        children.setAll(imageView, overlay)
    }
}
fun createTagNode(tag: Tag): Node =
        Label(tag.name).apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
        }
