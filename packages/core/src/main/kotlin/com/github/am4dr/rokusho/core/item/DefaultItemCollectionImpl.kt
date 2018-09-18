package com.github.am4dr.rokusho.core.item

class DefaultItemCollectionImpl<T : Any>(items: Collection<Item<out T>>) : ItemCollection<T> {

    private val byID = items.associateByTo(mutableMapOf(), Item<*>::id)

    override val ids: Set<ItemID> get() = byID.keys
    override val items: Set<Item<out T>> get() = byID.values.toSet()

    override fun get(id: ItemID): Item<out T>? = byID[id]
    override fun add(item: Item<out T>): Item<out T>? = item.also { if (byID[it.id]?.get() != item.get()) byID[it.id] = it }
    override fun remove(id: ItemID): Item<out T>? = byID.remove(id)
    override fun has(id: ItemID): Boolean = byID.containsKey(id)
}