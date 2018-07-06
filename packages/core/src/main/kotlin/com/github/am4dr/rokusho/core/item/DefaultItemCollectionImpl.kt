package com.github.am4dr.rokusho.core.item

class DefaultItemCollectionImpl(items: Collection<Item<*>>) : ItemCollection {

    private val byID = items.associateBy(Item<*>::id)

    override val ids: Set<ItemID> get() = byID.keys
    override val items: Set<Item<*>> get() = byID.values.toSet()

    override fun get(id: ItemID): Item<*>? = byID[id]
}