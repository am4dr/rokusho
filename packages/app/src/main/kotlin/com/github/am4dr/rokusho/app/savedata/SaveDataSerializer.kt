package com.github.am4dr.rokusho.app.savedata

interface SaveDataSerializer<in T> {
    fun serialize(data: T): ByteArray
}
