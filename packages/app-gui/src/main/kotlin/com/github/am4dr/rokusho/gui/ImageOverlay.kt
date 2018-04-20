package com.github.am4dr.rokusho.gui

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class ImageOverlay : VBox() {

    companion object {
        fun <T : Node> attach(node: T): Triple<StackPane, T, ImageOverlay> {
            val overlay = ImageOverlay()
            val stackPane = StackPane(node, overlay)
            return Triple(stackPane, node, overlay)
        }
    }

    val widthRatioProperty: DoubleProperty = SimpleDoubleProperty(0.8)
    val heightRatioProperty: DoubleProperty = SimpleDoubleProperty(0.8)
    private val imageView = ImageView()
    val imageProperty: ObjectProperty<Image>
    init {
        imageProperty = SimpleObjectProperty()
        imageView.apply {
            isPreserveRatio = true
            fitWidthProperty().bind(this@ImageOverlay.widthProperty().multiply(widthRatioProperty))
            fitHeightProperty().bind(this@ImageOverlay.heightProperty().multiply(heightRatioProperty))
            imageProperty().bind(imageProperty)
        }
        minWidth = 0.0
        minHeight = 0.0
        alignment = Pos.CENTER
        children.add(imageView)
    }
}