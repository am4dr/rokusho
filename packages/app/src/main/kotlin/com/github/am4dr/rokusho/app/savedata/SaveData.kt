package com.github.am4dr.rokusho.app.savedata

import com.github.am4dr.rokusho.core.library.Tag


data class SaveData(val version: Version, val tags: List<Tag>, val items: List<Item>) {

    companion object {
        val EMPTY: SaveData = SaveData(Version.VERSION_1, listOf(), listOf())
    }

    enum class Version(val stringValue: String) {
        VERSION_1("1"), UNKNOWN("unknown");

        companion object {
            fun of(string: String): Version = values().find { it.stringValue == string } ?: UNKNOWN
        }
    }
}
