package com.github.am4dr.rokusho.presenter.viewer.multipane.pane.thumbnail

import com.github.am4dr.rokusho.javafx.thumbnail.*
import com.github.am4dr.rokusho.library.LibraryItemTag
import com.github.am4dr.rokusho.presenter.ItemViewModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.image.Image
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class ImageThumbnailFactory(
    private val imageLoader: UrlImageLoader,
    private val thumbnailMaxWidth: Double,
    private val thumbnailMaxHeight: Double
) : ThumbnailFactory {

    companion object {
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
            Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }

    override fun maybeAcceptableType(kClass: KClass<*>): Boolean =
        kClass.isSubclassOf(Path::class)

    override fun isAcceptable(item: ItemViewModel<*>): Boolean {
        val itemInstance = item.item as? Path ?: return false
        return isSupportedImageFile(itemInstance)
    }

    override fun create(item: ItemViewModel<*>): ThumbnailNode<Path>? {
        if (!isAcceptable(item)) return null

        @Suppress("UNCHECKED_CAST")
        return ThumbnailNode(item as ItemViewModel<Path>, this::createThumbnail).apply {
            getFullImage.set { imageLoader.getImage(item.item.toUri().toURL()) }
        }
    }

    private fun createThumbnail(item: ItemViewModel<Path>): ThumbnailFlowPane.Thumbnail {
        val base = ImageThumbnail(getThumbnailImage(item.item.toUri().toURL()))
        val overlayInputFocused = SimpleBooleanProperty(false)
        val overlaySupplier = {
            val vm = ThumbnailTagEditorViewModel<LibraryItemTag>().apply {
                tags.addAll(item.tags) // TODO bindする
                tagNodeFactoryProperty.set(LibraryItemTagTagNodeFactory(this)::create)
                onEditEndedProperty.set { new -> item.updateTags(new) }
                inputParserProperty.set { input -> item.parseTagString(input) }
            }
            ThumbnailTagEditor().apply {
                overlayInputFocused.bind(inputFocusedProperty)
                vm.bindToView(this)
            }
        }
        return StackedThumbnail(base, overlaySupplier).apply {
            overlayVisibilityProperty().bind(hoverProperty().or(overlayInputFocused))
        }
    }

    private fun getThumbnailImage(url: URL): Image {
        return imageLoader.getImage(
            url,
            thumbnailMaxWidth,
            thumbnailMaxHeight,
            true
        )
    }
}