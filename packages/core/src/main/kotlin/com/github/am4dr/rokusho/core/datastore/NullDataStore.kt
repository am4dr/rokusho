package com.github.am4dr.rokusho.core.datastore

class NullDataStore<T> : DataStore<T> {

    override fun save(data: T) {
        // nop
    }

    override fun load(): T? {
        return null
    }
}