package com.github.am4dr.rokusho.core.item

interface ItemCollection {

    val ids: Set<ItemID>
    val items: Set<Item<*>>

    fun get(id: ItemID): Item<*>?
}