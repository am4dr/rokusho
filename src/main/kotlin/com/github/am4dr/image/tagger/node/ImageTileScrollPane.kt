package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.util.TransformedList
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.*
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImageTileScrollPane(val tileFactory: (Picture) -> ImageTile = ::ImageTile) : ScrollPane() {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ImageTileScrollPane::class.java)
    }
    val picturesProperty: ListProperty<Picture> = createEmptyListProperty()
    private val tilesProperty: ListProperty<ImageTile>
    var onTileClicked: (ImageTile) -> Unit = { tile -> }
    val filterProperty: ObjectProperty<(Picture) -> Boolean> = SimpleObjectProperty({ it -> true })

    private val vValueHeightProperty = SimpleDoubleProperty()
    init {
        val contentPadding = 25.0
        assert(contentPadding / 2 > 0.0) { "(contentPadding / 2) must be bigger than 0.0 to check if the layout of a tile is finished" }
        val margin = SimpleDoubleProperty(2000.0)
        val screenTop = margin.multiply(-1).add(vValueHeightProperty)
        val screenBottom = margin.add(vValueHeightProperty)
        tilesProperty = SimpleListProperty(TransformedList(picturesProperty) { pic ->
            tileFactory(pic).apply {
                onMouseClicked = EventHandler<MouseEvent> { onTileClicked(this) }
                val filterPassedProperty = object : BooleanBinding() {
                    init { super.bind(filterProperty, metaDataProperty) }
                    override fun computeValue(): Boolean = filterProperty.get().invoke(pictureProperty.get())
                }
                val layoutFinishedProperty = layoutYProperty().greaterThan(contentPadding/2)
                visibleProperty().bind(
                        managedProperty()
                                .and(layoutFinishedProperty)
                                .and(layoutYProperty().greaterThanOrEqualTo(screenTop))
                                .and(layoutYProperty().lessThanOrEqualTo(screenBottom)))
                managedProperty().bind(
                        imageProperty.get().widthProperty().isNotEqualTo(0)     // image is loaded
                                .and(filterPassedProperty))
            }
        })
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        content = FlowPane(10.0, 10.0).apply {
            padding = Insets(contentPadding, 0.0, contentPadding, 0.0)
            alignment = Pos.CENTER
            Bindings.bindContent(children, tilesProperty)
            vValueHeightProperty.bind(vvalueProperty().multiply(heightProperty()))
            setOnScroll {
                vvalue -= it.deltaY * 3 / height
                it.consume()
            }
        }
    }
}
