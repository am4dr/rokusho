package com.github.am4dr.rokusho.core.item

import kotlin.reflect.KClass

data class Item<T : Any>(val id: ItemID, val item: T) {

    val type: KClass<out T> = item::class

    override fun equals(other: Any?): Boolean = other is Item<*> && other.id == id
    override fun hashCode(): Int = id.hashCode()
}