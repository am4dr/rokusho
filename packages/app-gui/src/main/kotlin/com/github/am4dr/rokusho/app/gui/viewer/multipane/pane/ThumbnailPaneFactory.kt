package com.github.am4dr.rokusho.app.gui.viewer.multipane.pane

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.gui.viewer.multipane.MultiPaneLibraryViewer
import com.github.am4dr.rokusho.app.gui.viewer.multipane.PaneFactory
import com.github.am4dr.rokusho.gui.control.ImageOverlay
import com.github.am4dr.rokusho.gui.control.RemovableTag
import com.github.am4dr.rokusho.gui.thumbnail.CachedThumbnailFlowPane
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.StackedThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.ThumbnailTagEditor
import com.github.am4dr.rokusho.old.core.library.ItemTag
import com.github.am4dr.rokusho.old.core.library.Library
import com.github.am4dr.rokusho.old.core.library.Record
import com.github.am4dr.rokusho.old.core.library.Tag
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass


private const val thumbnailMaxWidth = 500.0
private const val thumbnailMaxHeight = 200.0

class ThumbnailPaneFactory : PaneFactory {

    companion object {
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) = Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val imageLoader = UrlImageLoader()
    private val supportedTypes = listOf(ImageUrl::class, Path::class)

    override fun isAcceptable(type: KClass<*>): Boolean = supportedTypes.contains(type)

    override fun create(library: Library<*>): MultiPaneLibraryViewer.Pane<*>? =
            @Suppress("UNCHECKED_CAST")
            when (library.type) {
                ImageUrl::class -> {
                    library as Library<ImageUrl>
                    val viewer = createImageRecordsViewer<ImageUrl>(
                            { imageLoader.getImage(it.key.url) },
                            { imageLoader.getImage(it.key.url, thumbnailMaxWidth, thumbnailMaxHeight, true) },
                            { record, tags -> library.updateItemTags(record.key, tags) })
                    MultiPaneLibraryViewer.Pane("サムネイル", viewer, viewer.records)
                }
                Path::class -> {
                    library as Library<Path>
                    val viewer = createImageRecordsViewer<Path>(
                            { imageLoader.getImage(it.key.toUri().toURL()) },
                            { imageLoader.getImage(it.key.toUri().toURL(), thumbnailMaxWidth, thumbnailMaxHeight, true) },
                            { record, tags -> library.updateItemTags(record.key, tags) })
                    MultiPaneLibraryViewer.Pane("サムネイル", viewer, viewer.records) { isSupportedImageFile(it.key) }
                }
                else -> null
            }
}

private fun <T> createImageRecordsViewer(getImage: (Record<T>) -> Image,
                                         getThumbnailImage: (Record<T>) -> Image,
                                         updateItemTags: (Record<T>, List<ItemTag>) -> Unit): CachedThumbnailFlowPane<Record<T>> {
    val imageViewer = createImageViewer()
    val thumbnailFactory = { record: Record<T> ->
        val base = ImageThumbnail(getThumbnailImage(record))
        val overlayInputFocused = SimpleBooleanProperty(false)
        val overlaySupplier = {
            ThumbnailTagEditor<ItemTag>().apply {
                tags.setAll(record.itemTags)
                onEditEndedProperty.set { new -> updateItemTags(record, new) }
                inputParserProperty.set { text: String -> ItemTag(Tag(text, Tag.Type.TEXT, mapOf("value" to text)), null) }
                tagNodeFactoryProperty.set { itemTag: ItemTag ->
                    val text = itemTag.run {
                        when (tag.type) {
                            Tag.Type.TEXT -> value ?: tag.id
                            Tag.Type.VALUE -> "${tag.id} | ${value?.takeIf { it.isNotBlank() } ?: "-"}"
                            Tag.Type.SELECTION -> "${tag.id} | ${value?.takeIf { it.isNotBlank() } ?: "-"}"
                            Tag.Type.OTHERS -> tag.id
                        }
                    }
                    RemovableTag().apply {
                        textProperty().set(text)
                        onRemoved.set {
                            remove(itemTag)
                        }
                    }
                }
                overlayInputFocused.bind(inputFocusedProperty())
            }
        }
        StackedThumbnail(base, overlaySupplier).apply {
            setOnMouseClicked { imageViewer.show(getImage(record)) }
            overlayVisibilityProperty().bind(hoverProperty().or(overlayInputFocused))
        }
    }
    return CachedThumbnailFlowPane(thumbnailFactory).apply { children.add(imageViewer) }
}

private fun createImageViewer(): ImageOverlay =
        ImageOverlay().apply {
            isVisible = false
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
            onMouseClicked = EventHandler { hide() }
        }
