package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemSet
import com.github.am4dr.rokusho.core.library.Library
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class LibraryLoader {
    private data class LoadedLibrary (val library: Library<ImageUrl>, val libraryRoot: Path, val savefileDir: Path)

    private val savefileLoader = SaveFileLoader()
    private val loadedLibraries: MutableList<LoadedLibrary> = mutableListOf()

    // TODO test
    fun loadDirectory(directory: Path): Library<ImageUrl> {
        val loadedLibrary = findLibrary(directory)
        val savefileLocation = savefileLoader.locateSaveFilePath(directory)?.normalize()

        if (loadedLibrary == null && savefileLocation == null) return createLibrary(directory).library

        val loadedLibraryNameCount = loadedLibrary?.libraryRoot?.nameCount ?: 0
        val savefileLocationNameCount = savefileLocation?.nameCount ?: 0

        return if (loadedLibraryNameCount >= savefileLocationNameCount) {
            loadedLibrary!!
        }
        else {
            LoadedLibrary(savefileLoader.load(savefileLocation!!), savefileLocation.parent, savefileLocation.parent).also { loadedLibraries.add(it) }
        }.library
    }

    @Deprecated("一時的な実装")
    fun getItemSet(directory: Path, depth: Int): ItemSet<ImageUrl> {
        return (findLibrary(directory) ?:createLibrary(directory)).library.getItemSet(collectImageUrls(directory, depth))
    }

    private fun findLibrary(directory: Path): LoadedLibrary? =
            loadedLibraries.filter { directory.normalize().startsWith(it.libraryRoot) }
                    .maxBy { it.libraryRoot.nameCount }

    private fun createLibrary(directory: Path): LoadedLibrary =
            LoadedLibrary(DefaultLibrary(), directory.normalize(), directory.normalize()).also { loadedLibraries.add(it) }

    // TODO 移動
    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())
}
