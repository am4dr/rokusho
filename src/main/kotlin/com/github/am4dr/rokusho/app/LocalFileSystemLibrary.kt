package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import com.github.am4dr.rokusho.app.savefile.SaveFile
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.toObservableMap
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibrary(savefilePath: Path,
                             override val tagRegistry: TagRegistry = DefaultTagRegistry(),
                             itemTags: ItemTagDB<ImageUrl>) : Library<ImageUrl> {

    override val metaDataRegistry: MetaDataRegistry<ImageUrl> = DefaultMetaDataRegistry(toObservableMap(tagRegistry.tags, Tag::id), itemTags)
    val savefilePath: Path = savefilePath.toAbsolutePath()
    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    override val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableRecordList<ImageUrl> =
            metaDataRegistry.getRecordList(list).also { _recordLists.add(it) }

    fun save(serializer: SaveDataSerializer) {
        val savefile = SaveFile.fromMetaDataRegistry(savefilePath, metaDataRegistry)
        val serialized = serializer.serialize(savefile.data)
        Files.write(savefilePath, serialized.split("\n"))
    }
}
