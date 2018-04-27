package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.app.datastore.DataStore
import com.github.am4dr.rokusho.app.savedata.SaveData
import java.nio.file.Path


class SaveDataStoreProvider(private val dataStoreConstructor: (Path) -> DataStore<SaveData>) {

    private val stores = mutableMapOf<Path, DataStore<SaveData>>()

    fun getOrCreate(path: Path): DataStore<SaveData> =
            stores[path] ?: dataStoreConstructor(path).also { stores[path] = it }
}