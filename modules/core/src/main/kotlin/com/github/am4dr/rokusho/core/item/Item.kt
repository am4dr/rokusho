package com.github.am4dr.rokusho.core.item

import kotlin.reflect.KClass

data class Item<T : Any>(val id: ID, val item: T) {

    val type: KClass<out T> = item::class

    fun get(): T = item

    override fun equals(other: Any?): Boolean = other is Item<*> && other.id == id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String {
        return "Item($id, $item)"
    }

    data class ID(val id: String) {
        override fun toString(): String = "ID($id)"
    }
}