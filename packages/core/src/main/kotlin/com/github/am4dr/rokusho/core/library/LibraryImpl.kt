package com.github.am4dr.rokusho.core.library

import com.github.am4dr.rokusho.core.item.Item
import com.github.am4dr.rokusho.core.item.ItemCollection
import com.github.am4dr.rokusho.core.metadata.BaseTag
import com.github.am4dr.rokusho.core.metadata.MetaDataRepository
import com.github.am4dr.rokusho.core.metadata.PatchedTag
import com.github.am4dr.rokusho.core.metadata.Record
import kotlin.reflect.KClass

class LibraryImpl<T : Any>(val metaDataRepository: MetaDataRepository,
                           val itemCollection: ItemCollection<T>,
                           val keyConverter: (Item.ID) -> Record.Key?,
                           override val type: KClass<T>) : Library<T> {

    override fun getTags(): Set<BaseTag> = metaDataRepository.getTags()
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
        return metaDataRepository.add(record) != null
    }
    override fun has(id: Item.ID): Boolean = itemCollection.has(id)
}
