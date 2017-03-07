package com.github.am4dr.rokusho.core

data class ImageMetaData(val tags: List<Tag> = listOf()) {
    fun toDumpStructure(): Map<String, Any> = mapOf("tags" to tags.map { it.id to it.data }.toMap())
}