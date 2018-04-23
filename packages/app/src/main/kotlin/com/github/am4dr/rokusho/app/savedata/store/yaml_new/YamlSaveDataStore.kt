package com.github.am4dr.rokusho.app.savedata.store.yaml_new

import java.nio.file.Path

class YamlSaveDataStore(savefile: Path) : SaveDataStore<SaveData> by FileBasedSaveDataStore(savefile, YamlSaveDataSerializer(), YamlSaveDataDeserializer())