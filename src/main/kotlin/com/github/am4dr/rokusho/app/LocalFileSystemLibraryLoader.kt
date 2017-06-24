package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultMetaDataRegistry
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibraryLoader {
    private val savefileLoader = SaveFileLoader()
    private val _loadedLibraries = ReadOnlyListWrapper(observableArrayList<LocalFileSystemLibrary>())
    val loadedLibraries: ReadOnlyListProperty<LocalFileSystemLibrary> = _loadedLibraries.readOnlyProperty

    fun loadDirectory(directory: Path) {
        val savefilePath = getSavefilePathFor(directory)
        if (findLibraryBySavefilePath(savefilePath) == null) {
            if (Files.exists(savefilePath)) {
                createLibrary(savefilePath, savefileLoader.load(savefilePath))
            }
            else {
                createLibrary(savefilePath)
            }
        }
    }

    fun getOrCreateLibrary(directory: Path): LocalFileSystemLibrary =
            findLibraryByDirectory(directory) ?: createLibrary(savefileLoader.getDefaultSavefilePath(directory))

    private fun createLibrary(savefilePath: Path, registry: MetaDataRegistry<ImageUrl> = DefaultMetaDataRegistry()): LocalFileSystemLibrary =
            LocalFileSystemLibrary(savefilePath, registry).also { _loadedLibraries.add(it) }

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

    private fun findLibraryByDirectory(directory: Path): LocalFileSystemLibrary? =
            findLibraryBySavefilePath(getSavefilePathFor(directory))

    private fun findLibraryBySavefilePath(savefilePath: Path): LocalFileSystemLibrary? =
            _loadedLibraries.find { savefilePath == it.savefilePath }
}