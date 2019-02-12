package com.github.am4dr.rokusho.library.internal

import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.Library.Event.ItemEvent.*
import com.github.am4dr.rokusho.library.LibraryItem
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * [Library]中で[LibraryItem]を扱うための補助的なクラス
 *
 * スレッドセーフではないので同期は外部で行う必要がある
 */
internal class ItemSet constructor(
    val eventPublisherSupport: EventPublisherSupport<in Library.Event.ItemEvent>,
    val tagSet: TagSet
) {

    private val items: MutableSet<LibraryItem<*>> = mutableSetOf()

    fun asSet(): Set<LibraryItem<*>> = items.toSet()

    fun has(item: LibraryItem<*>): Boolean = items.contains(item)

    @ExperimentalCoroutinesApi
    fun add(item: LibraryItem<*>) {
        items.add(item)
        eventPublisherSupport.dispatch(Added(item))
    }

    @ExperimentalCoroutinesApi
    fun update(item: LibraryItem<*>) {
        items.add(item)
        eventPublisherSupport.dispatch(Updated(item))
    }

    @ExperimentalCoroutinesApi
    fun load(item: LibraryItem<*>) {
        val newItemTags = item.tags.map { itemTag ->
            val currentTagInstance = tagSet.get(itemTag.tag) ?: return@map null.also {
                println("tag not found: ${itemTag.tag}")
            }
            itemTag.update(currentTagInstance)
        }.filterNotNullTo(mutableSetOf())
        val updatedItem = item.update(newItemTags)

        items.add(updatedItem)
        eventPublisherSupport.dispatch(Loaded(updatedItem))
    }
}