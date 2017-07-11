package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.core.library.Tag
import java.nio.file.Path

data class SaveData(
        val version: String,
        val tags: Map<String, Tag>,
        val metaData: Map<Path, ImageMetaData>) {
}

