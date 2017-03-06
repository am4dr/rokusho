package com.github.am4dr.rokusho.core

import java.nio.file.Path

interface LibraryFileParser {
    fun parse(path: Path): Library
}
