package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item

internal class LibraryItemImpl<T : Any>(
    val library: LibraryImpl<T>,
    item: Item<out T>
) : LibraryItem<T> {

    val item: T = item.get()
    val itemID: Item.ID = item.id
    override fun get(): T = item
    override fun getTags(): Set<LibraryItemTag> = library.getItemTags(itemID) ?: setOf()

    override fun updateTags(tags: Set<LibraryItemTag>): LibraryItem<out T>? {
        val result = library.updateItemTags(itemID, tags)
        if (!result) return null

        return library.get(itemID)
    }

    override fun equals(other: Any?): Boolean = other is LibraryItemImpl<*> && itemID == other.itemID
    override fun hashCode(): Int = itemID.hashCode()
    override fun toString(): String = "LibraryItem($itemID, ${get()}, tags=${getTags()})"
}