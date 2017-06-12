package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemSet
import com.github.am4dr.rokusho.core.library.Library
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class LibraryLoader {
    companion object {
        private const val SAVEFILE_NAME = "rokusho.yaml"
    }
    private data class LoadedLibrary(val library: Library<ImageUrl>, val path: Path, val depth: Int)

    private val loadedLibraries: List<LoadedLibrary> = mutableListOf()

    fun loadDirectory(directory: Path, depth: Int): ItemSet<ImageUrl> {
        val lib = findLibrary(directory)
                ?: locateSaveFilePath(directory)?.let { loadSaveFile(it, depth) }
                ?: createLibrary(directory, depth)
        return lib.library.getItemSet(collectImageUrls(directory, depth))
    }

    private fun findLibrary(directory: Path): LoadedLibrary? =
            loadedLibraries.find { Files.isSameFile(it.path, directory) }?.let { return it }

    private fun locateSaveFilePath(directory: Path): Path? =
            directory.resolve(SAVEFILE_NAME).takeIf { Files.exists(it) }
                    ?: directory.parent?.let { locateSaveFilePath(it) }

    private fun loadSaveFile(savefile: Path, depth: Int): LoadedLibrary =
            LoadedLibrary(SaveFileLoader().load(savefile), savefile.parent, depth)

    private fun createLibrary(directory: Path, depth: Int): LoadedLibrary =
            LoadedLibrary(DefaultLibrary(), directory, depth)

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())
}
