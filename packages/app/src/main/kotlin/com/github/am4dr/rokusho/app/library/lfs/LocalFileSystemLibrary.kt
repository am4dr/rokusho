package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.savedata.ItemMetaData
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.Tag
import com.github.am4dr.rokusho.core.library.helper.LibrarySupport
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import java.nio.file.Path
import java.nio.file.Paths

class LocalFileSystemLibrary(private val root: Path,
                             private val saveDataStore: SaveDataStore<SaveData>,
                             private val librarySupport: LibrarySupport<ImageUrl>) : Library<ImageUrl> {

    override val tags: ReadOnlyMapProperty<String, Tag> = ReadOnlyMapWrapper(librarySupport.tags).readOnlyProperty
    override val records: ReadOnlyListProperty<Record<ImageUrl>> = ReadOnlyListWrapper(toObservableList(librarySupport.records)).readOnlyProperty

    override fun updateItemTags(key: ImageUrl, tags: Iterable<ItemTag>) {
        librarySupport.records[key] = Record(key, tags.toList())
    }
    fun save() {
        saveDataStore.save(createSaveData())
    }

    private fun createSaveData(): SaveData {
        val metaData = librarySupport.records.keys.map {
            val path = root.relativize(Paths.get(it.url.toURI()))
            path to ItemMetaData(librarySupport.records[it]?.itemTags ?: mutableListOf())
        }.toMap()
        return SaveData(SaveData.Version.VERSION_1, tags, metaData)
    }
}