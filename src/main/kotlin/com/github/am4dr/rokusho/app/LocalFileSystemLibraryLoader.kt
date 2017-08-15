package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveFileLoader
import com.github.am4dr.rokusho.core.library.Library
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class LocalFileSystemLibraryLoader {
    private val savefileLoader = YamlSaveFileLoader()
    private val _loadedLibraries = ReadOnlyListWrapper(observableArrayList<LocalFileSystemLibrary>())
    val loadedLibraries: ReadOnlyListProperty<LocalFileSystemLibrary> = _loadedLibraries.readOnlyProperty

    private fun addLibrary(library: LocalFileSystemLibrary) { _loadedLibraries.add(library) }

    fun getOrLoadLibrary(directory: Path): LocalFileSystemLibrary {
        val savefilePath = getSavefilePathFor(directory)
        findLibraryBySavefilePath(savefilePath)?.let { return it }

        val items = getAllItems(directory)
        val library = Library({ items.asSequence() }, { it }).apply {
            if (Files.exists(savefilePath)) {
                val (initTags, initItemTags) = savefileLoader.load(savefilePath).toRegistries()
                tags.putAll(initTags)
                itemTags.putAll(initItemTags)
            }
        }
        return LocalFileSystemLibrary(savefilePath, library).apply(this::addLibrary)
    }
    private fun getAllItems(root: Path): List<ImageUrl> {
        val rootSavefilePath = getSavefilePathFor(root)
        val list = mutableListOf<ImageUrl>()
        Files.walkFileTree(root, object : FileVisitor<Path> {
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult =
                    if (Files.isSameFile(getSavefilePathFor(dir), rootSavefilePath)) FileVisitResult.CONTINUE else FileVisitResult.SKIP_SUBTREE
            override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                if (Rokusho.isSupportedImageFile(file)) list.add(ImageUrl(file.toUri().toURL()))
                return FileVisitResult.CONTINUE
            }
            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
            override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
        })
        return list
    }

    private fun getSavefilePathFor(directory: Path): Path {
        val loaded: LocalFileSystemLibrary? = findLibrariesContains(directory).maxBy { it.savefilePath.nameCount }
        val savefilePath = savefileLoader.locateSaveFilePath(directory)?.normalize()

        return when (compareValues(loaded?.savefilePath?.nameCount, savefilePath?.nameCount)) {
            0  -> loaded?.savefilePath ?: savefileLoader.getDefaultSavefilePath(directory)
            1  -> loaded!!.savefilePath
            else -> savefilePath!!
        }
    }

    private fun findLibrariesContains(directory: Path): List<LocalFileSystemLibrary> =
            _loadedLibraries.filter { directory.normalize().startsWith(it.savefilePath.parent) }

    private fun findLibraryBySavefilePath(savefilePath: Path): LocalFileSystemLibrary? =
            _loadedLibraries.find { savefilePath == it.savefilePath }
}