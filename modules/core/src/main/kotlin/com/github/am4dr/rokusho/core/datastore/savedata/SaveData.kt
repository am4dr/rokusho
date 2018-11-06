package com.github.am4dr.rokusho.core.datastore.savedata


data class SaveData(val tags: List<Tag>, val items: List<Item>) {

    companion object {
        val EMPTY: SaveData = SaveData(listOf(), listOf())
    }
}
