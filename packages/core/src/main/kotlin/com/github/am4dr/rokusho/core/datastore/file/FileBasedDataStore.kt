package com.github.am4dr.rokusho.core.datastore.file

import com.github.am4dr.rokusho.core.datastore.DataStore
import java.nio.file.Files
import java.nio.file.Path

// TODO expose save() is enabled or not
class FileBasedDataStore<T>(private val path: Path,
                            private val serializer: Serializer<T>,
                            private val deserializer: Deserializer<T>) : DataStore<T> {

    @Volatile
    private var saveEnabled: Boolean = true

    override fun save(data: T) {
        if (saveEnabled) {
            Files.write(getOrCreateTargetFile(path), serializer.serialize(data))
        }
    }

    override fun load(): T? {
        val bytes = if (Files.isRegularFile(path)) Files.readAllBytes(path) else byteArrayOf()
        val result = deserializer.deserialize(bytes)
        return result.result.also { if (it == null) saveEnabled = false }
    }

    private fun getOrCreateTargetFile(path: Path): Path {
        return if (Files.exists(path)) {
            path
        } else {
            Files.createDirectories(path.parent)
            Files.createFile(path)
        }
    }
}
