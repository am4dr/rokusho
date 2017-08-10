package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveDataSerializer
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.ObservableList
import java.nio.file.Files
import java.nio.file.Path

class Rokusho {
    companion object {
        val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val libraryLoader = LocalFileSystemLibraryLoader()

    val libraries: ReadOnlyListProperty<out RokushoLibrary<ImageUrl>> = libraryLoader.loadedLibraries

    val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>>
    init {
        val listOfRecordLists: ObservableList<ObservableList<ObservableRecordList<ImageUrl>>> =
                TransformedList(libraryLoader.loadedLibraries, RokushoLibrary<ImageUrl>::recordLists)
        recordLists = ReadOnlyListWrapper(ConcatenatedList(listOfRecordLists)).readOnlyProperty
    }

    fun addDirectory(directory: Path): ObservableRecordList<ImageUrl>? = libraryLoader.getOrLoadLibrary(directory).run { createRecordList(items) }

    fun updateItemTags(record: Record<ImageUrl>, itemTags: List<ItemTag>) = getLibrary(record)?.updateItemTags(record.key, itemTags)

    fun save() {
        val serializer = YamlSaveDataSerializer()
        libraryLoader.loadedLibraries.forEach { it.save(serializer) }
    }

    fun getLibrary(record: Record<ImageUrl>): RokushoLibrary<ImageUrl>? =
            recordLists.find { it.records.contains(record) }?.let { list ->
                libraries.find { it.recordLists.contains(list) }
            }
}