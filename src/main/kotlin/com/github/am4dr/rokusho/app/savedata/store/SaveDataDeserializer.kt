package com.github.am4dr.rokusho.app.savedata.store

interface SaveDataDeserializer<out T> {
    fun deserialize(bytes: ByteArray): T
}
