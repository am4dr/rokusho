package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.app.DraftMetaDataEditor
import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.ListBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
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

class ImageTile(image: Image, metaData: ImageMetaData = ImageMetaData()) : StackPane() {
    constructor(data: ImageData) : this(data.thumbnail, data.metaData)
    val imageVisibleProperty: BooleanProperty = SimpleBooleanProperty(true)
    val imageProperty: ObjectProperty<Image> = SimpleObjectProperty(image)
    val metaDataProperty: ObjectProperty<ImageMetaData> = SimpleObjectProperty(metaData)
    init {
        val imageView = ImageView().apply {
            imageProperty().bind(imageProperty)
            visibleProperty().bind(imageVisibleProperty)
        }
        val metaDataEditor = DraftMetaDataEditor(metaDataProperty.get(), imageProperty.get()).apply {
            onUpdate = { metaDataProperty.set(it); close() }
        }
        val addTagsButton = Button(" + ").apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
            onAction = EventHandler { metaDataEditor.show() }
        }
        val tagLabelNodes = object : ListBinding<Node>() {
            init { super.bind(metaDataProperty) }
            val labels = TransformedList(
                    FXCollections.observableList(metaDataProperty.get().tags),
                    ::createTagLabel)
            override fun computeValue(): ObservableList<Node> =
                    FXCollections.observableList(labels + addTagsButton)
        }
        val overlay = FlowPane(7.5, 5.0).apply {
            padding = Insets(10.0)
            background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
            Bindings.bindContent(children, tagLabelNodes)
            visibleProperty().bind(this@ImageTile.hoverProperty())
            prefWidthProperty().bind(Bindings.selectDouble(image, "width"))
            prefHeightProperty().bind(Bindings.selectDouble(image, "height"))
        }
        children.setAll(imageView, overlay)
    }
}
private fun createTagLabel(name: String): Node =
        Label(name).apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
        }
