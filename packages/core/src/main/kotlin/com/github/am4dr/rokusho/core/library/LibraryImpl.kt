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

class LibraryImpl<T : Any>(override val name: String,
                           override val shortName: String,
                           override val type: KClass<T>,
                           val itemCollection: ItemCollection<T>,
                           val metaDataRepository: MetaDataRepository,
                           val keyConverter: (Item.ID) -> Record.Key?) : Library<T> {

    private val items = FXCollections.observableHashMap<Item.ID, LibraryItem<out T>>()
    override fun getItems(): ReadOnlyListProperty<LibraryItem<out T>> = ReadOnlyListWrapper(toObservableList(items)).readOnlyProperty

    private val tags = FXCollections.observableSet<BaseTag>()
    override fun getTags(): ReadOnlySetProperty<BaseTag> = ReadOnlySetWrapper(tags).readOnlyProperty
    init {
        getIDs().forEach(this::updateObservableItemMap)
        tags.addAll(metaDataRepository.getTags())
    }

    private fun updateObservableItemMap(id: Item.ID) {
        get(id)?.let { item -> items[id] = item }
    }

    private fun updateObservableTagSet(name: BaseTag.Name) {
        val tag = metaDataRepository.get(name) ?: return
        tags.add(tag)
    }

    override fun getIDs(): Set<Item.ID> = itemCollection.ids
    override fun get(id: Item.ID): LibraryItem<out T>? {
        val item = itemCollection.get(id) ?: return null
        val recordKey = keyConverter(item.id)
        val record = recordKey?.let { metaDataRepository.get(it) }
        return LibraryItem(item, record?.tags ?: setOf())
    }

    override fun update(id: Item.ID, tags: Set<PatchedTag>): Boolean {
        if (!itemCollection.has(id)) return false
        val recordID = keyConverter(id) ?: return false
        val record = Record(recordID, tags)
        val result = metaDataRepository.add(record) != null
        if (result) {
            updateObservableItemMap(id)
        }
        return result
    }
    override fun has(id: Item.ID): Boolean = itemCollection.has(id)

    override fun parseTag(text: String): PatchedTag? {
        val asBaseTagName = BaseTag.Name(text)
        val baseTag = metaDataRepository.get(asBaseTagName) ?: BaseTag(text, mapOf())
        return PatchedTag(baseTag, DataObject())
    }
}
