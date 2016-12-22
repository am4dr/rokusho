package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ImageOverlay
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
import com.github.am4dr.image.tagger.util.TransformedList
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.property.*
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

class ThumbnailPane(imageDataList: ListProperty<ImageData>) : StackPane() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    init {
        val imagesProperty = SimpleListProperty<ImageData>().apply { bind(imageDataList) }
        val overlay = ImageOverlay().apply {
            visibleProperty().set(false)
            onMouseClicked = EventHandler<MouseEvent> { visibleProperty().set(false) }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
        val tiles = TransformedList(imagesProperty) { data ->
            ImageTile(data).apply {
                onMouseClicked = EventHandler<MouseEvent> { e ->
                    val tile = e.source as? ImageTile ?: return@EventHandler
                    log.info("tile clicked: $tile")
                    //overlay.show(data.tempImage)
                }
                metaDataProperty.addListener { tags, old, new ->
                    imagesProperty.indexOf(data).let {
                        if (it >= 0) { imagesProperty[it] = data.copy(new) }
                    }
                }
            }
        }
        tiles.addListener(ListChangeListener {
            log.debug("tiles changed")
        })
        children.addAll(
//                ImageTileScrollPane(ReadOnlyObjectWrapper(tiles)),
                overlay)
    }
}
class ThumbnailPane2(scrollPane: ImageTileScrollPane) : StackPane() {
    val picturesProperty: ListProperty<Picture> = createEmptyListProperty()
    val overlay: ImageOverlay
    val overlayVisibleProperty: BooleanProperty
    val overlayImageProperty: ObjectProperty<Image>
    private var onOverlayClickedHandler: ObjectProperty<EventHandler<MouseEvent>>
    var onOverlayClicked: () -> Unit = {}
        set(value) { onOverlayClickedHandler.set(EventHandler { value() }) }

    init {
        overlayVisibleProperty = SimpleBooleanProperty(false)
        overlayImageProperty = SimpleObjectProperty()
        onOverlayClickedHandler = SimpleObjectProperty()
        onOverlayClicked = { overlayVisibleProperty.set(false) }

        overlay = ImageOverlay().apply {
            visibleProperty().bind(overlayVisibleProperty)
            imageProperty.bind(overlayImageProperty)
            onMouseClickedProperty().bind(onOverlayClickedHandler)
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
        scrollPane.picturesProperty.bind(picturesProperty)
        scrollPane.onTileClicked = { tile, pic ->
            showOverlay(pic.loader.image)
        }
        children.addAll(scrollPane, overlay)
    }
    fun showOverlay(image: Image) {
        overlayImageProperty.set(image)
        overlayVisibleProperty.set(true)
    }
}