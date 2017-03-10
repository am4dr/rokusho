package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.ImageLibrary.Companion.isSupportedImageFile
import com.github.am4dr.rokusho.core.DefaultLibraryFileLocator
import com.github.am4dr.rokusho.util.ConcatenatedList
import com.github.am4dr.rokusho.util.createEmptyListProperty
import com.github.am4dr.rokusho.util.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import java.nio.file.Files
import java.nio.file.Path

class ImageLibraryCollection {
    private val libraries: ReadOnlyListWrapper<ImageLibrary> = ReadOnlyListWrapper(createEmptyListProperty<ImageLibrary>())
    val librariesProperty: ReadOnlyListProperty<ImageLibrary> = libraries.readOnlyProperty
    private val managedDirectories: MutableSet<Path> = mutableSetOf()
    private val items = ConcatenatedList<ImageItem>()
    val itemsProperty: ReadOnlyListProperty<ImageItem> = ReadOnlyListWrapper(items).readOnlyProperty

    fun addLibrary(library: ImageLibrary) {
        libraries.add(library)
        items.concat(toObservableList(library.imagesProperty))
    }
    fun addDirectory(path: Path, depth: Int = Int.MAX_VALUE) {
        if (!Files.isDirectory(path)) return
        if (managedDirectories.contains(path)) return
        val lib = findLibrary(path) ?: (ImageLibrary(path).also(this::addLibrary))
        val paths = mutableListOf<Path>()
        val dirs = mutableListOf<Path>()
        Files.list(path).forEach {
            if (Files.isDirectory(it)) dirs.add(it)
            else if (isSupportedImageFile(it)) paths.add(it)
        }
        managedDirectories.add(path)
        lib.addItemsByPaths(paths)
        if (depth > 1) { dirs.forEach { addDirectory(it, depth - 1) } }
    }
    fun findLibrary(path: Path): ImageLibrary? {
        val savefilePath = DefaultLibraryFileLocator().locate(path)
        return libraries.filter { savefilePath.startsWith(it.savefilePath.parent) }.maxBy { it.savefilePath.toString().length }
    }
}