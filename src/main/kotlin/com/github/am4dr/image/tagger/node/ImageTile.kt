package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.util.TransformedList
import com.github.am4dr.rokusho.core.SimpleTag
import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType
import com.github.am4dr.rokusho.gui.FittingTextField
import com.github.am4dr.rokusho.gui.TagNode
import com.github.am4dr.rokusho.gui.TextTagNode
import javafx.beans.binding.Bindings
import javafx.beans.binding.ListBinding
import javafx.beans.property.*
import javafx.collections.FXCollections.observableList
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

const val thumbnailMaxWidth: Double = 500.0
const val thumbnailMaxHeight: Double = 200.0

class ImageTile(picture: Picture, tagNodeFactory : (Tag) -> TagNode) : StackPane() {
    constructor(picture: Picture) : this(picture,  ::createTagNode)
    val pictureProperty: ReadOnlyObjectProperty<Picture> = ReadOnlyObjectWrapper(picture)
    val imageProperty: ReadOnlyObjectProperty<Image> = ReadOnlyObjectWrapper(picture.loader.getImage(thumbnailMaxWidth, thumbnailMaxHeight, true))
    private val _metaDataProperty: ObjectProperty<ImageMetaData> = SimpleObjectProperty(picture.metaData)
    val metaDataProperty: ReadOnlyObjectProperty<ImageMetaData> = SimpleObjectProperty(picture.metaData)
    private fun updateMetaData() =
            (metaDataProperty as SimpleObjectProperty).set(_metaDataProperty.get())

    init {
        maxWidthProperty().bind(imageProperty.get().widthProperty())
        maxHeightProperty().bind(imageProperty.get().heightProperty())
        val imageView = ImageView().apply { imageProperty().bind(imageProperty) }
        val overlay = ImageTileOverlay(_metaDataProperty.get(), tagNodeFactory).apply {
            tagsProperty.addListener { obs, old, new ->
                _metaDataProperty.set(_metaDataProperty.get().copy(tags = new))
                updateMetaData()
            }
            visibleProperty().bind(this@ImageTile.hoverProperty().or(tagInputFocusedProperty))
        }
        children.setAll(imageView, overlay)
    }
}
private fun createTagNode(tag: Tag): TagNode = TextTagNode(tag.id)

private class ImageTileOverlay(data: ImageMetaData, tagNodeFactory : (Tag) -> TagNode) : FlowPane(7.5, 5.0) {
    private val tags = observableList(data.tags.toMutableList())
    val tagsProperty: ReadOnlyListProperty<Tag> = SimpleListProperty(observableList(data.tags.toMutableList()))
    private fun updateTags() = (tagsProperty as SimpleListProperty).setAll(tags)

    private val tagNodes = TransformedList(tags) { tag ->
        tagNodeFactory(tag).apply { onRemovedProperty.set({ tags.remove(tag); updateTags() }) }
    }
    private val tagInput = FittingTextField().apply {
        font = Font(14.0)
        background = Background(BackgroundFill(Color.WHITE, CornerRadii(2.0), null))
        padding = Insets(-1.0, 2.0, 0.0, 2.0)
        visibleProperty().set(false)
        managedProperty().bind(visibleProperty())
        focusedProperty().addListener { observableValue, old, new ->
            if (new == false) { visibleProperty().set(false); updateTags() }
        }
        onAction = EventHandler {
            when (text) { null, "" -> return@EventHandler }
            tags.add(SimpleTag(text, TagType.TEXT))
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
                observableList(tagNodes + tagInput + addTagButton)
    }
    init {
        padding = Insets(10.0)
        background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
        Bindings.bindContent(children, fpContents)
    }
}