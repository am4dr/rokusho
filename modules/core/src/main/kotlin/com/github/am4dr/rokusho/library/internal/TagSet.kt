package com.github.am4dr.rokusho.library.internal

import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.Tag
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

    private val tags: MutableMap<Tag, Tag> = mutableMapOf()
    private val byName: MutableMap<String, Tag> = mutableMapOf()

    fun asSet(): Set<Tag> = tags.values.toSet()

    @ExperimentalCoroutinesApi
    fun add(tag: Tag) {
        tags[tag] = tag
        byName[tag.name] = tag
        eventPublisherSupport.dispatch(Library.Event.TagEvent.Added(tag))
    }

    @ExperimentalCoroutinesApi
    fun update(tag: Tag) {
        tags[tag] = tag
        byName[tag.name] = tag
        eventPublisherSupport.dispatch(Library.Event.TagEvent.Updated(tag))
    }

    @ExperimentalCoroutinesApi
    fun load(tag: Tag) {
        tags[tag] = tag
        byName[tag.name] = tag
        eventPublisherSupport.dispatch(Library.Event.TagEvent.Loaded(tag))
    }

    fun has(tag: Tag): Boolean = tags.contains(tag)
    fun hasAll(tagCollection: Collection<Tag>): Boolean =
        tagCollection.all { tags.contains(it) }

    fun isInvalidRename(tag: Tag): Boolean {
        val sameName = get(tag.name) ?: return false
        return !sameName.isSameEntity(tag)
    }

    fun alreadyExists(tag: Tag): Boolean {
        return tags.contains(tag) || byName.contains(tag.name)
    }

    fun get(tag: Tag): Tag? = tags[tag]

    fun get(name: String): Tag? = byName[name]
}