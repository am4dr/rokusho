package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.metadata.PatchedTag

data class LibraryItem<T : Any>(val item: Item<T>, val tags: Set<PatchedTag>) {

    val id: Item.ID get() = item.id

    fun get(): T = item.get()

    override fun equals(other: Any?): Boolean = other is LibraryItem<*> && id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "LibraryItem($id, ${get()}, tags=$tags)"
}