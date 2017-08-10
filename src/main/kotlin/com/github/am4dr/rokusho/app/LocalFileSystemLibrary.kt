package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import com.github.am4dr.rokusho.app.savefile.SaveFile
import com.github.am4dr.rokusho.core.library.*
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibrary(savefilePath: Path,
                             private val library: Library<ImageUrl, ImageUrl>) : RokushoLibrary<ImageUrl> {
    constructor(savefilePath: Path, items: Iterable<ImageUrl>) : this (savefilePath, Library({ items.asSequence() }, { it }))

    override val tags: ReadOnlyMapProperty<String, Tag> = library.tags
    override val itemTags: ReadOnlyMapProperty<ImageUrl, List<ItemTag>> = library.itemTags
    val items: List<ImageUrl> get() = library.items

    private val recordRepository: RecordRepository<ImageUrl> = DefaultRecordRepository(tags, itemTags)
    val savefilePath: Path = savefilePath.toAbsolutePath()
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    override val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableRecordList<ImageUrl> = recordRepository.getRecordList(list).also { _recordLists.add(it) }

    fun save(serializer: SaveDataSerializer) {
        val savefile = SaveFile.fromRegistries(savefilePath, tags, itemTags)
        val serialized = serializer.serialize(savefile.data)
        Files.write(savefilePath, serialized.split("\n"))
    }

    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        tags.forEach { it.tag.let { tag -> if (tag !== library.tags[tag.id]) { library.tags[tag.id] = tag } } }
        library.itemTags[key] = tags.toList()
    }
}
