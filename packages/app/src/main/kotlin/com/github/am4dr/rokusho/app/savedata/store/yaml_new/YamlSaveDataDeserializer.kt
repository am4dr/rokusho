package com.github.am4dr.rokusho.app.savedata.store.yaml_new

import com.github.am4dr.rokusho.app.savedata.store.yaml_new.SaveDataDeserializer.Result
import org.yaml.snakeyaml.Yaml
import java.nio.charset.StandardCharsets
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.v1.parse as parseV1

class YamlSaveDataDeserializer : SaveDataDeserializer<SaveData> {
    override fun deserialize(bytes: ByteArray): Result<SaveData> = deserialize(bytes.toString(StandardCharsets.UTF_8))
}

internal val CURRENT_VERSION = SaveData.Version.VERSION_1

fun deserialize(string: String): Result<SaveData> {
    if (string.isBlank()) return Result(SaveData.EMPTY)

    val msg = mutableListOf<String>()
    fun error(message: String): Result<SaveData> = Result(null, msg.apply { add("error: $message") }.toMutableList())
    fun warning(message: String): Unit { msg.add("warning: $message") }

    val data: Map<*, *> = Yaml().load(string) as? Map<*, *> ?: return error("top level must be a Map")
    val version: SaveData.Version = when (detectVersion(data)) {
        SaveData.Version.VERSION_1 -> SaveData.Version.VERSION_1
        SaveData.Version.UNKNOWN -> return error("unknown version is specified")
        null -> return error("version is not specified")
    }
    // select data parser by version

    val parsed = parseV1(data).toSaveData()
    return Result(parsed)
}

fun detectVersion(map: Map<*, *>): SaveData.Version? {
    val version = map["version"] as? String ?: return null
    return SaveData.Version.of(version)
}
