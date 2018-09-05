package com.github.am4dr.rokusho.gui.viewer.factory

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.old.ImageOverlay
import com.github.am4dr.rokusho.gui.old.thumbnail.ImageThumbnail
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import kotlin.reflect.KClass


private const val thumbnailMaxWidth = 500.0
private const val thumbnailMaxHeight = 200.0

class ThumbnailRecordsViewerFactory : RecordsViewerFactory {

    private val imageLoader = UrlImageLoader()
    private val supportedTypes = listOf(ImageUrl::class)

    override fun isAcceptable(type: KClass<*>): Boolean = supportedTypes.contains(type)

    override fun create(library: RokushoLibrary<*>): RecordsViewer<*>? =
            @Suppress("UNCHECKED_CAST")
            when (library.type) {
                ImageUrl::class -> {
                    library as RokushoLibrary<ImageUrl>
                    createImageRecordsViewer<ImageUrl>("サムネイル",
                            { imageLoader.getImage(it.key.url) },
                            { imageLoader.getImage(it.key.url, thumbnailMaxWidth, thumbnailMaxHeight, true) },
                            { record, tags -> library.updateItemTags(record.key, tags) })
                }
                else -> null
            }
}

private fun <T> createImageRecordsViewer(label: String,
                                         getImage: (Record<T>) -> Image,
                                         getThumbnailImage: (Record<T>) -> Image,
                                         updateItemTags: (Record<T>, List<ItemTag>) -> Unit): RecordsViewer<T> {
    val thumbnailFactory = { it: Record<T> -> ImageThumbnail(getThumbnailImage(it)) }
    val imageViewer = createImageViewer()
    val viewer = RecordThumbnailViewer(thumbnailFactory).apply {
        children.add(imageViewer)
        onActionProperty.set { selected -> imageViewer.show(getImage(selected.first())) }
        updateTagsProperty.set(updateItemTags)
    }
    return RecordsViewer(label, viewer, viewer.records)
}

private fun createImageViewer(): ImageOverlay =
        ImageOverlay().apply {
            isVisible = false
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
            onMouseClicked = EventHandler { hide() }
        }
