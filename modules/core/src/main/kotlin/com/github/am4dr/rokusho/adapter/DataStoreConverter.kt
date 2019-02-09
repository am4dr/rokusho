// TODO ちょっとあとで見返すときのために残してあるだけなので不要になりしだい消す
//package com.github.am4dr.rokusho.adapter
//
//import com.github.am4dr.rokusho.core.datastore.DataStore
//import com.github.am4dr.rokusho.core.datastore.NullDataStore
//import com.github.am4dr.rokusho.core.datastore.savedata.SaveData
//import com.github.am4dr.rokusho.core.metadata.*
//import com.github.am4dr.rokusho.library.DataObject
//import com.github.am4dr.rokusho.core.datastore.savedata.Item as OldItem
//import com.github.am4dr.rokusho.core.datastore.savedata.ItemTag as OldItemTag
//import com.github.am4dr.rokusho.core.datastore.savedata.Tag as OldTag
//
//class DataStoreConverter(private val store: DataStore<SaveData>) : DataStore<MetaDataRepository> {
//
//    override fun save(data: MetaDataRepository) {
//        store.save(toSaveData(data))
//    }
//
//    override fun load(): MetaDataRepository? {
//        return toMetaDataRepository(store.load(), this)
//    }
//}
//
//fun toMetaDataRepository(data: SaveData?, store: DataStore<MetaDataRepository> = NullDataStore()): MetaDataRepository {
//    if (data == null) return DefaultMetaDataRepositoryImpl(setOf(), setOf(), store)
//
//    val convertedTags = data.tags.map(::oldTagToBaseTag).toSet()
//    val baseTags = convertedTags.associateBy(BaseTag::name)
//    val convertedRecords = data.items.map {
//        Record(it.id, it.tags.mapNotNull { tag -> oldItemTagToPatchedTag(baseTags, tag) }.toSet())
//    }.toSet()
//    return DefaultMetaDataRepositoryImpl(convertedTags, convertedRecords, store)
//}
//internal fun oldTagToBaseTag(tag: OldTag): BaseTag {
//    val data = tag.data.entries.mapNotNull {
//        val value = it.value as? String ?: return@mapNotNull null
//        it.key to value
//    }.toMap(mutableMapOf())
//    data["type"] = tag.type.toString().toLowerCase()
//    return BaseTag(tag.id, data)
//}
//internal fun oldItemTagToPatchedTag(baseTags: Map<BaseTag.Name, BaseTag>, itemTag: OldItemTag): PatchedTag? {
//    val baseTag = baseTags[BaseTag.Name(itemTag.tag.id)] ?: return null
//    return PatchedTag(baseTag, DataObject(itemTag.value?.let{ v -> mapOf("value" to v) } ?: mapOf()))
//}
//
//fun toSaveData(metadataRepository: MetaDataRepository): SaveData {
//    val oldTags = metadataRepository.getTags().map(::baseTagToOldTag)
//    val idToTag = oldTags.associateBy(OldTag::id)
//    val records = metadataRepository.getRecords()
//    return SaveData(oldTags, records.map {
//        OldItem(it.key.id, it.tags.mapNotNull { tag -> patchedTagToOldItemTag(idToTag::get, tag) })
//    })
//}
//internal fun baseTagToOldTag(tag: BaseTag): OldTag {
//    val type = tag.data["type"] ?.let { OldTag.Type.from(it) } ?: OldTag.Type.TEXT
//    return OldTag(tag.name.name, type, tag.data.remove("type").asMap())
//}
//internal fun patchedTagToOldItemTag(idToTag: (String) -> OldTag?, tag: PatchedTag): OldItemTag? {
//    val oldTag = idToTag(tag.base.name.name) ?: return null
//    return OldItemTag(oldTag, tag.patchData["value"])
//}