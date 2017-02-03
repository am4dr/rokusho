package com.github.am4dr.rokusho.gui

import com.github.am4dr.image.tagger.util.TransformedList
import com.github.am4dr.rokusho.app.ImageItem
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

class ThumbnailNode(
        private val list: ObservableList<ImageItem>,
        private val filter: ObservableObjectValue<(ImageItem) -> Boolean>,
        private val thumbnailFactory: (ImageItem) -> Thumbnail,
        private val imageLoader: UrlImageLoader) : StackPane() {
    companion object {
        const val thumbnailMaxWidth: Double = 500.0
        const val thumbnailMaxHeight: Double = 200.0
    }
    private val vValueHeightProperty = SimpleDoubleProperty()
    private val margin = SimpleDoubleProperty(2000.0)
    private val screenTop = margin.multiply(-1).add(vValueHeightProperty)
    private val screenBottom = margin.add(vValueHeightProperty)
    private val thumbnails: ObservableList<Thumbnail> = TransformedList(list) { item ->
        thumbnailFactory(item).apply {
            layoutX = -thumbnailMaxWidth
            onMouseClicked = EventHandler<MouseEvent> { showOverlay(imageLoader.getImage(item.url)) }
            val filterPassedProperty = object : BooleanBinding() {
                init { super.bind(filter, item) }
                override fun computeValue(): Boolean = filter.value.invoke(item)
            }
            visibleProperty().bind(
                    managedProperty()
                            .and(layoutYProperty().greaterThanOrEqualTo(screenTop))
                            .and(layoutYProperty().lessThanOrEqualTo(screenBottom)))
            managedProperty().bind(
                    imageProperty.get().widthProperty().isNotEqualTo(0)     // image is loaded
                            .and(filterPassedProperty))
        }
    }
    private val overlay: ImageOverlay = ImageOverlay().apply {
        visibleProperty().set(false)
        background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        onMouseClicked = EventHandler<MouseEvent> { visibleProperty().set(false) }
    }
    private val scrollpane: ScrollPane = ScrollPane().apply {
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        content = FlowPane(10.0, 10.0).apply {
            padding = Insets(25.0, 0.0, 25.0, 0.0)
            alignment = Pos.CENTER
            Bindings.bindContent(children, thumbnails)
            vValueHeightProperty.bind(vvalueProperty().multiply(heightProperty()))
            setOnScroll {
                vvalue -= it.deltaY * 3 / height
                it.consume()
            }
        }
    }
    init {
        children.addAll(scrollpane, overlay)
    }
    fun showOverlay(image: Image) {
        overlay.imageProperty.set(image)
        overlay.visibleProperty().set(true)
    }
}

