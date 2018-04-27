package com.github.am4dr.rokusho.app.library.fs

import com.github.am4dr.rokusho.app.datastore.DataStore
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.app.savedata.Item
import com.github.am4dr.rokusho.app.savedata.ItemMetaData
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.core.library.helper.LibrarySupport
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty
import java.nio.file.Path

/**
 * a implementation of [Library] based on [java.nio.file.FileSystem]
 */
class FileSystemLibrary(val root: Path,
                        private val store: DataStore<SaveData>,
                        private val library: Library<Path> = SimpleLibrary()) : RokushoLibrary<Path> {

    override val tags: ReadOnlyMapProperty<String, Tag> get() = library.tags
    override val records: ReadOnlyListProperty<Record<Path>> get() = library.records
    override val name: String = root.toString()
    override var autoSaveEnabled: Boolean = false

    override fun updateItemTags(key: Path, tags: Iterable<ItemTag>) {
        library.updateItemTags(key, tags)

        if (autoSaveEnabled) save()
    }

    override fun save() = store.save(toSaveData())

    internal fun toSaveData(): SaveData {
        val metaData = records.map {
            val id = root.relativize(it.key).joinToString("/")
            Item(id, ItemMetaData(it.itemTags))
        }
        return SaveData(tags.values.distinct(), metaData)
    }
}


internal fun SaveData.toFileSystemLibrary(root: Path, paths: Iterable<Path>): Library<Path> {
    val recordMap = items.map {
        root.resolve(it.id) to it.data.tags
    }.toMap()
    val records = paths.map {
        Record(it, recordMap[it] ?: listOf())
    }.toList()
    return SimpleLibrary(LibrarySupport(records, tags))
}
