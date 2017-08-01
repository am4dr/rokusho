package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.yaml.YamlSaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultItemTagRegistry
import com.github.am4dr.rokusho.core.library.DefaultTagRegistry
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections.observableArrayList
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystemLibraryLoader {
    private val savefileLoader = YamlSaveFileLoader()
    private val _loadedLibraries = ReadOnlyListWrapper(observableArrayList<LocalFileSystemLibrary>())
    val loadedLibraries: ReadOnlyListProperty<LocalFileSystemLibrary> = _loadedLibraries.readOnlyProperty

    private fun addLibrary(library: LocalFileSystemLibrary) = _loadedLibraries.add(library)

    fun getOrLoadLibrary(directory: Path): LocalFileSystemLibrary {
        val savefilePath = getSavefilePathFor(directory)
        findLibraryBySavefilePath(savefilePath)?.let { return it }

        val registries = if (Files.exists(savefilePath)) savefileLoader.load(savefilePath).toRegistries() else Pair(DefaultTagRegistry(), DefaultItemTagRegistry<ImageUrl>())
        return LocalFileSystemLibrary(savefilePath, registries.first, registries.second).also { addLibrary(it) }
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