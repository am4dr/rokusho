package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.ListBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

private val transparentBlackBackground = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
class ImageTile(val data: ImageData) : StackPane() {
    val imageVisibleProperty: BooleanProperty = SimpleBooleanProperty(true)
    var onAddTagsButtonClicked: (ImageTile) -> Unit = {}
    val tags: ListProperty<String>
    private val tagLabelNodes: ObservableList<Node>
    init {
        val image = ImageView(data.thumbnail)
        val addTagsButton = Button(" + ").apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
            onAction = EventHandler { onAddTagsButtonClicked(this@ImageTile) }
        }
        tags = SimpleListProperty(FXCollections.observableList(data.metaData.tags.toMutableList()))
        tagLabelNodes = object : ListBinding<Node>() {
            init { super.bind(tags) }
            val labels = TransformedList(tags, ::createTagLabel)
            override fun computeValue(): ObservableList<Node> =
                    FXCollections.observableList(labels + addTagsButton)
        }
        val overlay = FlowPane(7.5, 5.0).apply {
            padding = Insets(10.0)
            background = transparentBlackBackground
            Bindings.bindContent(children, tagLabelNodes)
            visibleProperty().bind(this@ImageTile.hoverProperty())
            prefWidthProperty().bind(image.image.widthProperty())
            prefHeightProperty().bind(image.image.heightProperty())
        }
        children.setAll(image, overlay)
        image.visibleProperty().bind(imageVisibleProperty)
    }
}
private fun createTagLabel(name: String): Node =
        Label(name).apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
        }
