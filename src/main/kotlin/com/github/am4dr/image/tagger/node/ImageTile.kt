package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.app.DraftMetaDataEditor
import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.core.Tag
import javafx.beans.binding.Bindings
import javafx.beans.binding.ListBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
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
    val metaDataProperty: ReadOnlyObjectProperty<ImageMetaData> = _metaDataProperty
    init {
        maxWidthProperty().bind(imageProperty.get().widthProperty())
        maxHeightProperty().bind(imageProperty.get().heightProperty())
        val imageView = ImageView().apply { imageProperty().bind(imageProperty) }
        val addTagsButton = Button(" + ").apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
            onAction = EventHandler {
                DraftMetaDataEditor(metaDataProperty.get(), imageProperty.get()).apply {
                    onUpdate = { _metaDataProperty.set(it); close() }
                }.show()
            }
        }
        val tagLabelNodes = object : ListBinding<Node>() {
            init { super.bind(metaDataProperty) }
            override fun computeValue(): ObservableList<Node> =
                    FXCollections.observableList(
                            metaDataProperty.get().tags.map(tagNodeFactory)
                                    + addTagsButton)
        }
        val overlay = FlowPane(7.5, 5.0).apply {
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
            Bindings.bindContent(children, tagLabelNodes)
            visibleProperty().bind(this@ImageTile.hoverProperty())
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
