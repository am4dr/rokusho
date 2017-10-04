package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.RokushoLibrary
import com.github.am4dr.rokusho.app.savedata.ItemMetaData
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.Path
import java.nio.file.Paths

class LocalFileSystemLibrary(private val root: Path,
                             private val saveDataStore: SaveDataStore<SaveData>,
                             private val library: Library<ImageUrl>) : RokushoLibrary<ImageUrl> {

    override val tags: ReadOnlyMapProperty<String, Tag> = ReadOnlyMapWrapper(library.tags).readOnlyProperty
    override val records: ReadOnlyListProperty<Record<ImageUrl>> = ReadOnlyListWrapper(toObservableList(library.records)).readOnlyProperty

    private val _recordLists = ReadOnlyListWrapper(FXCollections.observableArrayList<ObservableList<Record<ImageUrl>>>())
    override val recordLists: ReadOnlyListProperty<ObservableList<Record<ImageUrl>>> = _recordLists.readOnlyProperty
    override fun createRecordList(list: Iterable<ImageUrl>): ObservableList<Record<ImageUrl>> {
        return ChangeAwareRecords(FXCollections.observableArrayList(list.mapNotNull(library.records::get)), library).also { _recordLists.add(it) }
    }
    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        library.records[key] = Record(key, tags.toList())
    }
    fun save() {
        saveDataStore.save(createSaveData())
    }

    private fun createSaveData(): SaveData {
        val metaData = library.records.keys.map {
            val path = root.relativize(Paths.get(it.url.toURI()))
            path to ItemMetaData(library.records[it]?.itemTags ?: mutableListOf())
        }.toMap()
        return SaveData(SaveData.Version.VERSION_1, tags, metaData)
    }
}