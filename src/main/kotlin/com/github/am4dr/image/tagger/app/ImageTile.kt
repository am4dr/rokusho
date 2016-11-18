package com.github.am4dr.image.tagger.app

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class ImageTile(data: ImageData) : Pane() {
    init {
        children.setAll(ImageView(data.thumnail))
    }
}