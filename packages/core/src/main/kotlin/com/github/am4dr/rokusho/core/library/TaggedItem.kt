package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemID

data class TaggedItem<T : Any>(val item: Item<T>, val tags: Set<ItemTag>) {

    val id: ItemID get() = item.id

    fun get(): T = item.get()

    override fun equals(other: Any?): Boolean = other is TaggedItem<*> && id == other.id
    override fun hashCode(): Int = id.hashCode()
}