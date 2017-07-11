package com.github.am4dr.rokusho.app.savefile.yaml

import com.github.am4dr.rokusho.app.savefile.SaveData
import com.github.am4dr.rokusho.app.savefile.SaveDataSerializer
import org.yaml.snakeyaml.Yaml

class YamlSaveDataSerializer : SaveDataSerializer {
    companion object {
        const val pathSeparator: String = "/"
        private fun SaveData.toDumpStructure(): Map<String, Any> =
                mapOf(
                        "version" to version,
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