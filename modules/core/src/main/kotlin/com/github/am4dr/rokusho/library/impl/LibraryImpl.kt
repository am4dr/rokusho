package com.github.am4dr.rokusho.library.impl

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.metadata.BaseTag
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository
import com.github.am4dr.rokusho.core.metadata.PatchedTag
import com.github.am4dr.rokusho.core.metadata.Record
import com.github.am4dr.rokusho.core.util.DataObject
import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.LibraryItem
import com.github.am4dr.rokusho.library.LibraryItemTag
import com.github.am4dr.rokusho.library.LibraryItemTagTemplate
import com.github.am4dr.rokusho.util.event.EventPublisher
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

// TODO 新しい種類のLibraryを実装できるように、ゆくゆくはこれの実装を整理して公開する必要がある
// TODO テンプレート側のタグが追加されたときの処理を実装する
internal class LibraryImpl<T : Any>(
    override val name: String,
    override val shortName: String,
    override val type: KClass<T>,
    val itemCollection: ItemCollection<T>,
    val metaDataRepository: MetaDataRepository,
    val keyConverter: (Item.ID) -> Record.Key?,
    private val events: EventPublisherSupport<Library.Event> = EventPublisherSupport(Dispatchers.Default)
) : Library<T>, EventPublisher<Library.Event> by events {

    private val items = mutableMapOf<Item.ID, LibraryItem<out T>>()
    override fun getItems(): List<LibraryItem<out T>> = items.values.toList()

    private val tags = mutableSetOf<LibraryItemTagTemplate>()
    override fun getTags(): Set<LibraryItemTagTemplate> = tags.toSet()

    init {
        itemCollection.ids.forEach {
            getOrCreateCache(it)
        }
        tags.addAll(metaDataRepository.getTags().map(::LibraryItemTagTemplateWrapper))
    }

    private fun updateObservableTagSet(name: BaseTag.Name) {
        val tag = metaDataRepository.get(name) ?: return
        tags.add(LibraryItemTagTemplateWrapper(tag))
    }

    private fun getOrCreateCache(id: Item.ID): LibraryItem<out T>? {
        if (!itemCollection.has(id)) return null
        return items.getOrPut(id) {
            val item = itemCollection.get(id) ?: return null
            LibraryItemImpl(this, item)
        }
    }

    fun get(id: Item.ID): LibraryItem<out T>? = getOrCreateCache(id)

    fun getItemTags(id: Item.ID): Set<LibraryItemTag>? {
        val item = itemCollection.get(id) ?: return null
        val recordKey = keyConverter(item.id)
        val record = recordKey?.let { metaDataRepository.get(it) }
        return record?.tags?.mapTo(mutableSetOf(), ::LibraryItemTagWrapper) ?: setOf()
    }

    fun updateItemTags(id: Item.ID, tags: Set<LibraryItemTag>): Boolean {
        if (!itemCollection.has(id)) return false
        val recordID = keyConverter(id) ?: return false
        val patchedTags = extractPatchedTags(tags) ?: return false
        val added = metaDataRepository.add(Record(recordID, patchedTags))
        val succeeded = added != null
        if (succeeded) {
            get(id)?.let { item ->
                events.dispatch(Library.Event.UpdateItem(item))
            }
        }
        return succeeded
    }
    private fun extractPatchedTags(tags: Set<LibraryItemTag>): Set<PatchedTag>? {
        val patchedTags = mutableSetOf<PatchedTag>()
        for (tag in tags) {
            if (tag !is LibraryItemTagWrapper) return null
            patchedTags.add(tag.tag)
        }
        return patchedTags
    }

    override fun contains(item: LibraryItem<*>): Boolean =
        item is LibraryItemImpl && item.library === this

    override fun parseItemTag(text: String): LibraryItemTag? {
        return parseAsTextTag(text)
    }
    private fun parseAsTextTag(text: String): LibraryItemTag? {
        val baseTagName = BaseTag.Name(text)
        val baseTag = metaDataRepository.get(baseTagName) ?: BaseTag(text, mapOf())
        return LibraryItemTagWrapper(PatchedTag(baseTag, DataObject()))
    }
}
