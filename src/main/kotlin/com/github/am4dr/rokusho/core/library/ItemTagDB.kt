package com.github.am4dr.rokusho.core.library

interface ItemTagDB<T> {
    fun set(value: T, itemTags: List<ItemTag>)
    fun get(value: T): List<ItemTag>
}

class SimpleItemTagDB<T>(initial: Map<T, List<ItemTag>> = mapOf()) : ItemTagDB<T> {
    private val data = initial.toMutableMap()

    override fun set(value: T, itemTags: List<ItemTag>) {
        data[value] = itemTags
    }
    override fun get(value: T): List<ItemTag> = data.getOrDefault(value, listOf())
}