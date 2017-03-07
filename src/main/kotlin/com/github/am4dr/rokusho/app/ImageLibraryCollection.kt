package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.ImageLibrary.Companion.isSupportedImageFile
import com.github.am4dr.rokusho.core.DefaultLibraryFileLocator
import com.github.am4dr.rokusho.util.createEmptyListProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableList
import java.nio.file.Files
import java.nio.file.Path

class ImageLibraryCollection {
    private val libraries: ReadOnlyListWrapper<ImageLibrary> = ReadOnlyListWrapper(createEmptyListProperty<ImageLibrary>())
    val librariesProperty: ReadOnlyListProperty<ImageLibrary> = libraries.readOnlyProperty
    private val managedDirectories: MutableSet<Path> = mutableSetOf()
    private val items = ReadOnlyListWrapper(observableList(mutableListOf<ImageItem>()))
    val itemsProperty: ReadOnlyListProperty<ImageItem> = items.readOnlyProperty

    fun addDirectory(path: Path, depth: Int = Int.MAX_VALUE) {
        if (!Files.isDirectory(path)) return
        if (managedDirectories.contains(path)) return
        val lib = findLibrary(path) ?: (ImageLibrary(path).also { libraries.add(it) })
        val paths = mutableListOf<Path>()
        val dirs = mutableListOf<Path>()
        Files.list(path).forEach {
            if (Files.isDirectory(it)) dirs.add(it)
            else if (isSupportedImageFile(it)) paths.add(it)
        }
        managedDirectories.add(path)
        items.addAll(lib.toImageItem(paths))
        if (depth > 1) { dirs.forEach { addDirectory(it, depth - 1) } }
    }
    fun findLibrary(path: Path): ImageLibrary? {
        val savefilePath = DefaultLibraryFileLocator().locate(path)
        return libraries.filter { savefilePath.startsWith(it.savefilePath.parent) }.maxBy { it.savefilePath.toString().length }
    }
}