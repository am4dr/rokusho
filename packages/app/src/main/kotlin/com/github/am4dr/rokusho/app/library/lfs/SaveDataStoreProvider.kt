package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import java.nio.file.Path

class SaveDataStoreProvider(private val saveDataStoreLocator: (Path) -> Pair<Path, SaveDataStore<SaveData>>,
                            private val knownStore: MutableMap<Path, SaveDataStore<SaveData>> = mutableMapOf()) {

    fun get(path: Path): Pair<Path, SaveDataStore<SaveData>> {
        val known = findMostInnerKnownStore(path)
        known?.let {
            if  (it.first.toRealPath() == path.toRealPath()) {
                return it
            }
        }
        val located = saveDataStoreLocator(path)

        val mostInner = if (known != null && known.first.toRealPath().nameCount >= located.first.toRealPath().nameCount) {
            known
        }
        else {
            located
        }
        return mostInner.apply {
            knownStore[first.toRealPath()] = second
        }
    }
    private fun findMostInnerKnownStore(path: Path): Pair<Path, SaveDataStore<SaveData>>? =
            knownStore.filterKeys { path.startsWith(it) }.maxBy { it.key.nameCount }?.toPair()
}