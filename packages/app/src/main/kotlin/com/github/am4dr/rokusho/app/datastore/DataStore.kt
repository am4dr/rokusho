package com.github.am4dr.rokusho.app.datastore

interface DataStore<T> {
    fun save(data: T)
    fun load(): T?
}
