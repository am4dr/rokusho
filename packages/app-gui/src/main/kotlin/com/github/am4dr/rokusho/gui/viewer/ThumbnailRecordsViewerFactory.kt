package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.RecordsViewerFactory
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

    override fun acceptable(type: KClass<*>): Boolean = type == ImageUrl::class

    @Suppress("UNCHECKED_CAST")
    override fun create(library: RokushoLibrary<*>, container: RecordsViewerContainer<*>): RecordsViewer {
        library as RokushoLibrary<ImageUrl>
        container as RecordsViewerContainer<ImageUrl>
        return RecordsViewer("サムネイル", createThumbnailRecordsViewer(library, container, imageLoader))
    }
}

private const val thumbnailMaxWidth = 500.0
private const val thumbnailMaxHeight = 200.0

private fun UrlImageLoader.getImageThumbnail(record: Record<ImageUrl>): ImageThumbnail =
        ImageThumbnail(getImage(record.key.url, thumbnailMaxWidth, thumbnailMaxHeight, true))

private fun createThumbnailRecordsViewer(library: RokushoLibrary<ImageUrl>,
                                         container: RecordsViewerContainer<ImageUrl>,
                                         imageLoader: UrlImageLoader): Node {
    val thumbnailViewer = RecordThumbnailViewer(imageLoader::getImageThumbnail)
    val overlay = ImageOverlay().apply {
        isVisible = false
        onMouseClicked = EventHandler { isVisible = false }
        background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
    }
    thumbnailViewer.apply {
        records.bindContent(container.records)
        updateTagsProperty.set { record, tags -> library.updateItemTags(record.key, tags) }
        onActionProperty.set { selected ->
            overlay.imageProperty.value = imageLoader.getImage(selected.first().key.url)
            overlay.isVisible = true
        }
    }
    return StackPane(thumbnailViewer, overlay)
}