package com.github.am4dr.rokusho.app.library.fs

import com.github.am4dr.rokusho.app.datastore.DataStore
import com.github.am4dr.rokusho.app.savedata.SaveData
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes


typealias LibraryRootDetector = (Path) -> Boolean

class FileSystemLibraryLoader(
        private val libraryRootDetector: LibraryRootDetector,
        private val saveDataStoreProvider: (Path) -> DataStore<SaveData>,
        private val collectDepthLimit: Int = 3 /* limit for safety */) {

    private val knownLibraries = mutableListOf<FileSystemLibrary>()

    fun load(path: Path): FileSystemLibrary {
        val root = findLibraryRoot(path) ?: path
        knownLibraries.find { Files.isSameFile(it.root, root) }?.let { return it }

        val dataStore = saveDataStoreProvider(root)
        val saveData = dataStore.load() ?: SaveData.EMPTY
        return FileSystemLibrary(root, dataStore, saveData.toFileSystemLibrary(root, collectPaths(root))).also {
            knownLibraries.add(it)
        }
    }

    internal tailrec fun findLibraryRoot(path: Path): Path? =
            when {
                isLibraryRoot(path) -> path
                path.parent != null -> findLibraryRoot(path.parent)
                else -> null
            }

    private fun isLibraryRoot(path: Path): Boolean =
            knownLibraries.any { Files.isSameFile(path, it.root) } || libraryRootDetector(path)

    internal fun collectPaths(root: Path, depth: Int = collectDepthLimit): List<Path> {
        val list = mutableListOf<Path>()
        Files.walkFileTree(root, setOf(), depth, object : FileVisitor<Path> {
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult =
                    if (Files.isSameFile(dir, root) || !isLibraryRoot(dir)) FileVisitResult.CONTINUE else FileVisitResult.SKIP_SUBTREE
            override fun visitFile(path: Path, attrs: BasicFileAttributes?): FileVisitResult {
                list.add(path)
                return FileVisitResult.CONTINUE
            }
            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
            override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
        })
        return list
    }
}
