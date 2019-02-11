package com.github.am4dr.rokusho.library

import com.github.am4dr.rokusho.datastore.DataStore
import com.github.am4dr.rokusho.datastore.NullDataStore

class LoadedLibrary private constructor(
    private val id: Any,
    val library: Library,
    val name: String,
    /* TODO DataStoreは古いinterfaceを流用しているに過ぎないので、適したものを作る */
    private val defaultStore: DataStore<Library.Data> = NullDataStore()
) : Entity<LoadedLibrary> {

    constructor(library: Library, name: String, store: DataStore<Library.Data>) : this(Any(), library, name, store)


    fun update(name: String): LoadedLibrary =
        LoadedLibrary(id, library, name, defaultStore)
    fun update(defaultStore: DataStore<Library.Data>): LoadedLibrary =
        LoadedLibrary(id, library, name, defaultStore)

    fun saveToDefault() {
        defaultStore.save(library.asData())
    }


    override fun isSameEntity(other: LoadedLibrary): Boolean =
        other.id === id

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean =
        other is LoadedLibrary && isSameEntity(other)
}
