package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import java.nio.file.Path


class SaveDataStoreProvider(private val dataStoreConstructor: (Path) -> SaveDataStore<SaveData>) {

    private val stores = mutableMapOf<Path, SaveDataStore<SaveData>>()

    fun getOrCreate(path: Path): SaveDataStore<SaveData> =
            stores[path] ?: dataStoreConstructor(path).also { stores[path] = it }
}