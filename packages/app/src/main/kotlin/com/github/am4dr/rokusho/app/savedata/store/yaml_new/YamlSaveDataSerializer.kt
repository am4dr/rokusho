package com.github.am4dr.rokusho.app.savedata.store.yaml_new

import com.github.am4dr.rokusho.app.savedata.store.SaveDataSerializer
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.v1.V1SaveData
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.v1.serialize

class YamlSaveDataSerializer : SaveDataSerializer<SaveData> {

    override fun serialize(data: SaveData): ByteArray = serialize(V1SaveData.from(data))
}