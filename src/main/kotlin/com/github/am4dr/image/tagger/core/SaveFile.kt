package com.github.am4dr.image.tagger.core

import com.github.am4dr.rokusho.core.SimpleTag
import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.nio.file.Paths

data class SaveFile(
        val version: String,
        val tags: Map<String, Tag>,
        val metaData: Map<Path, ImageMetaData>) {
    companion object {
        private val log = LoggerFactory.getLogger(SaveFile::class.java)
        fun parse(string: String): SaveFile {
            val yaml = Yaml().load(string)
            if (yaml == null || yaml !is Map<*,*>) { throw IllegalSaveFormatException("top level of save file must be a Map") }
            val version = parseVersion(yaml["version"])
            val tags = parseTagInfo(yaml["tags"])
            val metaData = parseMetaData(yaml["metaData"])
            return SaveFile(version, tags, metaData)
        }
        private fun parseVersion(data: Any?): String {
            data ?: throw VersionNotSpecifiedException()
            return data as? String ?: throw IllegalSaveFormatException("version must be a String")
        }
        private fun parseTagInfo(data: Any?): Map<String, Tag> {
            data ?: return mutableMapOf() // do not use mapOf() to avoid Yaml reference
            val map = data as? Map<*, *> ?: throw IllegalSaveFormatException("tags must be a Map<String, Map<String, String>>")
            return map.map {
                val name = it.key as? String ?: throw IllegalSaveFormatException("id of tag in tags must be a String")
                val opts = it.value as? Map<*, *> ?: throw IllegalSaveFormatException("value of tags must be a Map<String, String>")
                opts.forEach {
                    it.key as? String ?: throw IllegalSaveFormatException("id of tag option must be a String")
                    if (it.value == null) throw IllegalSaveFormatException("value of tag option must not be null")
                }
                if (opts.containsKey("type") && opts["type"] !is String) throw IllegalSaveFormatException("type of tag must be a String")
                val type = opts["type"] as? String ?: "text"
                @Suppress("UNCHECKED_CAST")
                opts as Map<String, Any>
                Pair(name, SimpleTag(name , TagType.from(type), opts))
            }.toMap()
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
            return map.map { tag ->
                val name = tag.key as? String ?: throw IllegalSaveFormatException("tag id in metaData must be a String: ${tag.key}")
                val ops = tag.value as? Map<*, *> ?: mutableMapOf<String, Any>() // do not use mapOf() to avoid Yaml reference
                ops.forEach {
                    if (it.key !is String) throw IllegalSaveFormatException("key of metaData.tags must be a String: ${it.key}")
                    if (it.value == null) throw IllegalSaveFormatException("value of metaData.tags.<option id> must not be null: ${it.value}")
                }
                @Suppress("UNCHECKED_CAST")
                ops as Map<String, Any>
                SimpleTag(name, TagType.TEXT, ops)
            }
        }
        const val pathSeparator: String = "/"
        fun ImageMetaData.toDumpStructure(): Map<String, Any> =
                mapOf("tags" to tags.map { it.id to it.data }.toMap())
    }
    fun toTextFormat(): String =
        Yaml().dump(toDumpStructure())
    fun toDumpStructure(): Map<String, Any> =
        mapOf(
                "version" to version,
                "tags" to tags.map {
                    val name = it.key
                    val info = it.value
                    Pair(name, info.data)
                }.toMap(),
                "metaData" to metaData.map {
                    val path = it.key.joinToString(pathSeparator)
                    val data = it.value
                    Pair(path, data.toDumpStructure())
                }.toMap())
}
open class IllegalSaveFormatException(message: String = "") : RuntimeException(message)
class VersionNotSpecifiedException(message: String = ""): IllegalSaveFormatException(message)

