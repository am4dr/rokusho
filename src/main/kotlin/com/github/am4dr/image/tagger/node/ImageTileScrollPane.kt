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
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val picturesProperty: ListProperty<Picture>
    val tilesProperty: ListProperty<ImageTile>
    var onTileClicked: (ImageTile, Picture) -> Unit = { tile, pic -> }
    var filterProperty: ObjectProperty<(Picture) -> Boolean> = SimpleObjectProperty({ it -> true })

    private val vValueHeightProperty = SimpleDoubleProperty()
    init {
        picturesProperty = createEmptyListProperty()
        val margin = SimpleDoubleProperty(2000.0)
        val screenTop = margin.multiply(-1).add(vValueHeightProperty)
        val screenBottom = margin.add(vValueHeightProperty)
        tilesProperty = SimpleListProperty(TransformedList(picturesProperty) { pic ->
            tileFactory(pic).apply {
                onMouseClicked = EventHandler<MouseEvent> { onTileClicked(this, pic) }
                val filterPassedProperty = object : BooleanBinding() {
                    init { super.bind(filterProperty, metaDataProperty) }
                    override fun computeValue(): Boolean = filterProperty.get().invoke(pic)
                }
                visibleProperty().bind(
                        filterPassedProperty
                                .and(imageProperty.get().widthProperty().isNotEqualTo(0))
                                .and(layoutYProperty().greaterThanOrEqualTo(screenTop))
                                .and(layoutYProperty().lessThanOrEqualTo(screenBottom)))
                managedProperty().bind(
                        filterPassedProperty
                                .and(imageProperty.get().widthProperty().isNotEqualTo(0)))
            }
        })
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        content = FlowPane(10.0, 10.0).apply {
            padding = Insets(25.0, 0.0, 25.0, 0.0)
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
