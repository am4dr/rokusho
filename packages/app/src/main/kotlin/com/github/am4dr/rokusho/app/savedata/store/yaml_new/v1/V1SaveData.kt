package com.github.am4dr.rokusho.app.savedata.store.yaml_new.v1

import com.github.am4dr.rokusho.app.savedata.ItemMetaData
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.Item
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.SaveData
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Tag

data class TagEntry(val id: String, val data: Map<String, Any> = mapOf())
data class ItemEntry(val id: String, val tags: List<ItemTagEntry> = listOf())
data class ItemTagEntry(val id: String, val data: Map<String, Any> = mapOf())

data class V1SaveData(val tags: List<TagEntry> = listOf(), val items: List<ItemEntry> = listOf()) {

    companion object {
        fun from(data: SaveData): V1SaveData {
            val tags = data.tags.map { TagEntry(it.id, it.data) }
            val items = data.items.map { (id, meta) ->
                val entries = meta.tags.map {
                    val tagData = mutableMapOf<String, Any>()
                    it.value?.let { tagData.put("value", it) }
                    ItemTagEntry(it.tag.id, tagData)
                }
                ItemEntry(id, entries)
            }
            return V1SaveData(tags, items)
        }
    }
    fun toSaveData(): SaveData {
        val sdTags = tags.distinctBy(TagEntry::id).map { Tag(it.id, detectTagType(it.data), it.data) }
        val sdTagMap = sdTags.map { it.id to it }.toMap()
        val sdItems = items.distinctBy(ItemEntry::id).map { Item(it.id, it.tags.toItemMetaData(sdTagMap)) }
        return SaveData(SaveData.Version.VERSION_1, sdTags, sdItems)
    }

    private fun List<ItemTagEntry>.toItemMetaData(tags: Map<String, Tag>): ItemMetaData =
            ItemMetaData(filter { tags.containsKey(it.id) }
                    .map { ItemTag(tags[it.id]!!, it.data["value"] as? String) })
}
