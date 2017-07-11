package com.github.am4dr.rokusho.app.savefile

interface SaveDataSerializer {
    fun serialize(data: SaveData): String
}