package com.github.am4dr.image.tagger.core

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.nio.file.Paths

data class SaveFile(
        val version: String,
        val tags: Map<String, Map<String, String>>,
        val metaData: Map<Path, ImageMetaData>) {
    companion object {
        val log = LoggerFactory.getLogger(SaveFile::class.java)
        fun parse(string: String): SaveFile {
            val yaml = Yaml().load(string)
            if (yaml == null || yaml !is Map<*,*>) { throw IllegalSaveFormatException("top level of save file must be a Map") }
            val version = parseVersion(yaml["version"])
            val tags = parseTagMetaData(yaml["tags"])
            val metaData = parseMetaData(yaml["metaData"])
            return SaveFile(version, tags, metaData)
        }
        private fun parseVersion(data: Any?): String {
            data ?: throw VersionNotSpecifiedException()
            return data as? String ?: throw IllegalSaveFormatException("version must be a String")
        }
        private fun parseTagMetaData(data: Any?): Map<String, Map<String, String>> {
            data ?: return mutableMapOf() // do not use mapOf() to avoid Yaml reference
            val map = data as? Map<*, *> ?: throw IllegalSaveFormatException("tags must be a Map<String, Map<String, String>>")
            map.forEach {
                it.key as? String ?: throw IllegalSaveFormatException("key of tags must be a String")
                val opts = it.value as? Map<*, *> ?: throw IllegalSaveFormatException("value of tags must be a Map<String, String>")
                opts.forEach {
                    it.key as? String ?: throw IllegalSaveFormatException("name of tag option must be a String")
                    it.value as? String ?: throw IllegalSaveFormatException("value of tag option must be a String")
                }
            }
            @Suppress("UNCHECKED_CAST")
            return map as Map<String, Map<String, String>>
        }
        private fun parseMetaData(data: Any?): Map<Path, ImageMetaData> {
            data ?: return mapOf()
            data as? Map<*, *> ?: throw IllegalSaveFormatException("metaData must be a Map")
            return data.map {
                val path = it.key as? String ?: throw IllegalSaveFormatException("key of metaData must be a String")
                val metaData = it.value as? Map<*, *> ?: throw IllegalSaveFormatException("value of metaData must be a Map")
                val tags = parseTagData(metaData["tags"])
                Pair(Paths.get(path), ImageMetaData(tags))
            }.toMap()
        }
        private fun parseTagData(data: Any?): List<Tag> {
            data ?: return listOf()
            val map = data as? Map<*, *> ?: throw IllegalSaveFormatException("tags in metaData must be a Map<String, Any>")
            return map.map {
                val name = it.key as? String ?: throw IllegalSaveFormatException("tag name in metaData must be a String")
                val ops = it.value as? Map<*, *> ?: mutableMapOf<String, Any>() // do not use mapOf() to avoid Yaml reference
                if (!ops.all { it.key == String }) throw IllegalSaveFormatException("metaData.tags.data must be a Map<String, Any>")
                @Suppress("UNCHECKED_CAST")
                ops as Map<String, Any>
                TextTag(name, ops)
            }
        }
        const val pathSeparator: String = "/"
        fun ImageMetaData.toDumpStructure(): Map<String, Any> =
                mapOf("tags" to tags.map { it.name to it.data }.toMap())
    }
    fun toTextFormat(): String =
        Yaml().dump(toDumpStructure())
    fun toDumpStructure(): Map<String, Any> =
        mapOf(
                "version" to version,
                "tags" to tags,
                "metaData" to metaData.map {
                    val path = it.key.joinToString(pathSeparator)
                    val data = it.value
                    Pair(path, data.toDumpStructure())
                }.toMap())
}
open class IllegalSaveFormatException(message: String = "") : RuntimeException(message)
class VersionNotSpecifiedException(message: String = ""): IllegalSaveFormatException(message)

