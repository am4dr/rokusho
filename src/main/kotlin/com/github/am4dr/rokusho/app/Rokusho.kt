package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.library.Item
import com.github.am4dr.rokusho.core.library.ItemSet
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Library
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

    private val _libraries = ReadOnlyListWrapper(observableArrayList<Library<ImageUrl>>())
    val libraries: ReadOnlyListProperty<Library<ImageUrl>> = _libraries.readOnlyProperty

    private val _itemSets = ReadOnlyListWrapper(observableArrayList<ItemSet<ImageUrl>>())
    val itemSets: ReadOnlyListProperty<ItemSet<ImageUrl>> = _itemSets.readOnlyProperty

    fun addDirectory(directory: Path, depth: Int) {
        libraryLoader.loadDirectory(directory)
        val itemSet = getItemSet(directory, depth)
        _libraries.add(itemSet.library)
        _itemSets.add(itemSet)
    }

    private fun getItemSet(directory: Path, depth: Int): ItemSet<ImageUrl> {
        return libraryLoader.getOrCreateLibrary(directory).getItemSet(collectImageUrls(directory, depth))
    }

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())

    fun updateItemTags(item: Item<ImageUrl>, itemTags: List<ItemTag>) {
        val itemSet = _itemSets.find { it.items.contains(item) } ?: return
        itemSet.library.updateItemTags(item.key, itemTags)
    }
}