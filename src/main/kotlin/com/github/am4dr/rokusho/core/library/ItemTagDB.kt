package com.github.am4dr.rokusho.core.library

interface ItemTagDB<T> {
    fun get(key: T): List<ItemTag>
    fun set(key: T, itemTags: List<ItemTag>)
    fun remove(key: T)
}

class SimpleItemTagDB<T>(initial: Map<T, List<ItemTag>> = mapOf()) : ItemTagDB<T> {
    private val data = initial.toMutableMap()

    override fun get(key: T): List<ItemTag> = data.getOrDefault(key, listOf())
    override fun set(key: T, itemTags: List<ItemTag>) {
        data[key] = itemTags
    }
    override fun remove(key: T) {
        data.remove(key)
    }
}