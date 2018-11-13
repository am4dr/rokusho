package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.LibraryItem
import com.github.am4dr.rokusho.core.metadata.PatchedTag

class LibraryItemItemViewModel(
    private val library: Library<*>,
    private val libraryItem: LibraryItem<*>
) : ItemViewModel<Any> {

    override val item: Any get() = libraryItem.get()
    override val tags: List<PatchedTag> get() = libraryItem.tags.toList()

    override fun parseTagString(string: String): PatchedTag? =
        library.parseTag(string)

    override fun updateTags(tags: List<PatchedTag>) {
        library.update(libraryItem.id, tags.toSet())
    }
}