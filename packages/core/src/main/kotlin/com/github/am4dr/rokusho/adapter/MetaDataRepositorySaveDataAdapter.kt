package com.github.am4dr.rokusho.adapter

import com.github.am4dr.rokusho.core.metadata.*
import com.github.am4dr.rokusho.core.util.DataObject
import com.github.am4dr.rokusho.old.savedata.SaveData
import com.github.am4dr.rokusho.old.core.library.ItemTag as OldItemTag
import com.github.am4dr.rokusho.old.core.library.Tag as OldTag
import com.github.am4dr.rokusho.old.savedata.Item as OldItem

fun SaveData?.toMetaDataRepository(): MetaDataRepository {
    if (this == null) return DefaultMetaDataRepositoryImpl()

    val convertedTags = tags.map(::OldTagToBaseTag).toSet()
    val baseTags = convertedTags.associateBy(BaseTag::name)
    val convertedRecords = items.map { OldItemToRecord(baseTags, it) }.toSet()
    return DefaultMetaDataRepositoryImpl(convertedTags, convertedRecords)
}

// TODO 情報を落とさないようにする
internal fun OldTagToBaseTag(tag: OldTag): BaseTag {
    val data = tag.data.entries.mapNotNull {
        val value = it.value as? String ?: return@mapNotNull null
        it.key to value
    }.toMap()
    return BaseTag(tag.id, data)
}

internal fun OldItemToRecord(baseTags: Map<BaseTag.Name, BaseTag>, item: OldItem): Record {
    val tags = item.data.tags.mapNotNull { tag ->
        val baseTag = baseTags[BaseTag.Name(tag.tag.id)] ?: return@mapNotNull null
        PatchedTag(baseTag, DataObject(tag.value?.let{ v -> mapOf("value" to v) } ?: mapOf()))
    }.toSet()
    return Record(Record.Key(item.id), tags)
}
