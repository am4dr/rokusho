package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.node.ImageOverlay
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class ThumbnailPane(imageDataList: ListProperty<ImageData>) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val imagesProperty = createEmptyListProperty<ImageData>()
    val view = ThumbnailPaneView()
    private val selectedTileProperty = SimpleObjectProperty<ImageTile>().apply {
        addListener { obs, old, new -> log.debug("change selectedTileProperty: $old -> $new") }
    }
    private val tiles = createEmptyListProperty<ImageTile>()
    init {
        imagesProperty.bind(imageDataList)
        val tileClickHandler = EventHandler<MouseEvent> { e ->
            val tile = e.source as? ImageTile ?: return@EventHandler
            log.info("tile clicked: $tile")
            selectedTileProperty.set(tile)
            view.overlayVisibleProperty.set(true)
        }
        imagesProperty.addListener(ListChangeListener {
            val newTiles = imagesProperty.map {
                ImageTile(it).apply { onMouseClicked = tileClickHandler }
            }
            selectedTileProperty.set(null)
            tiles.setAll(FXCollections.observableList(newTiles))
            log.debug("tiles changed - new.size: ${tiles.size}")
        })
        view.tilesProperty.bind(tiles)
        view.overlayImageProperty.bind(Bindings.createObjectBinding(
                    Callable { selectedTileProperty.get()?.data?.tempImage },
                    selectedTileProperty))
    }
}
class ThumbnailPaneView : StackPane() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val tilesProperty: ListProperty<ImageTile> = createEmptyListProperty()
    val overlayVisibleProperty: BooleanProperty = SimpleBooleanProperty(false)
    val overlayImageProperty: Property<Image> = SimpleObjectProperty<Image>()
    init {
        val overlay = ImageOverlay()
        overlay.visibleProperty().bind(overlayVisibleProperty)
        overlay.imageProperty.bind(overlayImageProperty)
        overlay.onMouseClicked = EventHandler<MouseEvent> { overlayVisibleProperty.set(false) }
        overlay.background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        this.children.addAll(
                ImageTileScrollPane().apply { tilesProperty.bind(this@ThumbnailPaneView.tilesProperty) },
                overlay)
    }
}
