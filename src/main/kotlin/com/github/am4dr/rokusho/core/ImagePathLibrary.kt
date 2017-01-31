package com.github.am4dr.rokusho.core

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes


private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
private fun isSupportedImageFile(path: Path) =
        Files.isRegularFile(path)
                && imageFileNameMatcher.matches(path.fileName.toString())

class ImagePathLibrary(private val library: PathLibrary) : Library by library, FilteredPathLibrary {
    constructor(path: Path) : this(DefaultLibraryFileLocator().locate(path))
    
    override val root: Path get() = library.root
    override val matcher: (Path?, BasicFileAttributes?) -> Boolean =
            { path, attr -> path?.let(::isSupportedImageFile) ?: false }
    val images: List<ImageItem> get() = getItems().map {
        val (path, meta) = it
        SimpleImage(path.toIdFormat(), path.toUri().toURL(), meta.tags)
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
