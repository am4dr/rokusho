package com.github.am4dr.rokusho.datastore.savedata.yaml

import com.github.am4dr.rokusho.datastore.DataStore
import com.github.am4dr.rokusho.datastore.file.FileBasedDataStore
import com.github.am4dr.rokusho.datastore.savedata.SaveData
import java.nio.file.Path

class YamlSaveDataStore(
    savefile: Path
) : DataStore<SaveData> by FileBasedDataStore(
    savefile,
    YamlSaveDataSerializer(),
    YamlSaveDataDeserializer()
)