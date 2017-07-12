package com.github.am4dr.rokusho.app.savefile.yaml

import com.github.am4dr.rokusho.app.savefile.SaveFile
import java.nio.file.Files
import java.nio.file.Path

class YamlSaveFileLoader {
    companion object {
        const val DEFAULT_SAVEFILE_NAME = "rokusho.yaml"
    }

    fun load(savefilePath: Path): SaveFile = YamlSaveFileParser().parse(savefilePath)

    fun locateSaveFilePath(directory: Path): Path? =
            directory.resolve(DEFAULT_SAVEFILE_NAME).takeIf { Files.exists(it) }
                    ?: directory.parent?.let { locateSaveFilePath(it) }

    fun getDefaultSavefilePath(directory: Path): Path = directory.normalize().resolve(DEFAULT_SAVEFILE_NAME)
}