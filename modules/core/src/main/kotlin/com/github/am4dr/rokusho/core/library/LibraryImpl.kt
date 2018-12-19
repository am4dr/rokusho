package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.metadata.BaseTag
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository
import com.github.am4dr.rokusho.core.metadata.PatchedTag
import com.github.am4dr.rokusho.core.metadata.Record
import com.github.am4dr.rokusho.core.util.DataObject
import com.github.am4dr.rokusho.javafx.collection.toObservableList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlySetProperty
import javafx.beans.property.ReadOnlySetWrapper
import javafx.collections.FXCollections
import kotlin.reflect.KClass

// TODO 新しい種類のLibraryを実装できるように、ゆくゆくはこれの実装を整理して公開する必要がある
internal class LibraryImpl<T : Any>(
    override val name: String,
    override val shortName: String,
    override val type: KClass<T>,
    val itemCollection: ItemCollection<T>,
    val metaDataRepository: MetaDataRepository,
    val keyConverter: (Item.ID) -> Record.Key?
) : Library<T> {

    private val items = FXCollections.observableHashMap<Item.ID, LibraryItem<out T>>()
    override fun getItems(): ReadOnlyListProperty<LibraryItem<out T>> = ReadOnlyListWrapper(toObservableList(items)).readOnlyProperty

    private val tags = FXCollections.observableSet<LibraryItemTagTemplate>()
    override fun getTags(): ReadOnlySetProperty<LibraryItemTagTemplate> = ReadOnlySetWrapper(tags).readOnlyProperty
    init {
        itemCollection.ids.forEach(this::updateObservableItemMap)
        tags.addAll(metaDataRepository.getTags().map(::LibraryItemTagTemplateWrapper))
    }

    private fun updateObservableItemMap(id: Item.ID) {
        get(id)?.let { item -> items[id] = item }
    }

    private fun updateObservableTagSet(name: BaseTag.Name) {
        val tag = metaDataRepository.get(name) ?: return
        tags.add(LibraryItemTagTemplateWrapper(tag))
    }

    fun get(id: Item.ID): LibraryItem<out T>? {
        val item = itemCollection.get(id) ?: return null
        return LibraryItemImpl(this, item)
    }

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
            updateObservableItemMap(id)
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
