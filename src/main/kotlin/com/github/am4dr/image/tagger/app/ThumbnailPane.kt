package com.github.am4dr.image.tagger.app

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.ListProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
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
    val pane: Pane
    private val sp = ScrollPane().apply {
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    }
    private val tiles = SimpleListProperty<ImageTile>()
    private val selectedTileProperty = SimpleObjectProperty<ImageTile>().apply {
        addListener { obs, old, new -> log.debug("change selectedTileProperty: $old -> $new") }
    }
    private val overlayTile = OverlayTile()
    private val flowPane = FlowPane(10.0, 10.0).apply {
        alignment = Pos.CENTER
        rowValignment = VPos.BASELINE
    }
    init {
        this.imageData = imageDataList
        pane = StackPane(
                sp.apply { content = flowPane },
                overlayTile.apply { visibleProperty().set(false) })
        tiles.bind(Bindings.createObjectBinding(
                Callable { FXCollections.observableList(imagesProperty.filterNotNull().map(::ImageTile)) },
                imagesProperty))
        tiles.addListener { observable, old, new ->
            log.debug("tiles changed - new.size: ${new.size}")
            selectedTileProperty.set(null)
            val tileClickHandler = EventHandler<MouseEvent> { e ->
                val tile = e.source as? ImageTile ?: return@EventHandler
                log.debug("tile clicked")
                if (selectedTileProperty.get() === tile) {
                    selectedTileProperty.set(null)
                    return@EventHandler
                }
                selectedTileProperty.set(tile)
                showOverlayTile()
            }
            new.map { tile -> tile.onMouseClicked = tileClickHandler }
            flowPane.children.setAll(new)
        }
        overlayTile.apply {
            image.bind(Bindings.createObjectBinding(
                    Callable { selectedTileProperty.get()?.data?.tempImage },
                    selectedTileProperty))
            onMouseClicked = EventHandler<MouseEvent> { e -> hideOverlayTile() }
        }
    }
    private fun showOverlayTile() {
        overlayTile.visibleProperty().set(true)
    }
    private fun hideOverlayTile() {
        overlayTile.visibleProperty().set(false)
        selectedTileProperty.set(null)
    }
    private class OverlayTile() : VBox() {
        val imageView = ImageView().apply {
            isPreserveRatio = true
            fitWidthProperty().bind(
                    When(this@OverlayTile.widthProperty().multiply(2.0).lessThan(400.0))
                            .then(0.9).otherwise(0.75)
                            .multiply(this@OverlayTile.widthProperty()))
            fitHeightProperty().bind(
                    When(this@OverlayTile.heightProperty().multiply(2.0).lessThan(400.0))
                            .then(0.9).otherwise(0.75)
                            .multiply(this@OverlayTile.heightProperty()))
        }
        val image: Property<Image>
            get() = imageView.imageProperty()
        init {
            fillWidthProperty().set(true)
            alignment = Pos.CENTER
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
            children.add(imageView)
        }
    }
}
