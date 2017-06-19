package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultMetaDataRegistry
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import java.nio.file.Files
import java.nio.file.Path

// TODO test
class LibraryLoader {

    private val savefileLoader = SaveFileLoader()
    private val loadedLibraries: MutableList<LoadedLibrary> = mutableListOf()

    private inner class LoadedLibrary (val metaDataRegistry: MetaDataRegistry<ImageUrl>, savefilePath: Path) {
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
            LoadedLibrary(DefaultMetaDataRegistry(), savefilePath.normalize())

    private fun getSavefilePathFor(directory: Path): Path {
        val loaded: LoadedLibrary? = findLibrariesContains(directory).maxBy { it.savefilePath.nameCount }
        val savefilePath = savefileLoader.locateSaveFilePath(directory)?.normalize()

        return when (compareValues(loaded?.savefilePath?.nameCount, savefilePath?.nameCount)) {
            0  -> loaded?.savefilePath ?: savefileLoader.getDefaultSavefilePath(directory)
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

    fun getOrCreateLibrary(directory: Path): MetaDataRegistry<ImageUrl> =
            (findLibraryByDirectory(directory) ?: createLibrary(savefileLoader.getDefaultSavefilePath(directory))).metaDataRegistry
}
