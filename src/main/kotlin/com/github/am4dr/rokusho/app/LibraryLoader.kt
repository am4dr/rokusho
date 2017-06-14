package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savefile.SaveFileLoader
import com.github.am4dr.rokusho.core.library.DefaultLibrary
import com.github.am4dr.rokusho.core.library.ItemSet
import com.github.am4dr.rokusho.core.library.Library
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class LibraryLoader {
    private data class LoadedLibrary constructor(val library: Library<ImageUrl>, val libraryDir: Path, val savefileDir: Path)

    private val savefileLoader = SaveFileLoader()
    private val loadedLibraries: MutableList<LoadedLibrary> = mutableListOf()
    
    fun loadDirectory(directory: Path): Library<ImageUrl> {
        return (findLibrary(directory)
                ?: savefileLoader.locateSaveFilePath(directory)?.let { LoadedLibrary(savefileLoader.load(it), directory, it.parent).also { loadedLibraries.add(it) } }
                ?: createLibrary(directory)).library
    }
    @Deprecated("一時的な実装")
    fun getItemSet(directory: Path, depth: Int): ItemSet<ImageUrl> {
        return (findLibrary(directory) ?:createLibrary(directory)).library.getItemSet(collectImageUrls(directory, depth))
    }

    private fun findLibrary(directory: Path): LoadedLibrary? =
            loadedLibraries.map { it.libraryDir.normalize() to it }
                    .filter { (libDir, _) -> directory.normalize().startsWith(libDir) }
                    .maxBy { (libDir, _) -> libDir.nameCount }
                    ?.second

    private fun createLibrary(directory: Path): LoadedLibrary =
            LoadedLibrary(DefaultLibrary(), directory, directory).also { loadedLibraries.add(it) }

    private fun collectImageUrls(directory: Path, depth: Int): List<ImageUrl> =
            Files.walk(directory, depth)
                    .filter(Rokusho.Companion::isSupportedImageFile)
                    .map { ImageUrl(it.toUri().toURL()) }
                    .collect(Collectors.toList())
}
