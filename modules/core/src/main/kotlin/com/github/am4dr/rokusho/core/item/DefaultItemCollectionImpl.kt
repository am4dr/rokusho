package com.github.am4dr.rokusho.core.item

class DefaultItemCollectionImpl<T : Any>(items: Collection<Item<out T>>) : ItemCollection<T> {

    private val byID = items.associateByTo(mutableMapOf(), Item<*>::id)

    override val ids: Set<Item.ID> get() = byID.keys
    override val items: Set<Item<out T>> get() = byID.values.toSet()

    override fun get(id: Item.ID): Item<out T>? = byID[id]
    override fun has(id: Item.ID): Boolean = byID.containsKey(id)
}