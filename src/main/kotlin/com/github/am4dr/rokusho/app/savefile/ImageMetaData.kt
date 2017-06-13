package com.github.am4dr.rokusho.app.savefile

import com.github.am4dr.rokusho.core.library.Tag

data class ImageMetaData(val tags: List<Tag> = listOf()) {
    // Dump method of SnakeYAML converts same objects into a YAML anchor and references.
    // To avoid that, if the 'tags' is empty, create a new empty mutable map.
    fun toDumpStructure(): Map<String, Any> =
            mapOf("tags" to tags.map { it.id to it.data }.toMap(mutableMapOf()))
}