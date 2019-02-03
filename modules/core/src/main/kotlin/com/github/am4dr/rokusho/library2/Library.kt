package com.github.am4dr.rokusho.library2

import com.github.am4dr.rokusho.library2.internal.ItemSet
import com.github.am4dr.rokusho.library2.internal.TagSet
import com.github.am4dr.rokusho.util.event.EventPublisher
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import com.github.am4dr.rokusho.util.event.EventSubscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.CoroutineContext


/**
 * [Item]の集合と[Tag]の集合およびそれらの関連を状態として持つ
 *
 * 次の制約を維持する
 * - [LibraryItem]が持つ[ItemTag]のから参照している[Tag]は[Library]上に存在しなければならない
 * - [Tag]の[Tag.name]はこの[Library]中で一意
 *
 * TODO ロードのための機能とイベントを追加する
 */
class Library private constructor(
    eventPublisherSupport: EventPublisherSupport<Event>
): EventPublisher<Library.Event> by eventPublisherSupport {

    constructor(
        eventPublisherContext: CoroutineContext
    ) : this(EventPublisherSupport(eventPublisherContext))

    private val tags = TagSet(eventPublisherSupport)
    private val items = ItemSet(eventPublisherSupport)
    // 個別にロックするとロック順序の管理が面倒なのでひとまずまとめてロックする
    private val lock = ReentrantReadWriteLock()

    fun getAllItems(): Set<LibraryItem<*>> = lock.read { items.asSet() }
    fun getAllTags(): Set<Tag> = lock.read { tags.asSet() }


    @ExperimentalCoroutinesApi
    fun update(item: LibraryItem<*>): LibraryItem<*>? = lock.write {
        if (!tags.hasAll(item.tags.map(ItemTag::tag))) return null
        if (!items.has(item)) return null

        items.update(item)
        return item
    }
    @ExperimentalCoroutinesApi
    fun update(tag: Tag): Tag? = lock.write {
        if (!tags.has(tag) || tags.isInvalidRename(tag)) return null

        tags.update(tag)
        items.asSet()
            .filter { it.has(tag) }
            .forEach { update(it.update(tag)) }
        return tag
    }

    @ExperimentalCoroutinesApi
    fun <T : Any> createItem(data: T): LibraryItem<T> = lock.write {
        val item = LibraryItem(Item(data), setOf())
        items.add(item)
        return item
    }


    @ExperimentalCoroutinesApi
    fun createTag(data: TagData): Tag? = lock.write {
        val tag = Tag(data)
        if (tags.alreadyExists(tag)) return null
        tags.add(tag)
        return tag
    }


    fun asData(): Data = lock.read {
        Data(tags = tags.asSet(), items = items.asSet())
    }

    fun getDataAndSubscribe(
        block: EventPublisher<Event>.(Data) -> EventSubscription
    ): EventSubscription = lock.read {
        block(this, asData())
    }


    data class Data(val tags: Set<Tag>, val items: Set<LibraryItem<*>>)

    sealed class Event {
        sealed class TagEvent(val tag: Tag) : Event() {
            class Loaded(tag: Tag) : TagEvent(tag)
            class Added(tag: Tag) : TagEvent(tag)
            class Removed(tag: Tag) : TagEvent(tag)
            class Updated(tag: Tag) : TagEvent(tag)
        }
        sealed class ItemEvent(val item: LibraryItem<*>) : Event() {
            class Loaded(item: LibraryItem<*>) : ItemEvent(item)
            class Added(item: LibraryItem<*>) : ItemEvent(item)
            class Removed(item: LibraryItem<*>) : ItemEvent(item)
            class Updated(item: LibraryItem<*>) : ItemEvent(item)
        }
    }
}