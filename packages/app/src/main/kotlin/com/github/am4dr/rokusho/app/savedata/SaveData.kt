package com.github.am4dr.rokusho.app.savedata

import com.github.am4dr.rokusho.core.library.Tag
import java.nio.file.Path

data class SaveData(
        val version: Version,
        val tags: Map<String, Tag>,
        val metaData: Map<Path, ItemMetaData>) {

    enum class Version(val stringValue: String) {
        VERSION_1("1");

        companion object {
            fun of(string: String): Version? = values().find { it.stringValue == string }
        }
    }
}
