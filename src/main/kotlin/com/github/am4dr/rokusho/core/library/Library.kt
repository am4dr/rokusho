package com.github.am4dr.rokusho.core.library

/**
 * [Item]のコレクション
 *
 * [Item]は[Item.key]をキーとして識別可能。
 */
interface Library<T> {
    fun addItem(key: T, tags: Iterable<ItemTag>)
    fun getItem(key: T): Item<T>?
    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
    fun removeItem(key: T)
    fun getOrCreateEmptyItemOf(key: T): Item<T>?
    fun getItemSet(list: Iterable<T>): ItemSet<T>
}