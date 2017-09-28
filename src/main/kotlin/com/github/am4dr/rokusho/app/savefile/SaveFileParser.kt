package com.github.am4dr.rokusho.app.savefile

import java.nio.file.Path

interface SaveFileParser {
    fun parse(path: Path): FileBasedSaveData

    open class IllegalSaveFormatException(message: String = "") : RuntimeException(message)
    class VersionNotSpecifiedException(message: String = ""): IllegalSaveFormatException(message)
}
