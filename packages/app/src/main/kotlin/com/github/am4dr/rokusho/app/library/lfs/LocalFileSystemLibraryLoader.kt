package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.SaveData
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.SaveDataStore
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.helper.LibrarySupport
import java.nio.file.Path
import java.util.*

class LocalFileSystemLibraryLoader(private val saveDataStoreProvider: (Path) -> Pair<Path, SaveDataStore<SaveData>>,
                                   private val fileCollector: (Path) -> List<Path>) {

    private val loadedLibraries = WeakHashMap<SaveDataStore<SaveData>, LocalFileSystemLibrary>()

    fun getLibrary(path: Path): LocalFileSystemLibrary {
        val (root, store) = saveDataStoreProvider(path)
        return loadedLibraries[store] ?: createLocalFileSystemLibrary(root, store)
    }

    private fun createLocalFileSystemLibrary(root: Path, store: SaveDataStore<SaveData>): LocalFileSystemLibrary {
        var loaded = false
        val data = store.load()?.also { loaded = true } ?: SaveData.EMPTY
        return LocalFileSystemLibrary(root, store, createLibrary(root, data), !loaded).apply { loadedLibraries[store] = this }
    }

    private fun createLibrary(root: Path, data: SaveData): LibrarySupport<ImageUrl> =
            LibrarySupport<ImageUrl>().apply {
                tags.putAll(data.tags.map { it.id to it })
                data.items.forEach { (id, meta) ->
                    val key = ImageUrl(root.resolve(id).toUri().toURL())
                    records[key] = Record(key, meta.tags)
                }
                fileCollector(root).map { ImageUrl(it.toUri().toURL()) }.forEach { key ->
                    records.getOrPut(key, { Record(key, listOf()) })
                }
            }
}
