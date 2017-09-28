package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.ImageMetaData
import com.github.am4dr.rokusho.app.savefile.SaveData
import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LocalFileSystemLibrary(savefilePath: Path, private val library: Library<ImageUrl>) : RokushoLibrary<ImageUrl> {

    val savefilePath: Path = savefilePath.toAbsolutePath()
    val items: List<ImageUrl> get() = library.records.keys.toList()

    override val tags: ReadOnlyMapProperty<String, Tag> = ReadOnlyMapWrapper(library.tags).readOnlyProperty
    override val records: ReadOnlyListProperty<Record<ImageUrl>> = ReadOnlyListWrapper(toObservableList(library.records)).readOnlyProperty

    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableList<Record<ImageUrl>>>())
    override val recordLists: ReadOnlyListProperty<ObservableList<Record<ImageUrl>>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableList<Record<ImageUrl>> {
        return ChangeAwareRecords(FXCollections.observableArrayList(list.mapNotNull(library.records::get)), library).also { _recordLists.add(it) }
    }

    fun save(serializer: SaveDataSerializer) {
        Files.write(savefilePath, serializer.serialize(createSaveData()).split("\n"))
    }

    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        tags.forEach { it.tag.let { tag -> if (tag !== library.tags[tag.id]) { library.tags[tag.id] = tag } } }
        library.records[key] = Record(key, tags.toList())
    }

    private fun createSaveData(): SaveData {
        val metaData = library.records.keys.map {
            val path = savefilePath.parent.relativize(Paths.get(it.url.toURI()))
            path to ImageMetaData(library.records[it]?.itemTags ?: mutableListOf())
        }.toMap()
        return SaveData(SaveData.Version.VERSION_1, tags, metaData)
    }
}
