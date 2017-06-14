package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemSet
import com.github.am4dr.rokusho.core.library.Library
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class LibraryLoader {
    private data class LoadedLibrary(val library: Library<ImageUrl>, val savefileDir: Path)

    private val loadedLibraries: MutableList<LoadedLibrary> = mutableListOf()

    fun loadDirectory(directory: Path): Library<ImageUrl> {
        return (findLibrary(directory)
                ?: locateSaveFilePath(directory)?.let { loadSaveFile(it) }
                ?: createLibrary(directory)).library
    }
    @Deprecated("一時的な実装")
    fun getItemSet(directory: Path, depth: Int): ItemSet<ImageUrl> {
        return (findLibrary(directory) ?:createLibrary(directory)).library.getItemSet(collectImageUrls(directory, depth))
    }

    private fun findLibrary(directory: Path): LoadedLibrary? =
            loadedLibraries.find { Files.isSameFile(it.savefileDir, directory) }

    // TODO SaveFileLoaderにうつすか
    private fun locateSaveFilePath(directory: Path): Path? =
            directory.resolve(SaveFileLoader.SAVEFILE_NAME).takeIf { Files.exists(it) }
                    ?: directory.parent?.let { locateSaveFilePath(it) }

    private fun loadSaveFile(savefile: Path): LoadedLibrary =
            LoadedLibrary(SaveFileLoader().load(savefile), savefile.parent).also { loadedLibraries.add(it) }

    private fun createLibrary(directory: Path): LoadedLibrary =
            LoadedLibrary(DefaultLibrary(), directory).also { loadedLibraries.add(it) }

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())
}
