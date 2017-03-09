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
import org.slf4j.LoggerFactory
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes


class ImageLibrary(path: Path) {
    companion object {
        private val log = LoggerFactory.getLogger(ImageLibrary::class.java)
        private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
        val matcher: (Path?, BasicFileAttributes?) -> Boolean =
                { path, _ -> path?.let(ImageLibrary.Companion::isSupportedImageFile) ?: false }
    }
    private val library = DefaultLibraryFileLocator().locate(path).let {
        if (Files.exists(it)) YamlSaveFileParser().parse(it) else SimpleParsedLibrary(it)
    }
    val savefilePath: Path = library.savefilePath

    private val baseTags = ReadOnlyMapWrapper(library.getTags().map(::SimpleObservableTag).associateByTo(mutableMapOf<String, ObservableTag>(), Tag::id).let(::observableMap))
    val baseTagsProperty: ReadOnlyMapProperty<String, ObservableTag> = baseTags.readOnlyProperty

    val tagNodeFactory: TagNodeFactory = TagNodeFactory(baseTagsProperty)
    val tagStringParser: TagStringParser = BaseUpdatingTagStringParser(baseTagsProperty, { it.also { baseTags[it.id] = it } })

    fun save(items: Iterable<ImageItem>) {
        val metaDataList = items.map { Pair(Paths.get(it.id), ImageMetaData(it.tags.map(DerivedObservableTag.Companion::extractDerivedPart))) }.toMap()
        val savefile = SaveFile("1", baseTagsProperty.get(), metaDataList)
        save(savefile.toTextFormat())
    }
    private fun save(string: String) {
        if (!Files.exists(savefilePath)) {
            log.info("$savefilePath is not exists; create it")
            Files.createFile(savefilePath)
        }
        log.info("save to $savefilePath")
        savefilePath.toFile().writeText(string)
        log.info("wrote to $savefilePath (size=${string.length})")
    }
    fun toImageItem(paths: Iterable<Path>): List<ImageItem> = library.toLibraryItems(paths).map(this::toImageItem)
    private fun toImageItem(path: Path, meta: LibraryItemMetaData): ImageItem =
            SimpleImageItem(this, library.toIdFormat(path), path.toUri().toURL(), meta.tags.map(this::toDerivedTag))
    private fun toImageItem(pair: Pair<Path, LibraryItemMetaData>): ImageItem =
            toImageItem(pair.first, pair.second)
    private fun toDerivedTag(tag: Tag): Tag = baseTagsProperty[tag.id]?.let { DerivedObservableTag(it, tag.data) } ?: tag
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
