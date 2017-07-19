package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveDataSerializer
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.ObservableList
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BiPredicate
import java.util.stream.Collectors

class Rokusho {
    companion object {
        val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val libraryLoader = LocalFileSystemLibraryLoader()

    val libraries: ReadOnlyListProperty<out Library<ImageUrl>> = libraryLoader.loadedLibraries

    val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>>
    init {
        val listOfRecordLists: ObservableList<ObservableList<ObservableRecordList<ImageUrl>>> =
                TransformedList(libraryLoader.loadedLibraries, Library<ImageUrl>::recordLists)
        recordLists = ReadOnlyListWrapper(ConcatenatedList(listOfRecordLists)).readOnlyProperty
    }

    private fun addDirectory(directory: Path): ObservableRecordList<ImageUrl>? =
            libraryLoader.getOrLoadLibrary(directory).run {
                collectImageUrls(directory, 1).takeIf(List<*>::isNotEmpty)?.let { createRecordList(it) }
            }

    fun addDirectory(directory: Path, depth: Int): List<ObservableRecordList<ImageUrl>> =
            Files.find(directory, depth, BiPredicate { _, a ->  a.isDirectory })
                    .map { addDirectory(it) }
                    .filter { it != null }
                    .collect(Collectors.toList<ObservableRecordList<ImageUrl>>())

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())

    fun updateItemTags(record: Record<ImageUrl>, itemTags: List<ItemTag>) =
            recordLists.find { it.records.contains(record) }?.let { list ->
                libraries.find { it.metaDataRegistry === list.metaDataRegistry }?.updateItemTags(record.key, itemTags)
            }

    fun save() {
        val serializer = YamlSaveDataSerializer()
        libraryLoader.loadedLibraries.forEach { it.save(serializer) }
    }
}