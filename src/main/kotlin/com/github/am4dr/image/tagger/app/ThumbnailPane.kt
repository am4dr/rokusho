package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class ThumbnailPane(imageDataList: ListProperty<ImageData>) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val imagesProperty = SimpleListProperty<ImageData>()
    var imageData: ListProperty<ImageData>
        get() = imagesProperty
        set(value) = imagesProperty.bind(value)
    val view = ThumbnailPaneView()
    private val selectedTileProperty = SimpleObjectProperty<ImageTile>().apply {
        addListener { obs, old, new -> log.debug("change selectedTileProperty: $old -> $new") }
    }
    val tiles = SimpleListProperty<ImageTile>()
    init {
        this.imageData = imageDataList
        tiles.bind(Bindings.createObjectBinding(
                Callable { FXCollections.observableList(imagesProperty.filterNotNull().map(::ImageTile)) },
                imagesProperty))
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed - new.size: ${new.size}")
            selectedTileProperty.set(null)
            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source as? ImageTile ?: return@EventHandler
                log.info("tile clicked: $tile")
                selectedTileProperty.set(tile)
                view.overlayVisibleProperty.set(true)
            }
            new.map { tile -> tile.onMouseClicked = tileClickHandler }
            view.tiles = new
        }
        view.overlayImageProperty.bind(Bindings.createObjectBinding(
                    Callable { selectedTileProperty.get()?.data?.tempImage },
                    selectedTileProperty))
    }
}
class ThumbnailPaneView : StackPane() {
    val tilesProperty: ListProperty<ImageTile> = SimpleListProperty<ImageTile>()
    var tiles: ObservableList<ImageTile>
        get() = tilesProperty.get()
        set(value) = tilesProperty.set(value)
    val overlayVisibleProperty: BooleanProperty = SimpleBooleanProperty(false)
    val overlayImageProperty: Property<Image> = SimpleObjectProperty<Image>()
    init {
        val overlay = ImageOverlayPane()
        overlay.visibleProperty().bind(overlayVisibleProperty)
        overlay.imageProperty.bind(overlayImageProperty)
        overlay.onMouseClicked = EventHandler<MouseEvent> { overlayVisibleProperty.set(false) }
        this.children.addAll(
                ScrollPane().apply {
                    fitToWidthProperty().set(true)
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    content = FlowPane(10.0, 10.0).apply {
                        alignment = Pos.CENTER
                        rowValignment = VPos.BASELINE
                        tilesProperty.addListener { observableValue, old, new ->
                            children.setAll(new)
                        }
                    }
                },
                overlay)
    }
}
class ImageOverlayPane : VBox() {
    val imageView = ImageView().apply {
        isPreserveRatio = true
        fitWidthProperty().bind(
                When(this@ImageOverlayPane.widthProperty().multiply(2.0).lessThan(400.0))
                        .then(0.9).otherwise(0.75)
                        .multiply(this@ImageOverlayPane.widthProperty()))
        fitHeightProperty().bind(
                When(this@ImageOverlayPane.heightProperty().multiply(2.0).lessThan(400.0))
                        .then(0.9).otherwise(0.75)
                        .multiply(this@ImageOverlayPane.heightProperty()))
    }
    val imageProperty: Property<Image>
        get() = imageView.imageProperty()
    init {
        fillWidthProperty().set(true)
        alignment = Pos.CENTER
        background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        children.add(imageView)
    }
}
