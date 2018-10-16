package com.github.am4dr.rokusho.app.gui.viewer.multipane.pane

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.gui.viewer.multipane.MultiPaneLibraryViewer
import com.github.am4dr.rokusho.app.gui.viewer.multipane.PaneFactory
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.LibraryItem
import com.github.am4dr.rokusho.core.metadata.PatchedTag
import com.github.am4dr.rokusho.gui.control.ImageOverlay
import com.github.am4dr.rokusho.gui.control.RemovableTag
import com.github.am4dr.rokusho.gui.thumbnail.CachedThumbnailFlowPane
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.StackedThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.ThumbnailTagEditor
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
                            { imageLoader.getImage(it.get().url) },
                            { imageLoader.getImage(it.get().url, thumbnailMaxWidth, thumbnailMaxHeight, true) },
                            { item, tags -> library.update(item.id, tags.map(ThumbnailTag::item).toSet()) },
                            { library.parseTag(it) })
                    MultiPaneLibraryViewer.Pane("サムネイル", viewer, viewer.records)
                }
                Path::class -> {
                    library as Library<Path>
                    val viewer = createImageRecordsViewer<Path>(
                            { imageLoader.getImage(it.get().toUri().toURL()) },
                            { imageLoader.getImage(it.get().toUri().toURL(), thumbnailMaxWidth, thumbnailMaxHeight, true) },
                            { item, tags -> library.update(item.id, tags.map(ThumbnailTag::item).toSet()) },
                            { library.parseTag(it) })
                    MultiPaneLibraryViewer.Pane("サムネイル", viewer, viewer.records) { isSupportedImageFile(it.get()) }
                }
                else -> null
            }
}

private fun patchedTagToString(tag: PatchedTag): String {
    val type = tag.data["type"]
    val value = tag.data["value"]
    val id = tag.base.name.name
    return when (type) {
        "text" -> value ?: id
        "value" -> "$id | ${value?.takeIf { it.isNotBlank() } ?: "-"}"
        "selection" -> "$id | ${value?.takeIf { it.isNotBlank() } ?: "-"}"
        else -> id
    }
}
data class ThumbnailTag(val text: String, val item: PatchedTag) {
    constructor(item: PatchedTag) : this(patchedTagToString(item), item)
}

private fun <T : Any> createImageRecordsViewer(getImage: (LibraryItem<T>) -> Image,
                                               getThumbnailImage: (LibraryItem<T>) -> Image,
                                               updateTags: (LibraryItem<T>, List<ThumbnailTag>) -> Unit,
                                               tagStringParser: (String) -> PatchedTag?): CachedThumbnailFlowPane<LibraryItem<T>> {
    val imageViewer = createImageViewer()
    val thumbnailFactory = { item: LibraryItem<T> ->
        val base = ImageThumbnail(getThumbnailImage(item))
        val overlayInputFocused = SimpleBooleanProperty(false)
        val overlaySupplier = {
            ThumbnailTagEditor<ThumbnailTag>().apply {
                tags.setAll(item.tags.map(::ThumbnailTag))
                onEditEndedProperty.set { new -> updateTags(item, new) }
                inputParserProperty.set { input -> tagStringParser(input)?.let { ThumbnailTag(it) } }
                tagNodeFactoryProperty.set { thumbnailTag ->
                    RemovableTag().apply {
                        textProperty().set(thumbnailTag.text)
                        onRemoved.set { remove(thumbnailTag) }
                    }
                }
                overlayInputFocused.bind(inputFocusedProperty())
            }
        }
        StackedThumbnail(base, overlaySupplier).apply {
            setOnMouseClicked { imageViewer.show(getImage(item)) }
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
