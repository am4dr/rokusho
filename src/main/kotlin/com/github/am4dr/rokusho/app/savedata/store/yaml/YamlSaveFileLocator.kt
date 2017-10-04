package com.github.am4dr.rokusho.app.savedata.store.yaml

import java.nio.file.Files
import java.nio.file.Path

class YamlSaveFileLocator {
    companion object {
        const val DEFAULT_SAVEFILE_NAME = "rokusho.yaml"
    }

    fun locateSaveFilePath(directory: Path): Path? =
            directory.resolve(DEFAULT_SAVEFILE_NAME).takeIf { Files.exists(it) }
                    ?: directory.parent?.let { locateSaveFilePath(it) }

    fun locateSaveFilePathOrDefault(directory: Path): Path = locateSaveFilePath(directory) ?: getDefaultSavefilePath(directory)

    fun getDefaultSavefilePath(directory: Path): Path = directory.normalize().resolve(DEFAULT_SAVEFILE_NAME)
}