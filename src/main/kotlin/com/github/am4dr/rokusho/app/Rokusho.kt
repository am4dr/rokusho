package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.util.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ListChangeListener
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

    private val configuredLibraries: ObservableList<Library<*>>

    val metaDataRegistries: ReadOnlyListProperty<MetaDataRegistry<ImageUrl>> =
            ReadOnlyListWrapper(TransformedList(libraryLoader.loadedLibraries, Library<ImageUrl>::metaDataRegistry)).readOnlyProperty

    val recordLists: ReadOnlyListProperty<ObservableRecordList<ImageUrl>>

    init {
        val allLists = ReadOnlyListWrapper(observableArrayList<ObservableRecordList<ImageUrl>>())
        configuredLibraries = TransformedList(libraryLoader.loadedLibraries) { libs ->
            libs.recordLists.addListener(ListChangeListener { c ->
                while (c.next()) {
                    if (c.wasRemoved()) {
                        allLists.removeAll(c.removed)
                    }
                    if (c.wasAdded()) {
                        allLists.addAll(c.addedSubList)
                    }
                }
            })
            return@TransformedList libs
        }
        recordLists = allLists.readOnlyProperty
    }

    fun addDirectory(directory: Path, depth: Int) {
        libraryLoader.getOrLoadLibrary(directory).createRecordList(collectImageUrls(directory, depth))
    }

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())

    fun updateItemTags(record: Record<ImageUrl>, itemTags: List<ItemTag>) =
            recordLists.find { it.records.contains(record) }
                    ?.apply { metaDataRegistry.updateItemTags(record.key, itemTags) }
}