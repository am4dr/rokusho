package com.github.am4dr.image.tagger.app

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font

private val transparentBlackBackground = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null))
class ImageTile(val data: ImageData) : StackPane() {
    init {
        val image = ImageView(data.thumnail)
        val overlay = AnchorPane().apply {
            val tagsPane = FlowPane(7.5, 5.0)
            tagsPane.children.addAll(data.tags.map(::createTagLabel))
            val d = 10.0
            AnchorPane.setTopAnchor(tagsPane, d)
            AnchorPane.setLeftAnchor(tagsPane, d)
            AnchorPane.setRightAnchor(tagsPane, d)
            AnchorPane.setBottomAnchor(tagsPane, d)
            children.add(tagsPane)
            background = transparentBlackBackground
            visibleProperty().bind(this@ImageTile.hoverProperty())
            prefWidthProperty().bind(image.image.widthProperty())
            prefHeightProperty().bind(image.image.heightProperty())
        }
        children.setAll(image, overlay)
    }
}
private fun createTagLabel(name: String): Node =
        Label(name).apply {
            textFill = Color.rgb(200, 200, 200)
            padding = Insets(-1.0, 2.0, 0.0, 2.0)
            font = Font(14.0)
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(2.0), null))
        }
