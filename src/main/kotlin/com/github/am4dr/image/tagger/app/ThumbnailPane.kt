package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.node.ImageOverlay
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
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
        val tiles = createEmptyListProperty<ImageTile>()
        val overlay = ImageOverlay().apply {
            visibleProperty().set(false)
            onMouseClicked = EventHandler<MouseEvent> { visibleProperty().set(false) }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
        children.addAll(
                ImageTileScrollPane().apply { tilesProperty.bind(tiles) },
                overlay)
        val tileClickHandler = EventHandler<MouseEvent> { e ->
            val tile = e.source as? ImageTile ?: return@EventHandler
            log.info("tile clicked: $tile")
            overlay.imageProperty.value = tile.data.tempImage
            overlay.visibleProperty().set(true)
        }
        imagesProperty.addListener(ListChangeListener {
            val newTiles = imagesProperty.map {
                ImageTile(it).apply { onMouseClicked = tileClickHandler }
            }
            tiles.setAll(FXCollections.observableList(newTiles))
            log.debug("tiles changed - new.size: ${tiles.size}")
        })
    }
}
