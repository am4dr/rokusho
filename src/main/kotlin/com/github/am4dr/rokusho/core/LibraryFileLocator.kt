package com.github.am4dr.rokusho.core

import com.github.am4dr.rokusho.core.LibraryFileLocator.Companion.DEFAULT_SAVEFILE_NAME
import java.nio.file.Files
import java.nio.file.Path

interface LibraryFileLocator {
    companion object {
        // TODO rokushoを入れた名前に変更
        const val DEFAULT_SAVEFILE_NAME = "image_tag_info.yaml"
    }
    fun locate(path: Path): ParsedLibrary
}
class DefaultLibraryFileLocator : LibraryFileLocator {
    private val parser = DefaultLibraryFileParser()
    override fun locate(path: Path): ParsedLibrary {
        val real = path.toRealPath() ?: throw IllegalArgumentException("path $path must be exist")
        if (Files.isRegularFile(real)) return parser.parse(real)
        val location = recursiveLocate(real)
        return if (location != null) parser.parse(location)
        else {
            SimpleParsedLibrary(path.resolve(DEFAULT_SAVEFILE_NAME))
        }
    }
    private tailrec fun recursiveLocate(path: Path?): Path? {
        if (path == null) { return null }
        val files = path.toFile().listFiles { file -> file.name == LibraryFileLocator.DEFAULT_SAVEFILE_NAME }
        return if (files.isNotEmpty()) files.first().toPath() else recursiveLocate(path.parent)
    }
}
