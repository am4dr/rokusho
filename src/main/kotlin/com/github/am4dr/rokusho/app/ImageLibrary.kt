package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.*
import com.github.am4dr.rokusho.gui.TagNodeFactory
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableList
import javafx.collections.FXCollections.observableMap
import javafx.collections.ObservableList
import java.net.URL
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors


typealias FileWalker = (Path) -> List<Path>
class ImageLibrary(path: Path, val fileWalker: FileWalker = ImageLibrary.defaultFileWalker) {
    companion object {
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
        val matcher: (Path?, BasicFileAttributes?) -> Boolean =
                { path, _ -> path?.let(ImageLibrary.Companion::isSupportedImageFile) ?: false }
        val defaultFileWalker: FileWalker = { root ->
            Files.find(root, Int.MAX_VALUE, matcher, arrayOf(FileVisitOption.FOLLOW_LINKS))
                    .collect(Collectors.toList<Path>())
        }
    }
    private val library = DefaultLibraryFileLocator().locate(path).let {
        if (Files.exists(it)) YamlSaveFileParser().parse(it) else SimpleParsedLibrary(it)
    }
    val savefilePath: Path = library.savefilePath

    val fileWalkRoot: Path = path
    private val paths = fileWalker(fileWalkRoot)

    private val baseTags = ReadOnlyMapWrapper(library.getTags().map(::SimpleObservableTag).associateBy(Tag::id).toMutableMap().let(::observableMap))
    val baseTagsProperty: ReadOnlyMapProperty<String, out ObservableTag> = baseTags.readOnlyProperty
    private val images = ReadOnlyMapWrapper(toImageItem(paths).associateBy(ImageItem::id).toMutableMap().let(::observableMap))
    val imagesProperty: ReadOnlyMapProperty<String, ImageItem> = images.readOnlyProperty

    val tagNodeFactory: TagNodeFactory = TagNodeFactory(baseTagsProperty)
    val tagStringParser: TagStringParser = DefaultTagStringParser(baseTagsProperty)

    fun toImageItem(paths: Iterable<Path>): List<ImageItem> = library.toLibraryItems(paths).map(this::toImageItem)
    private fun toImageItem(pair: Pair<Path, LibraryItemMetaData>): ImageItem {
        val (path, meta) = pair
        return SimpleImageItem(this, library.toIdFormat(path), path.toUri().toURL(), meta.tags.map(this::toDerivedTag))
    }
    private fun toDerivedTag(tag: Tag): Tag {
        return baseTagsProperty[tag.id]?.let { DerivedObservableTag(it, tag.data) } ?: tag
    }
}

interface ImageItem : ObservableValue<ImageItem> {
    val library: ImageLibrary
    val id: String
    val url: URL
    val tags: ObservableList<Tag>
}
class SimpleImageItem(
        override val library: ImageLibrary,
        override val id: String,
        override val url: URL,
        tags: List<Tag>) : ImageItem, ObjectBinding<ImageItem>() {
    override val tags: ObservableList<Tag> = observableList(tags.toMutableList())
    init { super.bind(this.tags) }
    override fun computeValue(): ImageItem = this
    override fun toString(): String = "SimpleImageItem(id: $id, url: $url, tags: $tags)"
}
