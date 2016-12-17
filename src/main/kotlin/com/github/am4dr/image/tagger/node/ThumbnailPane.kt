package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.app.DraftTagEditor
import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.util.TransformedList
import javafx.beans.property.ListProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// TODO モデルを変更する限りnodeではない
class ThumbnailPane(imageDataList: ListProperty<ImageData>) : StackPane() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    init {
        val imagesProperty = SimpleListProperty<ImageData>().apply { bind(imageDataList) }
        val overlay = ImageOverlay().apply {
            visibleProperty().set(false)
            onMouseClicked = EventHandler<MouseEvent> { visibleProperty().set(false) }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
        val tileClickHandler = EventHandler<MouseEvent> { e ->
            val tile = e.source as? ImageTile ?: return@EventHandler
            log.info("tile clicked: $tile")
            overlay.show(tile.data.tempImage)
        }
        val tiles = TransformedList(imagesProperty) { data ->
            ImageTile(data).apply {
                onMouseClicked = tileClickHandler
                onAddTagsButtonClicked = {
                    DraftTagEditor(data).apply {
                        onUpdate = { e, new ->
                            imagesProperty.indexOf(data)
                                    .let { if (it >= 0) imagesProperty[it] = new }
                            close()
                        }
                    }.show()
                }
            }
        }
        tiles.addListener(ListChangeListener {
            log.debug("tiles changed")
        })
        children.addAll(
                ImageTileScrollPane(ReadOnlyObjectWrapper(tiles)),
                overlay)
    }
}
