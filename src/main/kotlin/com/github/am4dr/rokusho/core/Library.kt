package com.github.am4dr.rokusho.core

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
