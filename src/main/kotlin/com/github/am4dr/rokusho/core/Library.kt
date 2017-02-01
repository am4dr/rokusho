package com.github.am4dr.rokusho.core

import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors

interface Library {
    fun getTags(): List<Tag>
    fun getItemMetaData(): List<LibraryItemMetaData>
    fun updateTag(tag: Tag): Unit
    fun removeTag(id: String): Unit
    fun updateItemMetaData(itemMetaData: LibraryItemMetaData): Unit
    fun removeItem(id: String): Unit
    fun updateTagAll(vararg tags: Tag): Unit = tags.forEach { updateTag(it) }
    fun removeTagAll(vararg ids: String): Unit = ids.forEach { removeTag(it) }
    fun updateItemMetaDataAll(vararg itemMetaData: LibraryItemMetaData): Unit = itemMetaData.forEach { updateItemMetaData(it) }
    fun removeItemAll(vararg ids: String): Unit = ids.forEach { removeItem(it) }
}
interface LibraryItemMetaData {
    val id: String
    val tags: List<Tag>
}
data class SimpleLibraryItemMetaData(
        override val id: String,
        override val tags: List<Tag> = listOf()
) : LibraryItemMetaData

class SimpleLibrary(tags: List<Tag> = listOf(), itemMetaData: List<LibraryItemMetaData> = listOf()) : Library {
    private val _tags: MutableList<Tag> = tags.toMutableList()
    private val _itemMetaData: MutableList<LibraryItemMetaData> = itemMetaData.toMutableList()
    override fun getTags(): List<Tag> = _tags.toList()
    override fun getItemMetaData(): List<LibraryItemMetaData> = _itemMetaData.toList()
    override fun updateTag(tag: Tag) {
        removeTag(tag.id)
        _tags.add(tag)
    }
    override fun removeTag(id: String) {
        _tags.removeAll { it.id == id }
    }
    override fun updateItemMetaData(itemMetaData: LibraryItemMetaData) {
        removeItem(itemMetaData.id)
        _itemMetaData.add(itemMetaData)
    }
    override fun removeItem(id: String) {
        _itemMetaData.removeAll { it.id == id }
    }
}

interface PathLibrary : Library {
    var fileWalkRoot: Path
    val savefileRoot: Path
    fun getPaths(): List<Path> = Files.walk(fileWalkRoot, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList<Path>())
    fun getItems(): List<Pair<Path, LibraryItemMetaData>> {
        val m = getItemMetaData()
        return getPaths().map {
            val id = it.toIdFormat()
            Pair(it, m.find { it.id == id } ?: SimpleLibraryItemMetaData(id))
        }
    }
    fun Path.toIdFormat(): String = this@PathLibrary.savefileRoot.relativize(this).joinToString("/")
}
class SimplePathLibrary(
        override var fileWalkRoot: Path,
        override val savefileRoot: Path,
        private val library: Library)
    : PathLibrary, Library by library {
    constructor(fileWalkRoot : Path, savefileRoot: Path, tags: List<Tag> = listOf(), itemMetaData: List<LibraryItemMetaData> = listOf())
            : this(fileWalkRoot, savefileRoot, SimpleLibrary(tags, itemMetaData))
}

interface FilteredPathLibrary : PathLibrary {
    val matcher: (Path?, BasicFileAttributes?) -> Boolean
    override fun getPaths(): List<Path> =
            Files.find(fileWalkRoot, Int.MAX_VALUE, matcher, arrayOf(FileVisitOption.FOLLOW_LINKS))
                    .collect(Collectors.toList<Path>())
}
