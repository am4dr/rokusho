package com.github.am4dr.rokusho.core.library

/**
 * [Item]のコレクション
 *
 * [Item]は[Item.value]をキーとして識別可能。
 */
interface Library<T> {
    fun getItemOf(value: T): Item<T>?
    fun getOrCreateItemOf(value: T): Item<T>?
    fun getItemSet(list: Iterable<T>): ItemSet<T>
    fun addItem(item: Item<T>): Boolean
    fun removeItem(item: Item<T>): Boolean
    fun updateItemTags(itemValue: T, itemTags: List<ItemTag>)
}