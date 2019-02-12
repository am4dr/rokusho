package com.github.am4dr.rokusho.library

import com.github.am4dr.rokusho.library.internal.DataLocker
import com.github.am4dr.rokusho.library.internal.ItemSet
import com.github.am4dr.rokusho.library.internal.TagSet
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
 * 初期データとして[Tag]のリストと[LibraryItem]のリストを与えることができ、それらが追加された際のイベントは通常の追加イベントとは区別できる。
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
    ) : this(context, EventPublisherSupport(context, 10000), initialTags, initialItemSequence)

    private val data: DataLocker<Pair<TagSet, ItemSet>> = initDataLocker(eventPublisherSupport)

    private val loaderScope = CoroutineScope(context)
    init {
        data.write { (tags) ->
            initialTags.forEach(tags::load)
        }
        loaderScope.launch {
            initialItemSequence.forEach {
                data.write { (_, items) ->
                    items.load(it)
                }
            }
        }
    }
    private fun initDataLocker(eventPublisherSupport: EventPublisherSupport<Event>): DataLocker<Pair<TagSet, ItemSet>> {
        val tags = TagSet(eventPublisherSupport)
        val items = ItemSet(eventPublisherSupport, tags)
        return DataLocker(tags to items)
    }

    fun getAllItems(): Set<LibraryItem<*>> = data.read { (_, items) -> items.asSet() }
    fun getAllTags(): Set<Tag> = data.read { (tags) -> tags.asSet() }

    @ExperimentalCoroutinesApi
    fun update(item: LibraryItem<*>): LibraryItem<*>? = data.write { (tags, items) ->
        if (!tags.hasAll(item.tags.map(ItemTag::tag))) return null
        if (!items.has(item)) return null

        items.update(item)
        return item
    }


    fun createItemTagByName(name: String): ItemTag? = data.write { (tags) ->
        tags.get(name)?.let { tag ->
            return ItemTag(tag)
        }
        val newTag = Tag(name, mapOf("value" to name))
        tags.add(newTag)
        return ItemTag(newTag)
    }


    fun asData(): Data = data.read { (tags, items) ->
        Data(tags = tags.asSet(), items = items.asSet())
    }

    fun getDataAndSubscribe(
        block: EventPublisher<Event>.(Data) -> EventSubscription
    ): EventSubscription = data.read {
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