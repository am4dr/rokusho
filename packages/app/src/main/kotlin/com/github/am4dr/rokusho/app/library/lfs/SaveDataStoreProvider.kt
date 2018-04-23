package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.savedata.store.yaml_new.SaveDataStore
import java.nio.file.Path

class SaveDataStoreProvider<T>(private val saveDataStoreLocator: (Path) -> Pair<Path, SaveDataStore<T>>,
                               private val knownStore: MutableMap<Path, SaveDataStore<T>> = mutableMapOf()) {

    fun get(path: Path): Pair<Path, SaveDataStore<T>> {
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
    private fun findMostInnerKnownStore(path: Path): Pair<Path, SaveDataStore<T>>? =
            knownStore.filterKeys { path.startsWith(it) }.maxBy { it.key.nameCount }?.toPair()
}