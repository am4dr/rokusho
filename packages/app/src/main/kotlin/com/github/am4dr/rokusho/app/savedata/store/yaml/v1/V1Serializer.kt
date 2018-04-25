package com.github.am4dr.rokusho.app.savedata.store.yaml.v1

import com.github.am4dr.rokusho.app.savedata.SaveData
import org.yaml.snakeyaml.Yaml

// TODO test
fun serialize(data: V1SaveData): ByteArray = Yaml().dump(data.toDumpStructure()).toByteArray()

fun V1SaveData.toDumpStructure(): Map<Any, Any> {
    return mapOf(
            "version" to SaveData.Version.VERSION_1.stringValue,
            "tags" to tags.map { it.toDumpStructure() }.toMap().toMutableMap(),
            "metaData" to items.map { it.toDumpStructure() }.filter { (_, data) -> data.isNotEmpty() }.toMap().toMutableMap()
            )
}

fun TagEntry.toDumpStructure(): Pair<String, Any> = id to data
fun ItemEntry.toDumpStructure(): Pair<String, Map<String, Any>> {
    val tags = tags.map { it.toDumpStructure() }.toMap().toMutableMap()
    return id to if (tags.isNotEmpty()) mapOf("tags" to tags) else mutableMapOf()
}
fun ItemTagEntry.toDumpStructure(): Pair<String, Any> = id to data
