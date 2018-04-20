package com.github.am4dr.rokusho.app.savedata.store

interface SaveDataStore<T> {
    fun save(data: T)
    fun load(): T
}
