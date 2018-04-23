package com.github.am4dr.rokusho.app.savedata.store.yaml_new

import com.github.am4dr.rokusho.app.savedata.store.SaveDataSerializer
import java.nio.file.Files
import java.nio.file.Path

class FileBasedSaveDataStore<T>(private val path: Path, private val serializer: SaveDataSerializer<T>, private val deserializer: SaveDataDeserializer<T>) : SaveDataStore<T> {

    override fun save(data: T) {
        Files.write(getTargetFile(path), serializer.serialize(data))
    }

    override fun load(): T? {
        val bytes = if (Files.isRegularFile(path)) Files.readAllBytes(path) else byteArrayOf()
        val result = deserializer.deserialize(bytes)
        return result.result
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
