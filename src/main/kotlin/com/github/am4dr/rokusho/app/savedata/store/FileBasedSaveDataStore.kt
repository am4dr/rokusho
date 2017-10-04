package com.github.am4dr.rokusho.app.savedata.store

import java.nio.file.Files
import java.nio.file.Path

// TODO エラー処理
class FileBasedSaveDataStore<T>(private val path: Path, private val serializer: SaveDataSerializer<T>, private val deserializer: SaveDataDeserializer<T>) : SaveDataStore<T> {

    override fun save(data: T) {
        Files.write(getTargetFile(path), serializer(data))
    }

    override fun load(): T {
        return if (Files.isRegularFile(path)) {
            deserializer(Files.readAllBytes(path))
        }
        else {
            deserializer(byteArrayOf())
        }
    }

    private fun getTargetFile(path: Path): Path {
        return if (Files.exists(path)) {
            path
        } else {
            Files.createDirectories(path.parent)
            Files.createFile(path)
        }
    }
}
