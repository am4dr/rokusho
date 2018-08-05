package com.github.am4dr.rokusho.core.item

interface ItemCollection<T : Any> {

    val ids: Set<ItemID>
    val items: Set<Item<T>>

    fun get(id: ItemID): Item<T>?
}