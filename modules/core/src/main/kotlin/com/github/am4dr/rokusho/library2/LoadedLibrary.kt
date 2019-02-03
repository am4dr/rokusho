package com.github.am4dr.rokusho.library2

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.datastore.NullDataStore

class LoadedLibrary(
    private val id: Any,
    val library: Library,
    val name: String,
    val defaultStore: DataStore<LoadedLibrary> = NullDataStore()
) : Entity<LoadedLibrary> {

    constructor(library: Library, name: String, store: DataStore<LoadedLibrary>) : this(Any(), library, name, store)


    fun update(name: String): LoadedLibrary =
        LoadedLibrary(id, library, name, defaultStore)
    fun update(defaultStore: DataStore<LoadedLibrary>): LoadedLibrary =
        LoadedLibrary(id, library, name, defaultStore)

    fun saveToDefault() {
        defaultStore.save(this)
    }


    override fun isSameEntity(other: LoadedLibrary): Boolean =
        other.id === id
}
