package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemID
import com.github.am4dr.rokusho.core.metadata.BaseTag

data class TaggedItem<T : Any>(val item: Item<T>, val tags: Set<BaseTag>) {

    val id: ItemID get() = item.id

    fun get(): T = item.get()

    override fun equals(other: Any?): Boolean = other is Item<*> && item == other.item
    override fun hashCode(): Int = item.hashCode()
}