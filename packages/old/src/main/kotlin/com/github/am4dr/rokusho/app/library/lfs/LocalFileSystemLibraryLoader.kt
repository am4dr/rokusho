package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import com.github.am4dr.rokusho.core.library.helper.LibrarySupport
import com.github.am4dr.rokusho.core.library.Record
import java.nio.file.Path
import java.util.*

class LocalFileSystemLibraryLoader(private val saveDataStoreProvider: (Path) -> Pair<Path, SaveDataStore<SaveData>>,
                                   private val fileCollector: (Path) -> List<Path>) {

    private val loadedLibraries = WeakHashMap<SaveDataStore<SaveData>, LocalFileSystemLibrary>()

    fun getLibrary(path: Path): LocalFileSystemLibrary {
        val (root, store) = saveDataStoreProvider(path)
        return loadedLibraries[store] ?: createLocalFileSystemLibrary(root, store)
    }

    private fun createLocalFileSystemLibrary(root: Path, store: SaveDataStore<SaveData>): LocalFileSystemLibrary =
            LocalFileSystemLibrary(root, store, createLibrary(root, store)).apply { loadedLibraries[store] = this }

    private fun createLibrary(root: Path, store: SaveDataStore<SaveData>): LibrarySupport<ImageUrl> =
            LibrarySupport<ImageUrl>().apply {
                val data = store.load()
                tags.putAll(data.tags)
                data.metaData.forEach { (path, meta) ->
                    val key = ImageUrl(root.resolve(path).toUri().toURL())
                    records[key] = Record(key, meta.tags)
                }
                fileCollector(root).map { ImageUrl(it.toUri().toURL()) }.forEach { key ->
                    records.getOrPut(key, { Record(key, listOf()) })
                }
            }
}
