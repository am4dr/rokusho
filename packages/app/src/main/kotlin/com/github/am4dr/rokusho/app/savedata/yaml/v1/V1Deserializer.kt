package com.github.am4dr.rokusho.app.savedata.yaml.v1


fun parse(data: Map<*, *>): V1SaveData = V1SaveData(extractTags(data), extractItems(data))


fun extractTags(data: Map<*, *>): List<TagEntry> {
    val tagsMap = data["tags"] as? Map<*, *> ?: return listOf()
    return tagsMap.entries.mapNotNull(::parseTagEntry)
}

fun parseTagEntry(entry: Map.Entry<*, *>): TagEntry? {
    val id = entry.key as? String ?: return null
    val data = entry.value as? Map<*, *> ?: mapOf<String, Any>()
    val typeCheckedData = data.entries
            .filter { it.key is String && it.value != null }
            .map { it.key as String to it.value as Any }
            .toMap()
    return TagEntry(id, typeCheckedData)
}


fun extractItems(data: Map<*, *>): List<ItemEntry> {
    val itemsMap = data["metaData"] as? Map<*, *> ?: return listOf()
    return itemsMap.mapNotNull(::parseItemEntry).toList()
}

fun parseItemEntry(entry: Map.Entry<*, *>): ItemEntry? {
    val id = entry.key as? String ?: return null
    val data = entry.value as? Map<*, *> ?: mapOf<Any, Any>()
    val tags = data["tags"] as? Map<* ,*> ?: mapOf<String, Any>()
    return ItemEntry(id, tags.mapNotNull(::parseItemTagEntry).toList())
}

fun parseItemTagEntry(entry: Map.Entry<*, *>): ItemTagEntry? {
    val id = entry.key as? String ?: return null
    val data = entry.value as? Map<*, *> ?: mapOf<String, Any>()
    val checkedData = data.entries
            .filter { it.key is String && it.value != null }
            .map { it.key as String to it.value as Any }
            .toMap()
    return ItemTagEntry(id, checkedData)
}