package com.github.am4dr.rokusho.gui.old.thumbnail

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class ImageThumbnail(image: Image): ThumbnailFlowPane.Thumbnail {

    override val view: ImageView = ImageView(image)
    override val loadedProperty: ReadOnlyBooleanProperty = ReadOnlyBooleanWrapper(false).apply {
        bind(image.widthProperty().isNotEqualTo(0).and(image.heightProperty().isNotEqualTo(0)))
    }.readOnlyProperty
}
