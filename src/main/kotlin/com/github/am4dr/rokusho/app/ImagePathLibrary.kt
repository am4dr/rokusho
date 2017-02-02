package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.DefaultLibraryFileLocator
import com.github.am4dr.rokusho.core.LibraryItemMetaData
import com.github.am4dr.rokusho.core.SimpleLibraryItemMetaData
import com.github.am4dr.rokusho.core.Tag
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
            get(id)?.let { set(id, SimpleImage(id, it.url, tags)) }
        }
    }
    private fun toImageItem(pair: Pair<Path, LibraryItemMetaData>): ImageItem {
        val (path, meta) = pair
        return SimpleImage(library.toIdFormat(path), path.toUri().toURL(), meta.tags) // TODO DerivedTag
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
