package com.github.am4dr.rokusho.gui.viewer.factory

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.old.ImageOverlay
import com.github.am4dr.rokusho.gui.old.thumbnail.ImageThumbnail
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import kotlin.reflect.KClass

class ThumbnailRecordsViewerFactory : RecordsViewerFactory {

    private val imageLoader = UrlImageLoader()

    override fun isAcceptable(type: KClass<*>): Boolean = type == ImageUrl::class

    override fun create(library: RokushoLibrary<*>): RecordsViewer<*>? {
        if (library.type != ImageUrl::class) return null
        @Suppress("UNCHECKED_CAST")
        library as RokushoLibrary<ImageUrl>
        val (view, node) = createThumbnailRecordsViewer(library, imageLoader)
        return RecordsViewer("サムネイル", node, view.records)
    }
}

private const val thumbnailMaxWidth = 500.0
private const val thumbnailMaxHeight = 200.0

private fun UrlImageLoader.getImageThumbnail(record: Record<ImageUrl>): ImageThumbnail =
        ImageThumbnail(getImage(record.key.url, thumbnailMaxWidth, thumbnailMaxHeight, true))

private fun createThumbnailRecordsViewer(library: RokushoLibrary<ImageUrl>,
                                         imageLoader: UrlImageLoader): Pair<RecordThumbnailViewer<ImageUrl>, Node> {
    val thumbnailViewer = RecordThumbnailViewer(imageLoader::getImageThumbnail)
    val overlay = ImageOverlay().apply {
        isVisible = false
        onMouseClicked = EventHandler { isVisible = false }
        background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
    }
    thumbnailViewer.apply {
        updateTagsProperty.set { record, tags -> library.updateItemTags(record.key, tags) }
        onActionProperty.set { selected ->
            overlay.imageProperty.value = imageLoader.getImage(selected.first().key.url)
            overlay.isVisible = true
        }
    }
    return thumbnailViewer to StackPane(thumbnailViewer, overlay)
}