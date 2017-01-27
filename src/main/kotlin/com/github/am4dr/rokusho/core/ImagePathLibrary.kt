package com.github.am4dr.rokusho.core

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes


private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
private fun isSupportedImageFile(path: Path) =
        Files.isRegularFile(path)
                && imageFileNameMatcher.matches(path.fileName.toString())

class ImagePathLibrary(private val library: PathLibrary) : PathLibrary by library, FilteredPathLibrary {
    constructor(path: Path) : this(DefaultLibraryFileLocator().locate(path))
    override val filter: (Path?, BasicFileAttributes?) -> Boolean =
            { path, attr -> path?.let(::isSupportedImageFile) ?: false }
    val images: List<ImageItem>
    init {
        images = getItems().map {
            val (path, meta) = it
            SimpleImage(path.toIdFormat(), path.toUri().toURL(), meta.tags, this)
        }
    }
}
interface ImageItem {
    val id: String
    val url: URL
    val tags: List<Tag>
    val library: Library
}
data class SimpleImage(
        override val id: String,
        override val url: URL,
        override val tags: List<Tag>,
        override val library: Library) : ImageItem
