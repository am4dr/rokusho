package com.github.am4dr.rokusho.adapter

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository
import com.github.am4dr.rokusho.old.savedata.SaveData

class DataStoreConverter(private val store: DataStore<SaveData>) : DataStore<MetaDataRepository> {

    override fun save(data: MetaDataRepository) {
        store.save(toSaveData(data))
    }

    override fun load(): MetaDataRepository? {
        return toMetaDataRepository(store.load(), this)
    }
}