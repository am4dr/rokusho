package com.github.am4dr.rokusho.core

import java.nio.file.Files
import java.nio.file.Path

interface LibraryFileLocator {
    companion object {
        // TODO rokushoを入れた名前に変更
        const val DEFAULT_SAVEFILE_NAME = "image_tag_info.yaml"
    }
    fun locate(path: Path): PathLibrary
}
class DefaultLibraryFileLocator : LibraryFileLocator {
    private val parser = DefaultLibraryFileParser()
    override fun locate(path: Path): PathLibrary {
        val real = path.toRealPath() ?: throw IllegalArgumentException("path $path must be exist")
        if (Files.isRegularFile(real)) return parser.parse(real)
        val location = recursiveLocate(real, real.resolve(LibraryFileLocator.DEFAULT_SAVEFILE_NAME))
        // TODO デフォルトが選択された場合ファイルが存在しないので、その時の動作をテストしておく
        val lib =
                if (Files.isRegularFile(location)) parser.parse(location)
                else SimplePathLibrary(location.parent)
        return lib
    }
    private tailrec fun recursiveLocate(path: Path?, default: Path): Path {
        if (path == null) { return default }
        val files = path.toFile().listFiles { file -> file.name == LibraryFileLocator.DEFAULT_SAVEFILE_NAME }
        return if (files.isNotEmpty()) files.first().toPath() else recursiveLocate(path.parent, default)
    }
}
