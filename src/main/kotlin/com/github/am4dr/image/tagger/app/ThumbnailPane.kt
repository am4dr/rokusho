package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.node.ImageOverlay
import com.github.am4dr.image.tagger.node.ImageTile
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.FlowPane
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
    private var ranges: List<TileRange> = mutableListOf()
    private val maxRangeSize: Int = 200
    init {
        val overlay = ImageOverlay()
        overlay.visibleProperty().bind(overlayVisibleProperty)
        overlay.imageProperty.bind(overlayImageProperty)
        overlay.onMouseClicked = EventHandler<MouseEvent> { overlayVisibleProperty.set(false) }
        overlay.background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        val scrollPaneVValueProperty = SimpleDoubleProperty()
        this.children.addAll(
                ScrollPane().apply {
                    fitToWidthProperty().set(true)
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    content = FlowPane(10.0, 10.0).apply {
                        padding = Insets(25.0, 0.0, 25.0, 0.0)
                        alignment = Pos.CENTER
                        rowValignment = VPos.BASELINE
                        Bindings.bindContent(children, tilesProperty)
                        scrollPaneVValueProperty.bind(vvalueProperty().multiply(heightProperty()))
                    }
                },
                overlay)
        tilesProperty.addListener { tiles, old, new ->
            if (new.size < 1) { ranges = mutableListOf(); return@addListener }
            val screenTop = scrollPaneVValueProperty.subtract(2000)
            val screenBottom = scrollPaneVValueProperty.add(2000)
            val l = mutableListOf<TileRange>()
            (0..new.size/maxRangeSize).forEach { n ->
                val r = n*maxRangeSize..Math.min((n+1)*maxRangeSize-1, new.size-1)
                val tr = TileRange(new.slice(r))
                tr.imageVisibleProperty.bind(
                        Bindings.and(
                                screenTop.lessThanOrEqualTo(tr.lastTileProperty.value.layoutYProperty()),
                                screenBottom.greaterThanOrEqualTo(tr.firstTileProperty.value.layoutYProperty())))
                log.debug("TileRange created for: $r")
                tr.imageVisibleProperty.addListener { value, old, new ->
                    log.debug("TileRange $r : ${if(new) "visible" else "invisible"}")
                }
                l.add(tr)
            }
            ranges = l
        }
    }
}
private class TileRange(val list: List<ImageTile>) {
    val imageVisibleProperty: BooleanProperty = SimpleBooleanProperty(true)
    val firstTileProperty: Property<ImageTile> = SimpleObjectProperty<ImageTile>(list.first())
    val lastTileProperty: Property<ImageTile> = SimpleObjectProperty<ImageTile>(list.last())
    init {
        this.list.forEach { it.imageVisibleProperty.bind(imageVisibleProperty) }
    }
}
