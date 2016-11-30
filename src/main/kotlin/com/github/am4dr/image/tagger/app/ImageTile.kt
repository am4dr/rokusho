package com.github.am4dr.image.tagger.app

import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class ImageTile(val data: ImageData) : StackPane() {
    init {
        val overlay = VBox().apply {
            alignment = Pos.CENTER
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
            visibleProperty().bind(this@ImageTile.hoverProperty())
        }
        children.setAll(ImageView(data.thumnail), overlay)
    }
}