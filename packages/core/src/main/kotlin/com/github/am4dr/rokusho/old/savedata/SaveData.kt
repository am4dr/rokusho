package com.github.am4dr.rokusho.old.savedata

import com.github.am4dr.rokusho.old.core.library.Tag


data class SaveData(val tags: List<Tag>, val items: List<Item>) {

    companion object {
        val EMPTY: SaveData = SaveData(listOf(), listOf())
    }
}
