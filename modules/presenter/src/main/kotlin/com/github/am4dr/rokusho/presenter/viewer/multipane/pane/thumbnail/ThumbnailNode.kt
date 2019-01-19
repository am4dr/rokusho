package com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail

import com.github.am4dr.rokusho.javafx.thumbnail.ThumbnailFlowPane
import com.github.am4dr.rokusho.presenter.ItemViewModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.image.Image

class ThumbnailNode<T : Any>(
    item: ItemViewModel<T>,
    thumbnailFactory: (ItemViewModel<T>) -> ThumbnailFlowPane.Thumbnail
) : ThumbnailFlowPane.Thumbnail {

    private val thumbnail = thumbnailFactory(item)

    override val view: Node = thumbnail.view
    override val loadedProperty: ReadOnlyBooleanProperty = thumbnail.loadedProperty

    // TODO replace with getFullNode
    val getFullImage: ObjectProperty<(() -> Image?)?> = SimpleObjectProperty()
}