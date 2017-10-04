package com.github.am4dr.rokusho.app.library.lfs

import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.store.SaveDataStore
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class FileCollector(private val saveDataStoreProvider: (Path) -> Pair<Path, SaveDataStore<SaveData>>, private val filter: (Path) -> Boolean) {

    fun collect(root: Path): List<Path> {
        val libraryRoot = saveDataStoreProvider(root).second
        val list = mutableListOf<Path>()
        Files.walkFileTree(root, object : FileVisitor<Path> {
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult =
                    if (saveDataStoreProvider(dir).second == libraryRoot) FileVisitResult.CONTINUE else FileVisitResult.SKIP_SUBTREE
            override fun visitFile(path: Path, attrs: BasicFileAttributes?): FileVisitResult {
                if (filter(path)) list.add(path)
                return FileVisitResult.CONTINUE
            }
            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
            override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
        })
        return list
   }
}