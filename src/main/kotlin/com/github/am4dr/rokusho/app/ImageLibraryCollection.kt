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

    private val roots get() = libraries.map(ImageLibrary::fileWalkRoot)

    private val fileWalker: FileWalker = this::listImages
    private fun listImages(root: Path): List<Path> {
        val queue = mutableListOf(root)
        val paths = mutableListOf<Path>()
        while (queue.isNotEmpty()) {
            Files.list(queue.first()).forEach { path ->
                if (Files.isDirectory(path)) {
                    if (roots.none(path::startsWith)) { queue.add(path) }
                }
                else if (isSupportedImageFile(path)) { paths.add(path) }
            }
            queue.remove(queue.first())
        }
        return paths
    }
    fun addDirectory(path: Path, depth: Int = Int.MAX_VALUE) {
        if (!Files.isDirectory(path)) return
        if (managedDirectories.contains(path)) return
        val lib = findLibraryContains(path) ?: (ImageLibrary(path, fileWalker).also { libraries.add(it) })
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
    fun findLibraryContains(path: Path): ImageLibrary? {
        val savefilePath = DefaultLibraryFileLocator().locate(path)
        return libraries.find { it.savefilePath == savefilePath }
    }
}