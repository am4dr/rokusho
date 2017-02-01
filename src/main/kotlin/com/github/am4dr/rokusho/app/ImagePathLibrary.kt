package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.DefaultLibraryFileLocator
import com.github.am4dr.rokusho.core.FilteredPathLibrary
import com.github.am4dr.rokusho.core.LibraryItemMetaData
import com.github.am4dr.rokusho.core.Tag
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes


private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
private fun isSupportedImageFile(path: Path) =
        Files.isRegularFile(path)
                && imageFileNameMatcher.matches(path.fileName.toString())

class ImagePathLibrary(path: Path) : FilteredPathLibrary {
    override var fileWalkRoot: Path = path
    override val savefileRoot: Path
    override val matcher: (Path?, BasicFileAttributes?) -> Boolean =
            { path, attr -> path?.let(::isSupportedImageFile) ?: false }

    private val _itemMetaData: MutableList<LibraryItemMetaData> = mutableListOf()
    val baseTags: Map<String, ObservableTag> = mutableMapOf()
    val images: Map<String, ImageItem> = mutableMapOf()
    init {
        val library = DefaultLibraryFileLocator().locate(path)
        savefileRoot = library.savefileRoot
        _itemMetaData.addAll(library.getItemMetaData())
        (baseTags as MutableMap)
                .putAll(library.getTags().map(::SimpleObservableTag).associateBy(Tag::id))
        (images as MutableMap)
                .putAll(getItems().map { it.toImageItem() }.associateBy(ImageItem::id))
    }
    override fun getTags(): List<Tag> = baseTags.values.toList()
    override fun getItemMetaData(): List<LibraryItemMetaData> = _itemMetaData
    override fun updateTag(tag: Tag) { baseTags[tag.id]?.putAllData(tag.data) }
    override fun removeTag(id: String) { (baseTags as MutableMap).remove(id) }
    override fun updateItemMetaData(itemMetaData: LibraryItemMetaData) {
        _itemMetaData.run {
            set(indexOfFirst { it.id == itemMetaData.id }, itemMetaData)
        }
        val new = images[itemMetaData.id]?.let { SimpleImage(it.id, it.url, itemMetaData.tags) }
        if (new != null) { (images as MutableMap)[itemMetaData.id] = new }
    }
    override fun removeItem(id: String) {
        _itemMetaData.removeAll { it.id == id }
        (images as MutableMap).remove(id)
    }

    private fun Pair<Path, LibraryItemMetaData>.toImageItem(): ImageItem {
        val (path, meta) = this
        return SimpleImage(path.toIdFormat(), path.toUri().toURL(), meta.tags) // TODO DerivedTag
    }
}
interface ImageItem {
    val id: String
    val url: URL
    val tags: List<Tag>
}
data class SimpleImage(
        override val id: String,
        override val url: URL,
        override val tags: List<Tag>) : ImageItem
