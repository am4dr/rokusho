package com.github.am4dr.image.tagger.app

import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox

class ImageOverlay : VBox() {
    val widthRatioProperty: DoubleProperty = SimpleDoubleProperty(0.8)
    val heightRatioProperty: DoubleProperty = SimpleDoubleProperty(0.8)
    private val imageView = ImageView()
    val imageProperty: Property<Image>
        get() = imageView.imageProperty()
    init {
        imageView.apply {
            isPreserveRatio = true
            fitWidthProperty().bind(this@ImageOverlay.widthProperty().multiply(widthRatioProperty))
            fitHeightProperty().bind(this@ImageOverlay.heightProperty().multiply(heightRatioProperty))
        }
        minWidth = 0.0
        minHeight = 0.0
        alignment = Pos.CENTER
        children.add(imageView)
    }
}