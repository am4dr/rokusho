package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.*
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.RecordListWatcher
import com.github.am4dr.rokusho.core.library.Tag
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LocalFileSystemLibrary(savefilePath: Path,
                             private val library: Library<ImageUrl, ImageUrl>) : RokushoLibrary<ImageUrl> {
    constructor(savefilePath: Path, items: Iterable<ImageUrl>) : this (savefilePath, Library({ items.asSequence() }, { it }))

    override val tags: ReadOnlyMapProperty<String, Tag> = ReadOnlyMapWrapper(library.tags).readOnlyProperty
    override val itemTags: ReadOnlyMapProperty<ImageUrl, List<ItemTag>> = ReadOnlyMapWrapper(library.itemTags).readOnlyProperty
    val items: List<ImageUrl> get() = library.items

    val savefilePath: Path = savefilePath.toAbsolutePath()
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<RecordListWatcher<ImageUrl>.Records>())
    override val recordLists: ReadOnlyListProperty<RecordListWatcher<ImageUrl>.Records> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): RecordListWatcher<ImageUrl>.Records = library.getRecordList(library.records).also { _recordLists.add(it) }

    fun save(serializer: SaveDataSerializer) {
        Files.write(savefilePath, serializer.serialize(createSaveData()).split("\n"))
    }

    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        tags.forEach { it.tag.let { tag -> if (tag !== library.tags[tag.id]) { library.tags[tag.id] = tag } } }
        library.itemTags[key] = tags.toList()
    }

    private fun createSaveData(): SaveData {
        val metaData = itemTags.keys.map {
            val path = savefilePath.parent.relativize(Paths.get(it.url.toURI()))
            path to ImageMetaData(itemTags.getOrDefault(it, mutableListOf()))
        }.toMap()
        return SaveData(SaveData.Version.VERSION_1, tags, metaData)
    }
}
