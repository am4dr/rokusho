package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.node.ImageOverlay
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
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
    private val imagesProperty = SimpleListProperty<ImageData>()
    val view = ThumbnailPaneView()
    private val selectedTileProperty = SimpleObjectProperty<ImageTile>().apply {
        addListener { obs, old, new -> log.debug("change selectedTileProperty: $old -> $new") }
    }
    private val tiles = SimpleListProperty<ImageTile>(FXCollections.observableList(mutableListOf()))
    init {
        imagesProperty.bind(imageDataList)
        imagesProperty.addListener(ListChangeListener {
            tiles.setAll(FXCollections.observableList(imagesProperty.filterNotNull().map(::ImageTile)))
        })
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed - new.size: ${new.size}")
            selectedTileProperty.set(null)
            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source as? ImageTile ?: return@EventHandler
                log.info("tile clicked: $tile")
                selectedTileProperty.set(tile)
                view.overlayVisibleProperty.set(true)
            }
            new.forEach { tile -> tile.onMouseClicked = tileClickHandler }
        }
        view.tilesProperty.bind(tiles)
        view.overlayImageProperty.bind(Bindings.createObjectBinding(
                    Callable { selectedTileProperty.get()?.data?.tempImage },
                    selectedTileProperty))
    }
}
class ThumbnailPaneView : StackPane() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val tilesProperty: ListProperty<ImageTile> = SimpleListProperty<ImageTile>()
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
