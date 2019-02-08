package com.github.am4dr.rokusho.library2.internal

import com.github.am4dr.rokusho.library2.Library
import com.github.am4dr.rokusho.library2.Tag
import com.github.am4dr.rokusho.library2.putOrReplaceEntity
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * [Library]中で[Tag]を扱うための補助的なクラス
 *
 * スレッドセーフではないので同期は外部で行う必要がある
 */
internal class TagSet(
    val eventPublisherSupport: EventPublisherSupport<in Library.Event.TagEvent>
) {

    private val tags: MutableSet<Tag> = mutableSetOf()

    fun asSet(): Set<Tag> = tags.toSet()

    @ExperimentalCoroutinesApi
    fun add(tag: Tag) {
        tags.add(tag)
        eventPublisherSupport.dispatch(Library.Event.TagEvent.Added(tag))
    }

    @ExperimentalCoroutinesApi
    fun update(tag: Tag) {
        tags.putOrReplaceEntity(tag)
        eventPublisherSupport.dispatch(Library.Event.TagEvent.Updated(tag))
    }

    @ExperimentalCoroutinesApi
    fun load(tag: Tag) {
        tags.putOrReplaceEntity(tag)
        eventPublisherSupport.dispatch(Library.Event.TagEvent.Loaded(tag))
    }

    fun has(tag: Tag): Boolean {
        return tags.any { tag.isSameEntity(it) }
    }
    fun hasAll(tagCollection: Collection<Tag>): Boolean =
        tagCollection.all { has(it) }

    fun isInvalidRename(tag: Tag): Boolean {
        val renameCollisionDetected = tags.any { it.isSameName(tag) && !it.isSameEntity(tag) }
        return renameCollisionDetected
    }

    fun alreadyExists(tag: Tag): Boolean {
        return tags.any { it.isSameEntity(tag) || it.isSameName(tag) }
    }

    fun get(tag: Tag): Tag? {
        return tags.find { it.isSameEntity(tag) }
    }

    fun get(name: String): Tag? {
        return tags.find { it.name == name }
    }
}