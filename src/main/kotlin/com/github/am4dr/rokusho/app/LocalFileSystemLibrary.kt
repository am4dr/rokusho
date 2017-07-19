package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import com.github.am4dr.rokusho.app.savefile.SaveFile
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.toObservableMap
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibrary(savefilePath: Path,
                             override val tagRegistry: TagRegistry = DefaultTagRegistry(),
                             override val itemTagDB: ItemTagDB<ImageUrl>) : Library<ImageUrl> {

    override val metaDataRegistry: MetaDataRegistry<ImageUrl> = DefaultMetaDataRegistry(SimpleMapProperty(toObservableMap(tagRegistry.tags, Tag::id)), itemTagDB.itemTags)
    val savefilePath: Path = savefilePath.toAbsolutePath()
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    override val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableRecordList<ImageUrl> =
            metaDataRegistry.getRecordList(list).also { _recordLists.add(it) }

    fun save(serializer: SaveDataSerializer) {
        val savefile = SaveFile.fromRegistries(savefilePath, tagRegistry, itemTagDB)
        val serialized = serializer.serialize(savefile.data)
        Files.write(savefilePath, serialized.split("\n"))
    }

    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        tags.map(ItemTag::tag).filter { it !== tagRegistry.get(it.id) }.forEach(tagRegistry::put)
        itemTagDB.set(key, tags.toList())
    }
}
