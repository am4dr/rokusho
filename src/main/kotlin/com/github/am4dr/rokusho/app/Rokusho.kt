package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class Rokusho {
    companion object {
        val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val libraryLoader = LibraryLoader()

    private val _metaDataRegistries = ReadOnlyListWrapper(observableArrayList<MetaDataRegistry<ImageUrl>>())
    val metaDataRegistries: ReadOnlyListProperty<MetaDataRegistry<ImageUrl>> = _metaDataRegistries.readOnlyProperty

    private val _recordLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
    val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>> = _recordLists.readOnlyProperty

    fun addDirectory(directory: Path, depth: Int) {
        libraryLoader.loadDirectory(directory)
        val itemSet = getRecordList(directory, depth)
        _metaDataRegistries.add(itemSet.metaDataRegistry)
        _recordLists.add(itemSet)
    }

    private fun getRecordList(directory: Path, depth: Int): ObservableRecordList<ImageUrl> =
            libraryLoader.getOrCreateLibrary(directory).getRecordList(collectImageUrls(directory, depth))

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())

    fun updateItemTags(record: Record<ImageUrl>, itemTags: List<ItemTag>) =
            _recordLists.find { it.records.contains(record) }
                    ?.apply { metaDataRegistry.updateItemTags(record.key, itemTags) }
}