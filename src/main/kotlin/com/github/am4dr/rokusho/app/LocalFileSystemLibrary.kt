package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import com.github.am4dr.rokusho.app.savefile.SaveFile
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.toObservableMap
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibrary(savefilePath: Path,
                             private val tagRegistry: TagRegistry = DefaultTagRegistry(),
                             private val itemTagRegistry: ItemTagRegistry<ImageUrl>) : Library<ImageUrl> {
    override val tags: ReadOnlySetProperty<Tag> = tagRegistry.tags
    override val itemTags: ReadOnlyMapProperty<ImageUrl, List<ItemTag>> = itemTagRegistry.itemTags

    private val recordRepository: RecordRepository<ImageUrl> = DefaultRecordRepository(SimpleMapProperty(toObservableMap(tagRegistry.tags, Tag::id)), itemTagRegistry.itemTags)
    val savefilePath: Path = savefilePath.toAbsolutePath()
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    override val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableRecordList<ImageUrl> =
            recordRepository.getRecordList(list).also { _recordLists.add(it) }

    fun save(serializer: SaveDataSerializer) {
        val savefile = SaveFile.fromRegistries(savefilePath, tagRegistry, itemTagRegistry)
        val serialized = serializer.serialize(savefile.data)
        Files.write(savefilePath, serialized.split("\n"))
    }

    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        tags.map(ItemTag::tag).filter { it !== tagRegistry.get(it.id) }.forEach(tagRegistry::put)
        itemTagRegistry.set(key, tags.toList())
    }
}
