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
        return LibraryItemTag(base, recordTag.data)
    }
    override fun add(item: LibraryItem<out T>): LibraryItem<out T>? {
        val recordID = idConverter(item.id) ?: return null
        val record = Record(recordID, item.tags.mapTo(mutableSetOf()) { RecordTag(it.base.name, it.patch) })
        val rollbackRecord = getRecordRollback(recordID)
        metaDataRepository.add(record) ?: return null
        if (itemCollection.add(item.item) == null) {
            rollbackRecord()
            return null
        }
        return item
    }
    private fun getRecordRollback(id: RecordID): () -> Record? {
        val oldRecord = metaDataRepository.get(id)
        return {
            if (oldRecord == null) metaDataRepository.remove(id)
            else metaDataRepository.add(oldRecord)
        }
    }
    override fun remove(id: ItemID): LibraryItem<out T>? {
        val removedItem = get(id)
        val recordID = idConverter(id) ?: return null
        val rollbackRecord = getRecordRollback(recordID)
        if (metaDataRepository.has(recordID) && metaDataRepository.remove(recordID) == null) {
             return null
        }
        if (itemCollection.has(id) && itemCollection.remove(id) == null) {
            rollbackRecord()
            return null
        }
        return removedItem
    }

    override fun has(id: ItemID): Boolean = itemCollection.has(id)
}
