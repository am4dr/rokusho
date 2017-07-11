package com.github.am4dr.rokusho.app.savefile.yaml

import com.github.am4dr.rokusho.app.savefile.ImageMetaData
import com.github.am4dr.rokusho.app.savefile.SaveData
import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import org.yaml.snakeyaml.Yaml

class YamlSaveDataSerializer : SaveDataSerializer {
    companion object {
        const val pathSeparator: String = "/"

        // Dump method of SnakeYAML converts same objects into a YAML anchor and references.
        // To avoid that, if the 'tags' is empty, create a new empty mutable map.
        private fun ImageMetaData.toDumpStructure(): Map<String, Any> =
                mapOf("tags" to tags.map { it.id to it.data }.toMap(mutableMapOf()))

        private fun SaveData.toDumpStructure(): Map<String, Any> =
                mapOf(
                        "version" to version.stringValue,
                        "tags" to tags.map {
                            val name = it.key
                            val info = it.value
                            Pair(name, info.data)
                        }.toMap(),
                        "metaData" to metaData.map {
                            val path = it.key.joinToString(pathSeparator)
                            val data = it.value
                            Pair(path, data.toDumpStructure())
                        }.toMap())
    }

    override fun serialize(data: SaveData): String = Yaml().dump(data.toDumpStructure())
}