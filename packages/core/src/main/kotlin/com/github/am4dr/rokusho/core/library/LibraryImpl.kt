package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.item.ItemID
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository
import com.github.am4dr.rokusho.core.metadata.RecordID
import com.github.am4dr.rokusho.core.metadata.RecordTag
import com.github.am4dr.rokusho.core.metadata.BaseTag
import kotlin.reflect.KClass

class LibraryImpl<T : Any>(val metaDataRepository: MetaDataRepository,
                           val itemCollection: ItemCollection<T>,
                           val idConverter: (ItemID) -> RecordID?,
                           override val type: KClass<T>) : Library<T> {

    override fun getIDs(): Set<ItemID> = itemCollection.ids
    override fun get(id: ItemID): TaggedItem<out T>? {
        val item = itemCollection.get(id) ?: return null

        val record = idConverter(item.id)?.let { metaDataRepository.getRecord(it) }
        val recordTags = record?.tags ?: setOf()
        val tags = recordTags.map(::recordTagToTag).toSet()
        return TaggedItem(item, tags)
    }
    private fun recordTagToTag(recordTag: RecordTag): BaseTag {
        val tag = metaDataRepository.get(recordTag.base)
        val data = tag?.let { tag.data.merge(recordTag.data) } ?: recordTag.data
        return BaseTag(recordTag.base, data)
    }
    override fun getTags(): Set<BaseTag> = metaDataRepository.getTags()
}
