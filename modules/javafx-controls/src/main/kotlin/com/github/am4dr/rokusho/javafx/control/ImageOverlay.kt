package com.github.am4dr.rokusho.javafx.control

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox

class ImageOverlay : VBox() {

    val widthRatioProperty: DoubleProperty = SimpleDoubleProperty(0.8)
    val heightRatioProperty: DoubleProperty = SimpleDoubleProperty(0.8)
    val imageProperty: ObjectProperty<Image> = SimpleObjectProperty()

    init {
        minWidth = 0.0
        minHeight = 0.0
        alignment = Pos.CENTER
        children.add(ImageView().apply {
            isPreserveRatio = true
            fitWidthProperty().bind(this@ImageOverlay.widthProperty().multiply(widthRatioProperty))
            fitHeightProperty().bind(this@ImageOverlay.heightProperty().multiply(heightRatioProperty))
            imageProperty().bind(imageProperty)
        })
    }

    fun show(image: Image) {
        imageProperty.set(image)
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}