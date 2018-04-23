package com.github.am4dr.rokusho.app.savedata.store.yaml

import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.SaveDataSerializer
import com.github.am4dr.rokusho.app.savedata.store.yaml.v1.V1SaveData
import com.github.am4dr.rokusho.app.savedata.store.yaml.v1.serialize

class YamlSaveDataSerializer : SaveDataSerializer<SaveData> {

    override fun serialize(data: SaveData): ByteArray = serialize(V1SaveData.from(data))
}