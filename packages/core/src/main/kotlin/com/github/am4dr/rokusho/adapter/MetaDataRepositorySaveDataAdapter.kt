package com.github.am4dr.rokusho.adapter

import com.github.am4dr.rokusho.core.metadata.*
import com.github.am4dr.rokusho.old.savedata.SaveData
import com.github.am4dr.rokusho.old.core.library.ItemTag as OldItemTag
import com.github.am4dr.rokusho.old.core.library.Tag as OldTag
import com.github.am4dr.rokusho.old.savedata.Item as OldItem

fun SaveData?.toMetaDataRepository(): MetaDataRepository {
    if (this == null) return DefaultMetaDataRepositoryImpl()

    val convertedTags = tags.map(::OldTagToTag).toSet()
    val convertedRecords = items.map(::OldItemToRecord).toSet()
    return DefaultMetaDataRepositoryImpl(convertedTags, convertedRecords)
}

// TODO 情報を落とさないようにする
internal fun OldTagToTag(tag: OldTag): Tag = Tag(tag.id, tag.data.entries
        .filter { it.value is String }
        .map { it.toPair() as Pair<String, String> }
        .toMap())

internal fun OldItemTagToRecordTag(itemTag: OldItemTag): RecordTag =
        RecordTag(itemTag.tag.id, itemTag.value?.let{ mapOf("value" to it) } ?: mapOf())

internal fun OldItemToRecord(item: OldItem): Record =
        Record(RecordID(item.id), item.data.tags.map(::OldItemTagToRecordTag).toSet())