package com.github.am4dr.rokusho.datastore.savedata.yaml.v1

import com.github.am4dr.rokusho.datastore.savedata.Item
import com.github.am4dr.rokusho.datastore.savedata.ItemTag
import com.github.am4dr.rokusho.datastore.savedata.SaveData
import com.github.am4dr.rokusho.datastore.savedata.Tag

data class TagEntry(val id: String, val data: Map<String, Any> = mapOf())
data class ItemEntry(val id: String, val tags: List<ItemTagEntry> = listOf())
data class ItemTagEntry(val id: String, val data: Map<String, Any> = mapOf())

data class V1SaveData(val tags: List<TagEntry> = listOf(), val items: List<ItemEntry> = listOf()) {

    companion object {
        fun from(data: SaveData): V1SaveData {
            val tags = data.tags.map { TagEntry(it.id, it.data) }
            val items = data.items.map { (id, itemTags) ->
                val entries = itemTags.map { itemTag ->
                    val tagData = mutableMapOf<String, Any>()
                    itemTag.value?.let { tagData.put("value", it) }
                    ItemTagEntry(itemTag.tag.id, tagData)
                }
                ItemEntry(id, entries)
            }
            return V1SaveData(tags, items)
        }


        fun detectTagType(data: Map<String, Any>): Tag.Type {
            val type = data["type"] as? String ?: return Tag.Type.TEXT
            return Tag.Type.from(type)
        }
    }
    fun toSaveData(): SaveData {
        val sdTags = tags.distinctBy(TagEntry::id).map {
            Tag(
                it.id,
                detectTagType(it.data),
                it.data
            )
        }
        val sdTagMap = sdTags.map { it.id to it }.toMap()
        val sdItems = items
            .distinctBy(ItemEntry::id)
            .map { toItem(it, sdTagMap) }
        return SaveData(sdTags, sdItems)
    }
    private fun toItem(itemEntry: ItemEntry, tags: Map<String, Tag>): Item {
        val itemTags = itemEntry.tags
            .filter { tags.containsKey(it.id) }
            .map {
                ItemTag(
                    tags[it.id]!!,
                    it.data["value"] as? String
                )
            }
        return Item(itemEntry.id, itemTags)
    }
}