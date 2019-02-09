package com.github.am4dr.rokusho.datastore

interface DataStore<T> {
    fun save(data: T)
    fun load(): T?
}
