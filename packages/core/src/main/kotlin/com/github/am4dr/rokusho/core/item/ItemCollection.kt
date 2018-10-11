package com.github.am4dr.rokusho.core.item

interface ItemCollection<T : Any> {

    val ids: Set<ItemID>
    val items: Set<Item<out T>>

    fun get(id: ItemID): Item<out T>?
    fun has(id: ItemID): Boolean
}