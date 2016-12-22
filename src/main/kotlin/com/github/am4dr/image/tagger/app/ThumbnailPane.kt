package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ImageOverlay
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
import com.github.am4dr.image.tagger.util.TransformedList
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThumbnailPane(imageDataList: ListProperty<ImageData>) : StackPane() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    init {
        val imagesProperty = SimpleListProperty<ImageData>().apply { bind(imageDataList) }
        val overlay = ImageOverlay().apply {
            visibleProperty().set(false)
            onMouseClicked = EventHandler<MouseEvent> { visibleProperty().set(false) }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
        val tiles = TransformedList(imagesProperty) { data ->
            ImageTile(data).apply {
                onMouseClicked = EventHandler<MouseEvent> { e ->
                    val tile = e.source as? ImageTile ?: return@EventHandler
                    log.info("tile clicked: $tile")
                    overlay.show(data.tempImage)
                }
                metaDataProperty.addListener { tags, old, new ->
                    imagesProperty.indexOf(data).let {
                        if (it >= 0) { imagesProperty[it] = data.copy(new) }
                    }
                }
            }
        }
        tiles.addListener(ListChangeListener {
            log.debug("tiles changed")
        })
        children.addAll(
                ImageTileScrollPane(ReadOnlyObjectWrapper(tiles)),
                overlay)
    }
}
class ThumbnailPane2() : StackPane() {
    val  picturesProperty: ListProperty<Picture> = createEmptyListProperty()
}