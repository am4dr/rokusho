package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.item.ItemID
import com.github.am4dr.rokusho.core.metadata.*
import kotlin.reflect.KClass

class LibraryImpl<T : Any>(val metaDataRepository: MetaDataRepository,
                           val itemCollection: ItemCollection<T>,
                           val idConverter: (ItemID) -> RecordID?,
                           override val type: KClass<T>) : Library<T> {

    override fun getTags(): Set<BaseTag> = metaDataRepository.getTags()
    override fun getIDs(): Set<ItemID> = itemCollection.ids
    override fun get(id: ItemID): LibraryItem<out T>? {
        val item = itemCollection.get(id) ?: return null

        val record = idConverter(item.id)?.let { metaDataRepository.get(it) }
        val recordTags = record?.tags ?: setOf()
        val tags = recordTags.mapNotNullTo(mutableSetOf(), ::recordTagToItemTag)
        return LibraryItem(item, tags)
    }
    private fun recordTagToItemTag(recordTag: RecordTag): LibraryItemTag? {
        val base = metaDataRepository.get(recordTag.base) ?: return null
        return LibraryItemTag(base, recordTag.patchData)
    }

    override fun update(id: ItemID, tags: Set<LibraryItemTag>): Boolean {
        if (!itemCollection.has(id)) return false
        val recordID = idConverter(id) ?: return false
        val recordTags = tags.mapTo(mutableSetOf()) { RecordTag(it.base.name, it.patch) }
        val record = Record(recordID, recordTags)
        metaDataRepository.add(record) ?: return false
        return true
    }
    override fun has(id: ItemID): Boolean = itemCollection.has(id)
}
