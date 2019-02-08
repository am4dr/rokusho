package com.github.am4dr.rokusho.library2.internal

import com.github.am4dr.rokusho.library2.Library
import com.github.am4dr.rokusho.library2.LibraryItem
import com.github.am4dr.rokusho.library2.putOrReplaceEntity
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

    fun has(item: LibraryItem<*>): Boolean =
        items.any { it.isSameEntity(item) }

    @ExperimentalCoroutinesApi
    fun add(item: LibraryItem<*>) {
        items.add(item)
        eventPublisherSupport.dispatch(Library.Event.ItemEvent.Added(item))
    }

    @ExperimentalCoroutinesApi
    fun update(item: LibraryItem<*>) {
        items.putOrReplaceEntity(item)
        eventPublisherSupport.dispatch(Library.Event.ItemEvent.Updated(item))
    }

    @ExperimentalCoroutinesApi
    fun load(item: LibraryItem<*>) {
        val newItemTags = item.tags.map { itemTag ->
            val currentTagInstance = tagSet.get(itemTag.tag) ?: return@map null
            itemTag.update(currentTagInstance)
        }.filterNotNullTo(mutableSetOf())
        val updatedItem = item.update(newItemTags)

        items.putOrReplaceEntity(updatedItem)
        eventPublisherSupport.dispatch(Library.Event.ItemEvent.Loaded(updatedItem))
    }
}