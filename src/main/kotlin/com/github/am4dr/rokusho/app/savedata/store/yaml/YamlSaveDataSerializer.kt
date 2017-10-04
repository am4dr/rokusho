package com.github.am4dr.rokusho.app.savedata.store.yaml

import com.github.am4dr.rokusho.app.savedata.ItemMetaData
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataSerializer
import com.github.am4dr.rokusho.core.library.ItemTag
import org.yaml.snakeyaml.Yaml

class YamlSaveDataSerializer : SaveDataSerializer<SaveData> {
    companion object {
        const val pathSeparator: String = "/"

        // Dump method of SnakeYAML converts same objects into a YAML anchor and references.
        // To avoid that, if the 'tags' is empty, create a new empty mutable map.
        private fun ItemMetaData.toDumpStructure(): Map<String, Any> =
                mapOf("tags" to tags.map { it.toDumpStructure() }.toMap(mutableMapOf()))

        private fun ItemTag.toDumpStructure(): Pair<String, Any> = tag.id to (value?.let { mapOf("value" to it) } ?: mutableMapOf())

        private fun SaveData.toDumpStructure(): Map<String, Any> =
                mapOf(
                        "version" to version.stringValue,
                        "tags" to tags.map {
                            val name = it.key
                            val info = it.value
                            Pair(name, info.data)
                        }.toMap().toMutableMap(),
                        "metaData" to metaData.map {
                            val path = it.key.joinToString(pathSeparator)
                            val data = it.value
                            Pair(path, data.toDumpStructure())
                        }.toMap().toMutableMap())
    }

    override fun invoke(data: SaveData): ByteArray = Yaml().dump(data.toDumpStructure()).toByteArray()
}