package com.github.am4dr.rokusho.library2

import com.github.am4dr.rokusho.core.util.DataObject
import com.github.am4dr.rokusho.library2.internal.DataLocker
import com.github.am4dr.rokusho.library2.internal.ItemSet
import com.github.am4dr.rokusho.library2.internal.TagSet
import com.github.am4dr.rokusho.util.event.EventPublisher
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import com.github.am4dr.rokusho.util.event.EventSubscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


/**
 * [Item]の集合と[Tag]の集合およびそれらの関連を状態として持つ
 *
 * 次の制約を維持する
 * - [LibraryItem]が持つ[ItemTag]のから参照している[Tag]は[Library]上に存在しなければならない
 * - [Tag]の[Tag.name]はこの[Library]中で一意
 *
 * 初期データとして[Tag]のリストと[LibraryItem]のリストを与えることができ、それらが追加された際のイベントは区別できる。
 * [Tag]の集合の読み込みはすべて読み込むまで処理を待つが、[LibraryItem]の[Sequence]の読み込みは与えられたcontextのもとで行われる。
 * TODO contextをイベント用とアイテム読み込みようで共用することに問題はないか
 */
class Library private constructor(
    context: CoroutineContext,
    eventPublisherSupport: EventPublisherSupport<Event>,
    initialTags: Collection<Tag>,
    initialItemSequence: Sequence<LibraryItem<*>>
): EventPublisher<Library.Event> by eventPublisherSupport {

    constructor(
        context: CoroutineContext,
        initialTags: Collection<Tag> = setOf(),
        initialItemSequence: Sequence<LibraryItem<*>> = emptySequence()
    ) : this(context, EventPublisherSupport(context), initialTags, initialItemSequence)

    private val locker: DataLocker<Pair<TagSet, ItemSet>>

    private val loaderScope = CoroutineScope(context)
    init {
        val tags = TagSet(eventPublisherSupport)
        val items = ItemSet(eventPublisherSupport, tags)
        locker = DataLocker(tags to items)
        locker.write {
            initialTags.forEach(tags::load)
        }
        loaderScope.launch {
            initialItemSequence.forEach {
                locker.write { (_, items) ->
                    items.load(it)
                }
            }
        }
    }

    fun getAllItems(): Set<LibraryItem<*>> = locker.read { (_, items) -> items.asSet() }
    fun getAllTags(): Set<Tag> = locker.read { (tags) -> tags.asSet() }

    @ExperimentalCoroutinesApi
    fun update(item: LibraryItem<*>): LibraryItem<*>? = locker.write { (tags, items) ->
        if (!tags.hasAll(item.tags.map(ItemTag::tag))) return null
        if (!items.has(item)) return null

        items.update(item)
        return item
    }
    @ExperimentalCoroutinesApi
    fun update(tag: Tag): Tag? = locker.write { (tags, items) ->
        if (!tags.has(tag) || tags.isInvalidRename(tag)) return null

        tags.update(tag)
        items.asSet()
            .filter { it.has(tag) }
            .forEach { update(it.update(tag)) }
        return tag
    }

    @ExperimentalCoroutinesApi
    fun <T : Any> createItem(data: T): LibraryItem<T> = locker.write { (_, items) ->
        val item = LibraryItem(Item(data), setOf())
        items.add(item)
        return item
    }


    @ExperimentalCoroutinesApi
    fun createTag(data: TagData): Tag? = locker.write { (tags) ->
        val tag = Tag(data)
        if (tags.alreadyExists(tag)) return null
        tags.add(tag)
        return tag
    }

    fun createItemTagByName(name: String): ItemTag? = locker.write { (tags) ->
        tags.get(name)?.let {
            return ItemTag(it, DataObject())
        }
        val newTag = Tag(TagData(name, DataObject(mapOf("value" to name))))
        tags.add(newTag)
        return ItemTag(newTag, DataObject())
    }

    fun asData(): Data = locker.read { (tags, items) ->
        Data(tags = tags.asSet(), items = items.asSet())
    }

    fun getDataAndSubscribe(
        block: EventPublisher<Event>.(Data) -> EventSubscription
    ): EventSubscription = locker.read {
        block(this, asData())
    }


    data class Data(val tags: Set<Tag>, val items: Set<LibraryItem<*>>)

    sealed class Event {
        sealed class TagEvent(val tag: Tag) : Event() {
            class Loaded(tag: Tag) : TagEvent(tag)
            class Added(tag: Tag)  : TagEvent(tag)
            class Removed(tag: Tag) : TagEvent(tag)
            class Updated(tag: Tag) : TagEvent(tag)
        }
        sealed class ItemEvent(val item: LibraryItem<*>) : Event() {
            class Loaded(item: LibraryItem<*>) : ItemEvent(item)
            class Added(item: LibraryItem<*>)  : ItemEvent(item)
            class Removed(item: LibraryItem<*>) : ItemEvent(item)
            class Updated(item: LibraryItem<*>) : ItemEvent(item)
        }
    }
}