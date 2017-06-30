package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.ObservableList
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class Rokusho {
    companion object {
        val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val libraryLoader = LocalFileSystemLibraryLoader()

    val metaDataRegistries: ReadOnlyListProperty<MetaDataRegistry<ImageUrl>> =
            ReadOnlyListWrapper(TransformedList(libraryLoader.loadedLibraries, Library<ImageUrl>::metaDataRegistry)).readOnlyProperty

    val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>>
    init {
        val listOfRecordLists: ObservableList<ObservableList<ObservableRecordList<ImageUrl>>> =
                TransformedList(libraryLoader.loadedLibraries, Library<ImageUrl>::recordLists)
        recordLists = ReadOnlyListWrapper(ConcatenatedList(listOfRecordLists)).readOnlyProperty
    }

    fun addDirectory(directory: Path, depth: Int) =
            libraryLoader.getOrLoadLibrary(directory).createRecordList(collectImageUrls(directory, depth))

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())

    fun updateItemTags(record: Record<ImageUrl>, itemTags: List<ItemTag>) =
            recordLists.find { it.records.contains(record) }
                    ?.apply { metaDataRegistry.updateItemTags(record.key, itemTags) }
}