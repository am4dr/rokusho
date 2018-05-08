package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.datastore.yaml.YamlSaveDataStore
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.app.library.fs.FileSystemLibraryLoader
import com.github.am4dr.rokusho.app.library.fs.LibraryRootDetector
import com.github.am4dr.rokusho.app.library.toRokushoLibrary
import com.github.am4dr.rokusho.core.library.filter
import com.github.am4dr.rokusho.core.library.transform
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ImageLibraryLoader : LibraryLoader<Path, ImageUrl> {

    override val name: String = "image-file-loader"

    private val fsLoader = createFileSystemLibraryLoader()

    override fun load(specifier: String): RokushoLibrary<ImageUrl> = load(Paths.get(specifier))
    override fun load(specifier: Path): RokushoLibrary<ImageUrl> =
            fsLoader.load(specifier).let { base ->
                base.filter { Rokusho.isSupportedImageFile(it) }
                        .transform { ImageUrl(it.toUri().toURL()) }
                        .toRokushoLibrary(specifier.toString(), base.shortName, base::save)
            }
}


private fun createFileSystemLibraryLoader(): FileSystemLibraryLoader {
    val saveFileName = "rokusho.yaml"
    val libraryRootDetector: LibraryRootDetector = { path -> Files.isRegularFile(path.resolve(saveFileName)) }
    val saveDataStoreProvider = SaveDataStoreProvider { YamlSaveDataStore(it.resolve(saveFileName)) }
    return FileSystemLibraryLoader(libraryRootDetector, saveDataStoreProvider::getOrCreate)
}
