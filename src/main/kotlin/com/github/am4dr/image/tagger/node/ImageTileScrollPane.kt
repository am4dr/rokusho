package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImageTileScrollPane(tiles: ObservableValue<ObservableList<ImageTile>>) : ScrollPane() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val tilesProperty: ListProperty<ImageTile> = createEmptyListProperty()
    private val vValueHeightProperty = SimpleDoubleProperty()
    private var ranges = mutableListOf<TileRange>()
    private val maxRangeSize: Int = 200
    init {
        tilesProperty.bind(tiles)
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        content = FlowPane(10.0, 10.0).apply {
            padding = Insets(25.0, 0.0, 25.0, 0.0)
            alignment = Pos.CENTER
            Bindings.bindContent(children, tilesProperty)
            vValueHeightProperty.bind(vvalueProperty().multiply(heightProperty()))
        }
        val margin = SimpleDoubleProperty(2000.0)
        val screenTop = margin.multiply(-1).add(vValueHeightProperty)
        val screenBottom = margin.add(vValueHeightProperty)
        tilesProperty.addListener { tiles, old, new ->
            if (new != null && new.size < 1) { ranges.clear(); return@addListener }
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