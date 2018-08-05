package com.github.am4dr.rokusho.core.item

class DefaultItemCollectionImpl<T : Any>(items: Collection<Item<T>>) : ItemCollection<T> {

    private val byID = items.associateBy(Item<*>::id)

    override val ids: Set<ItemID> get() = byID.keys
    override val items: Set<Item<T>> get() = byID.values.toSet()

    override fun get(id: ItemID): Item<T>? = byID[id]
}