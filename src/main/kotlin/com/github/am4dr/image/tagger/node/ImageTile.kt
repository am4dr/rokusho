package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.app.DraftTagEditor
import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.ListBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
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
    private val tagNodes: ObservableList<Node>
    init {
        val image = ImageView(data.thumbnail)
        val overlay = FlowPane(7.5, 5.0)
        val addTagsButton = Button(" + ").apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
            onMouseClicked = EventHandler {
                DraftTagEditor(data).apply {
                    onUpdate = { e, str ->
                        e.update()
                        close()
                    }
                    show()
                }
            }
        }
        val tagLabels = TransformedList(data.metaData.tags, ::createTagLabel)
        tagNodes = object : ListBinding<Node>() {
            init { super.bind(tagLabels) }
            override fun computeValue(): ObservableList<Node> =
                    FXCollections.observableList(tagLabels.plus(addTagsButton).toMutableList())
        }
        overlay.apply {
            padding = Insets(10.0)
            background = transparentBlackBackground
            Bindings.bindContent(children, tagNodes)
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
