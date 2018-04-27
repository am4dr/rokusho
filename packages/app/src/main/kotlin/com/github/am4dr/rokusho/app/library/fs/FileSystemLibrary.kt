package com.github.am4dr.rokusho.app.library.fs

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.app.savedata.Item
import com.github.am4dr.rokusho.app.savedata.ItemMetaData
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.Tag
import com.github.am4dr.rokusho.core.library.helper.LibrarySupport
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import java.nio.file.Path

/**
 * a implementation of [Library] based on [java.nio.file.FileSystem]
 */
class FileSystemLibrary(val root: Path,
                        private val store: SaveDataStore<SaveData>,
                        private val librarySupport: LibrarySupport<Path> = LibrarySupport()) : RokushoLibrary<Path> {

    override val name: String = root.toString()
    override var autoSaveEnabled: Boolean = false

    override val tags: ReadOnlyMapProperty<String, Tag> = ReadOnlyMapWrapper(librarySupport.tags).readOnlyProperty
    override val records: ReadOnlyListProperty<Record<Path>> = ReadOnlyListWrapper(toObservableList(librarySupport.records)).readOnlyProperty
    override fun updateItemTags(key: Path, tags: Iterable<ItemTag>) {
        librarySupport.records[key] = Record(key, tags.toList())

        if (autoSaveEnabled) save()
    }

    override fun save() = store.save(toSaveData())
}

internal fun FileSystemLibrary.toSaveData(): SaveData {
    val metaData = records.map {
        val id = root.relativize(it.key).joinToString("/")
        Item(id, ItemMetaData(it.itemTags))
    }
    return SaveData(SaveData.Version.VERSION_1, tags.values.distinct(), metaData)
}

internal fun SaveData.toFileSystemLibrary(root: Path, paths: Iterable<Path>): LibrarySupport<Path> {
    val recordMap = items.map {
        root.resolve(it.id) to it.data.tags
    }.toMap()
    val records = paths.map {
        Record(it, recordMap[it] ?: listOf())
    }.toList()
    return LibrarySupport(records, tags)
}
