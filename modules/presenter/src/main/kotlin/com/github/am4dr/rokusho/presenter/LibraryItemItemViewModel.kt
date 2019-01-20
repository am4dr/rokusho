package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.LibraryItem
import com.github.am4dr.rokusho.library.LibraryItemTag

class LibraryItemItemViewModel(
    private val library: Library<*>,
    private val libraryItem: LibraryItem<*>
) : ItemViewModel<Any> {

    override val item: Any get() = libraryItem.get()
    override val tags: List<LibraryItemTag> get() = libraryItem.getTags().toList()

    override fun parseTagString(string: String): LibraryItemTag? =
        library.parseItemTag(string)

    override fun updateTags(tags: List<LibraryItemTag>) {
        libraryItem.updateTags(tags.toSet())
    }

    fun has(item: LibraryItem<*>): Boolean = libraryItem === item
}