package com.github.am4dr.rokusho.adapter

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.datastore.NullDataStore
import com.github.am4dr.rokusho.core.metadata.*
import com.github.am4dr.rokusho.core.util.DataObject
import com.github.am4dr.rokusho.old.savedata.ItemMetaData
import com.github.am4dr.rokusho.old.savedata.SaveData
import com.github.am4dr.rokusho.old.core.library.ItemTag as OldItemTag
import com.github.am4dr.rokusho.old.core.library.Tag as OldTag
import com.github.am4dr.rokusho.old.savedata.Item as OldItem

fun toMetaDataRepository(data: SaveData?, store: DataStore<MetaDataRepository> = NullDataStore()): MetaDataRepository {
    if (data == null) return DefaultMetaDataRepositoryImpl(setOf(), setOf(), store)

    val convertedTags = data.tags.map(::oldTagToBaseTag).toSet()
    val baseTags = convertedTags.associateBy(BaseTag::name)
    val convertedRecords = data.items.map { oldItemToRecord(baseTags, it) }.toSet()
    return DefaultMetaDataRepositoryImpl(convertedTags, convertedRecords, store)
}
internal fun oldTagToBaseTag(tag: OldTag): BaseTag {
    val data = tag.data.entries.mapNotNull {
        val value = it.value as? String ?: return@mapNotNull null
        it.key to value
    }.toMap(mutableMapOf())
    data["type"] = tag.type.toString().toLowerCase()
    return BaseTag(tag.id, data)
}
internal fun oldItemToRecord(baseTags: Map<BaseTag.Name, BaseTag>, item: OldItem): Record {
    val tags = item.data.tags.mapNotNull { tag ->
        val baseTag = baseTags[BaseTag.Name(tag.tag.id)] ?: return@mapNotNull null
        PatchedTag(baseTag, DataObject(tag.value?.let{ v -> mapOf("value" to v) } ?: mapOf()))
    }.toSet()
    return Record(Record.Key(item.id), tags)
}

fun toSaveData(metadataRepository: MetaDataRepository): SaveData {
    val oldTags = metadataRepository.getTags().map(::baseTagToOldTag)
    val idToTag = oldTags.associateBy(OldTag::id)
    val records = metadataRepository.getRecords()
    return SaveData(oldTags, records.map { recordToOldItem(idToTag::get, it) })
}
internal fun baseTagToOldTag(tag: BaseTag): OldTag {
    val type = tag.data["type"] ?.let { OldTag.Type.from(it) } ?: OldTag.Type.TEXT
    return OldTag(tag.name.name, type, tag.data.remove("type").entries.toMap())
}
internal fun recordToOldItem(idToTag: (String) -> OldTag?, record: Record): OldItem {
    val oldItemTags = record.tags.mapNotNull {
        val value = it.patchData["value"] ?: return@mapNotNull null
        val tag = idToTag(it.base.name.name) ?: return@mapNotNull null
        OldItemTag(tag, value)
    }
    return OldItem(record.key.id, ItemMetaData(oldItemTags))
}