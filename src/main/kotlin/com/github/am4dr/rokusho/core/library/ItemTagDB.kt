package com.github.am4dr.rokusho.core.library

interface ItemTagDB<T> {
    fun getKeys(): Set<T>
    fun get(key: T): List<ItemTag>
    fun set(key: T, itemTags: List<ItemTag>)
    fun remove(key: T)
}
