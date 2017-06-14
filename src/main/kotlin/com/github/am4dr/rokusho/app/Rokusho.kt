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

class Rokusho {
    companion object {
        val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
        fun isSupportedImageFile(path: Path) =
                Files.isRegularFile(path) && imageFileNameMatcher.matches(path.fileName.toString())
    }
    private val libraryLoader = LibraryLoader()

    private val _libraries = observableArrayList<Library<ImageUrl>>()
    val libraries: ReadOnlyListProperty<Library<ImageUrl>> = ReadOnlyListWrapper(_libraries).readOnlyProperty

    private val _itemSets = observableArrayList<ItemSet<ImageUrl>>()
    val itemSets: ReadOnlyListProperty<ItemSet<ImageUrl>> = ReadOnlyListWrapper(_itemSets).readOnlyProperty

    /**
     * 指定のディレクトリを含むライブラリを作成し追加する。
     *
     * 指定のディレクトリを含むライブラリが存在しない場合は、そのディレクトリをルートとしてライブラリを作成し追加する。
     */
    fun addDirectory(directory: Path, depth: Int) {
        libraryLoader.loadDirectory(directory)
        val itemSet = libraryLoader.getItemSet(directory, depth)
        _libraries.add(itemSet.library)
        _itemSets.add(itemSet)
    }

    fun updateItemTags(item: Item<ImageUrl>, itemTags: List<ItemTag>) {
        val itemSet = _itemSets.find { it.items.contains(item) } ?: return
        itemSet.library.updateItemTags(item.key, itemTags)
    }
}