package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.app.library.fs.FileSystemLibraryLoader
import com.github.am4dr.rokusho.app.library.toRokushoLibrary
import com.github.am4dr.rokusho.old.core.library.filter
import com.github.am4dr.rokusho.old.core.library.transform
import java.nio.file.Path
import java.nio.file.Paths

class ImageLibraryLoader(private val fsLoader: FileSystemLibraryLoader) : LibraryLoader<Path, ImageUrl> {

    override val name: String = "image-file-loader"

    override fun load(specifier: String): RokushoLibrary<ImageUrl> = load(Paths.get(specifier))
    override fun load(specifier: Path): RokushoLibrary<ImageUrl> =
            fsLoader.load(specifier).let { base ->
                base.filter { Rokusho.isSupportedImageFile(it) }
                        .transform { ImageUrl(it.toUri().toURL()) }
                        .toRokushoLibrary(specifier.toString(), base.shortName, base::save)
            }
}
