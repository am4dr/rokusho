package com.github.am4dr.rokusho.app.datastore.yaml

import com.github.am4dr.rokusho.app.datastore.Serializer
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.yaml.Versions
import com.github.am4dr.rokusho.app.savedata.yaml.v1.V1SaveData
import com.github.am4dr.rokusho.app.savedata.yaml.v1.serialize as serializeV1

class YamlSaveDataSerializer : Serializer<SaveData> {

    override fun serialize(data: SaveData): ByteArray = serialize(data, Versions.CURRENT)
}

fun serialize(data: SaveData, version: Versions = Versions.CURRENT): ByteArray = getSerializer(version).invoke(data)

fun getSerializer(version: Versions): (SaveData) -> ByteArray {
    when (version) {
        Versions.UNKNOWN -> throw IllegalArgumentException()
        Versions.V1 -> return { serializeV1(V1SaveData.from(it)) }
    }
}