package com.github.am4dr.rokusho.app.savefile

import java.nio.file.Path

interface SaveFileParser {
    fun parse(path: Path): SaveFile
}
