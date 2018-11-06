package com.github.am4dr.rokusho.core.item

interface ItemCollection<T : Any> {

    val ids: Set<Item.ID>
    val items: Set<Item<out T>>

    fun get(id: Item.ID): Item<out T>?
    fun has(id: Item.ID): Boolean
}