package com.github.am4dr.rokusho.core.library

class DefaultItemTagDB<T>(initial: Map<T, List<ItemTag>> = mapOf()) : ItemTagDB<T> {
    private val data = initial.toMutableMap()

    override fun getKeys(): Set<T> = data.keys.toSet()
    override fun get(key: T): List<ItemTag> = data.getOrDefault(key, listOf())
    override fun set(key: T, itemTags: List<ItemTag>) {
        data[key] = itemTags
    }
    override fun remove(key: T) {
        data.remove(key)
    }
}