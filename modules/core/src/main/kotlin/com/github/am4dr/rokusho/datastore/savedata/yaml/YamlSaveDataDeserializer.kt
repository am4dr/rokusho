package com.github.am4dr.rokusho.datastore.savedata.yaml

import com.github.am4dr.rokusho.datastore.file.Deserializer
import com.github.am4dr.rokusho.datastore.file.Deserializer.Result
import com.github.am4dr.rokusho.datastore.savedata.SaveData
import org.yaml.snakeyaml.Yaml
import java.nio.charset.StandardCharsets
import com.github.am4dr.rokusho.datastore.savedata.yaml.v1.parse as parseV1

class YamlSaveDataDeserializer :
    Deserializer<SaveData> {
    override fun deserialize(bytes: ByteArray): Result<SaveData> =
        deserialize(bytes.toString(StandardCharsets.UTF_8))
}

fun deserialize(string: String): Result<SaveData> {
    if (string.isBlank()) return Result(SaveData.EMPTY)

    val msg = mutableListOf<String>()
    fun error(message: String): Result<SaveData> = Result(null, msg.apply { add("error: $message") }.toMutableList())
    fun warning(message: String): Unit { msg.add("warning: $message") }

    val data: Map<*, *> = Yaml().load(string) as? Map<*, *> ?: return error("top level must be a Map")
    val version = detectVersion(data)
    val parser = when (version) {
        Versions.UNKNOWN -> return error("unknown version is specified")
        null -> return error("version is not specified")
        else -> getParser(version)
    }

    return Result(parser(data))
}

fun detectVersion(map: Map<*, *>): Versions? {
    val version = map["version"] as? String ?: return null
    return Versions.getOrUnknown(version)
}

fun getParser(version: Versions): (Map<*, *>) -> SaveData {
    when (version) {
        Versions.UNKNOWN -> throw IllegalArgumentException()
        Versions.V1 -> return { parseV1(it).toSaveData() }
    }
}