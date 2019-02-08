package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library2.ItemTag
import com.github.am4dr.rokusho.library2.Library
import com.github.am4dr.rokusho.library2.LibraryItem
import com.github.am4dr.rokusho.library2.TagData


// TODO 実装する
class LibraryItemViewModel(
    private val library: Library,
    private val libraryItem: LibraryItem<*>
) : ItemViewModel<Any> {

    override val item: Any = libraryItem.item.data
    private val _tags: List<LibraryItemTagData> = libraryItem.tags.map(::LibraryItemTagData)
    override val tags: List<ItemTagData> = _tags

    override fun parseTagString(string: String): ItemTagData? {
        return string.let(TagData.Companion::parse)
            ?.let(library::createTag)
            ?.let(::ItemTag)
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

// TODO 上の実装で動いたら、これの削除
//class LibraryItemItemViewModel(
//    private val library: Library<*>,
//    private val libraryItem: LibraryItem<*>
//) : ItemViewModel<Any> {
//
//    override val item: Any get() = libraryItem.get()
//    override val tags: List<LibraryItemTag> get() = libraryItem.getTags().toList()
//
//    override fun parseTagString(string: String): LibraryItemTag? =
//        library.parseItemTag(string)
//
//    override fun updateTags(tags: List<LibraryItemTag>) {
//        libraryItem.updateTags(tags.toSet())
//    }
//
//    fun has(item: LibraryItem<*>): Boolean = libraryItem === item
//}