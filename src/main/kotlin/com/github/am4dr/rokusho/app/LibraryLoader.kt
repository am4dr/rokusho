package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemSet
import com.github.am4dr.rokusho.core.library.Library
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

// TODO test
class LibraryLoader {

    private val savefileLoader = SaveFileLoader()
    private val loadedLibraries: MutableList<LoadedLibrary> = mutableListOf()

    private inner class LoadedLibrary (val library: Library<ImageUrl>, savefilePath: Path) {
        val savefilePath: Path = savefilePath.normalize()
        init {
            loadedLibraries.add(this)
        }
    }

    fun loadDirectory(directory: Path) {
        val savefilePath = getSavefilePathFor(directory)
        if (findLibraryBySavefilePath(savefilePath) == null) {
            if (Files.exists(savefilePath)) {
                LoadedLibrary(savefileLoader.load(savefilePath), savefilePath)
            }
            else {
                createLibrary(savefilePath)
            }
        }
    }

    private fun createLibrary(savefilePath: Path): LoadedLibrary =
            LoadedLibrary(DefaultLibrary(), savefilePath.normalize())

    private fun getSavefilePathFor(directory: Path): Path {
        val loaded: LoadedLibrary? = findLibrariesContains(directory).maxBy { it.savefilePath.nameCount }
        val savefilePath = savefileLoader.locateSaveFilePath(directory)?.normalize()

        return when (compareValues(loaded?.savefilePath?.nameCount, savefilePath?.nameCount)) {
            0  -> loaded?.savefilePath ?: getDefaultSavefilePath(directory)
            1  -> loaded!!.savefilePath
            else -> savefilePath!!
        }
    }

    private fun findLibrariesContains(directory: Path): List<LoadedLibrary> =
            loadedLibraries.filter { directory.normalize().startsWith(it.savefilePath.parent) }

    private fun findLibraryByDirectory(directory: Path): LoadedLibrary? =
            findLibraryBySavefilePath(getSavefilePathFor(directory))

    private fun findLibraryBySavefilePath(savefilePath: Path): LoadedLibrary? =
            loadedLibraries.find { savefilePath == it.savefilePath }

    fun getOrCreateLibrary(directory: Path): Library<ImageUrl> =
            (findLibraryByDirectory(directory) ?: createLibrary(getDefaultSavefilePath(directory))).library

    // TODO delegate to other class
    private fun getDefaultSavefilePath(directory: Path): Path = directory.normalize().resolve(SaveFileLoader.SAVEFILE_NAME)
}
