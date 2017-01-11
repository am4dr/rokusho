package com.github.am4dr.image.tagger.node

import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.property.*
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

class ThumbnailPane(scrollPane: ImageTileScrollPane = ImageTileScrollPane()) : StackPane() {
    val picturesProperty: ListProperty<Picture> = createEmptyListProperty()
    val overlay: ImageOverlay
    val overlayVisibleProperty: BooleanProperty
    val overlayImageProperty: ObjectProperty<Image>
    private var onOverlayClickedHandler: ObjectProperty<EventHandler<MouseEvent>>
    var onOverlayClicked: () -> Unit = {}
        set(value) { onOverlayClickedHandler.set(EventHandler { value() }) }
    var filterProperty: ObjectProperty<(Picture) -> Boolean> = SimpleObjectProperty({ it -> true })

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
        scrollPane.filterProperty.bind(filterProperty)
        scrollPane.onTileClicked = { tile ->
            showOverlay(tile.pictureProperty.get().loader.image)
        }
        children.addAll(scrollPane, overlay)
    }
    fun showOverlay(image: Image) {
        overlayImageProperty.set(image)
        overlayVisibleProperty.set(true)
    }
}