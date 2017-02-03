package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.DefaultLibraryFileLocator
import com.github.am4dr.rokusho.core.LibraryItemMetaData
import com.github.am4dr.rokusho.core.SimpleLibraryItemMetaData
import com.github.am4dr.rokusho.core.Tag
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableList
import javafx.collections.ObservableList
import java.net.URL
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors


private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
private fun isSupportedImageFile(path: Path) =
        Files.isRegularFile(path)
                && imageFileNameMatcher.matches(path.fileName.toString())

class ImagePathLibrary(path: Path) {
    private val library = DefaultLibraryFileLocator().locate(path)

    private val fileWalkRoot: Path = path
    private val matcher: (Path?, BasicFileAttributes?) -> Boolean =
            { path, attr -> path?.let(::isSupportedImageFile) ?: false }
    private val paths =
            Files.find(fileWalkRoot, Int.MAX_VALUE, matcher, arrayOf(FileVisitOption.FOLLOW_LINKS))
                    .collect(Collectors.toList<Path>())

    val baseTags: Map<String, ObservableTag>
    val images: Map<String, ImageItem>
    init {
        baseTags = mutableMapOf()
        baseTags.putAll(library.getTags().map(::SimpleObservableTag).associateBy(Tag::id))
        images = mutableMapOf()
        images.putAll(
                library.toLibraryItems(paths).map { toImageItem(it) }.associateBy(ImageItem::id))
    }
    fun update(id: String, tags: List<Tag>) {
        library.updateItemMetaData(SimpleLibraryItemMetaData(id, tags))
        (images as MutableMap).run {
            get(id)?.let { set(id, SimpleImageItem(id, it.url, tags)) }
        }
    }
    fun update(tag: Tag) {
        library.updateTag(tag)
        (baseTags as MutableMap).run {
            get(tag.id)?.let { it.putAllData(tag.data) }
        }
    }
    private fun toImageItem(pair: Pair<Path, LibraryItemMetaData>): ImageItem {
        val (path, meta) = pair
        return SimpleImageItem(library.toIdFormat(path),
                path.toUri().toURL(), meta.tags.map { toDerivedTag(it) })
    }
    private fun toDerivedTag(tag: Tag): Tag {
        return baseTags[tag.id]?.let { DerivedObservableTag(it, tag.data) } ?: tag
    }
}

interface ImageItem : ObservableValue<ImageItem> {
    val id: String
    val url: URL
    val tags: ObservableList<Tag>
    fun addOrUpdateTag(tag: Tag)
    fun removeTag(id: String)
}
class SimpleImageItem(
        override val id: String,
        override val url: URL,
        tags: List<Tag>) : ImageItem, ObjectBinding<ImageItem>() {
    override val tags: ObservableList<Tag> = observableList(tags.toMutableList())
    override fun addOrUpdateTag(tag: Tag) {
        tags.run {
            val i = indexOfFirst { it.id == tag.id }
            if (i >= 0) { set(i, tag) }
            else { add(tag) }
        }
    }
    override fun removeTag(id: String) {
        tags.removeAll { it.id == id }
    }
    init { super.bind(this.tags) }
    override fun computeValue(): ImageItem = this
}
