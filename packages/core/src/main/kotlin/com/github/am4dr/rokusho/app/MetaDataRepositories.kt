package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.adapter.DataStoreConverter
import com.github.am4dr.rokusho.core.metadata.DefaultMetaDataRepositoryImpl
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository
import com.github.am4dr.rokusho.old.datastore.file.yaml.YamlSaveDataStore
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher

class MetaDataRepositories {

    companion object {
        private const val defaultSavefileName: String = "rokusho.yaml"
        private val saveFilePathMatchers: PathMatcher = PathMatcher { it.endsWith(defaultSavefileName) }
    }

    private val knownRepositories = mutableMapOf<Path, MetaDataRepository>()

    fun get(savefile: Path): Pair<Path, MetaDataRepository>? =
            knownRepositories.entries.find { Files.isSameFile(it.key, savefile) }?.toPair()

    fun getByDirectory(path: Path): Pair<Path, MetaDataRepository>? =
            if (Files.isDirectory(path)) knownRepositories.entries.find { Files.isSameFile(it.key.parent, path) }?.toPair()
            else null

    fun getOrCreate(savefileParent: Path): Pair<Path, MetaDataRepository> {
        getByDirectory(savefileParent)?.let { return it }

        val location = findLibraryRoot(savefileParent) ?: savefileParent
        val savefile = location.resolve(defaultSavefileName)
        val store = DataStoreConverter(YamlSaveDataStore(savefile))
        val repo = store.load() ?: DefaultMetaDataRepositoryImpl()
        knownRepositories[savefile] = repo
        return savefile to repo
    }

    internal tailrec fun findLibraryRoot(path: Path): Path? =
            when {
                isLibraryRoot(path) -> path
                path.parent != null -> findLibraryRoot(path.parent)
                else -> null
            }

    internal fun isLibraryRoot(path: Path): Boolean =
            knownRepositories.any { (savefile, _) -> Files.isSameFile(path, savefile.parent) } ||
                    Files.isDirectory(path) && Files.list(path).anyMatch(saveFilePathMatchers::matches)
}
