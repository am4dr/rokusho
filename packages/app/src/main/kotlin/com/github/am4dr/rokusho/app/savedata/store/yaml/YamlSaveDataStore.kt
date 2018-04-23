package com.github.am4dr.rokusho.app.savedata.store.yaml

import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.FileBasedSaveDataStore
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import java.nio.file.Path

class YamlSaveDataStore(savefile: Path) : SaveDataStore<SaveData> by FileBasedSaveDataStore(savefile, YamlSaveDataSerializer(), YamlSaveDataDeserializer())