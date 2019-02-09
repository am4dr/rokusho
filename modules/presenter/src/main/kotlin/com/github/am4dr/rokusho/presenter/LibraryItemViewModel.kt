package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library.ItemTag
import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.LibraryItem
import com.github.am4dr.rokusho.library.TagData


class LibraryItemViewModel(
    private val library: Library,
    private val libraryItem: LibraryItem<*>
) : ItemViewModel<Any> {

    override val item: Any = libraryItem.item.data
    private val _tags: List<LibraryItemTagData> = libraryItem.tags.map(::LibraryItemTagData)
    override val tags: List<ItemTagData> = _tags

    override fun parseTagString(string: String): ItemTagData? {
        /* TODO この処理はLibraryに実装を移す */
        return string.let(TagData.Companion::parse)
            ?.let(TagData::name)
            ?.let(library::createItemTagByName)
            ?.let(::LibraryItemTagData)
    }

    override fun updateTags(tags: List<ItemTagData>) {
        if (tags.any { it !is LibraryItemTagData }) return

        tags as List<LibraryItemTagData>
        val newTags = tags.mapTo(mutableSetOf(), LibraryItemTagData::itemTag)
        val newLibraryItem = libraryItem.update(newTags)
        library.update(newLibraryItem)
    }

    fun has(libraryItem: LibraryItem<*>): Boolean {
        return this.libraryItem.isSameEntity(libraryItem)
    }

    private class LibraryItemTagData(val itemTag: ItemTag) : ItemTagData {

        override val name: String
            get() = itemTag.tag.name

        override fun get(key: String): String? = itemTag[key]
    }
}
