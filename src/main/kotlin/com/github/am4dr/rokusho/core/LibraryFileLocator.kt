package com.github.am4dr.rokusho.core

import com.github.am4dr.rokusho.core.LibraryFileLocator.Companion.DEFAULT_SAVEFILE_NAME
import java.nio.file.Files
import java.nio.file.Path

interface LibraryFileLocator {
    companion object {
        const val DEFAULT_SAVEFILE_NAME = "rokusho.yaml"
    }
    fun locate(path: Path): Path
}
class DefaultLibraryFileLocator : LibraryFileLocator {
    override fun locate(path: Path): Path {
        val real = path.toRealPath() ?: throw IllegalArgumentException("path $path must be exist")
        if (Files.isRegularFile(real)) return real
        return recursiveLocate(real) ?: path.resolve(DEFAULT_SAVEFILE_NAME)
    }
    private tailrec fun recursiveLocate(path: Path?): Path? {
        if (path == null) { return null }
        val files = path.toFile().listFiles { file -> file.name == LibraryFileLocator.DEFAULT_SAVEFILE_NAME }
        return if (files.isNotEmpty()) files.first().toPath() else recursiveLocate(path.parent)
    }
}
