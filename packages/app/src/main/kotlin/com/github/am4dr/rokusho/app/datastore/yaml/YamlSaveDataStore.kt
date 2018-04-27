package com.github.am4dr.rokusho.app.datastore.yaml

import com.github.am4dr.rokusho.app.datastore.DataStore
import com.github.am4dr.rokusho.app.datastore.FileBasedDataStore
import com.github.am4dr.rokusho.app.savedata.SaveData
import java.nio.file.Path

class YamlSaveDataStore(savefile: Path) : DataStore<SaveData> by FileBasedDataStore(savefile, YamlSaveDataSerializer(), YamlSaveDataDeserializer())