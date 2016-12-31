package com.github.am4dr.image.tagger.core

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

data class SaveFile(
        val version: String,
        val tags: Map<String, Map<String, String>>,
        val metaData: Map<String, ImageMetaData>) {
    companion object {
        val log = LoggerFactory.getLogger(SaveFile::class.java)
        fun parse(string: String): SaveFile {
            val yaml = Yaml().load(string)
            if (yaml == null || yaml !is Map<*,*>) { throw IllegalSaveFormatException() }
            val version = yaml["version"]?.let { it as? String } ?: throw VersionNotSpecifiedException()
            val tags = parseMetaTagData(yaml["tags"])
            val metaData = parseMetaData(yaml["metaData"])
            return SaveFile(version, tags, metaData)
        }
        private fun parseMetaTagData(data: Any?): Map<String, Map<String, String>> {
            data ?: return mapOf()
            val map = data as? Map<*, *> ?: throw IllegalSaveFormatException("tags must be a Map<String, Map<String, String>>")
            map.forEach {
                val name = it.key as? String ?: throw IllegalSaveFormatException("key of tags must be a String")
                val opts = it.value as? Map<*, *> ?: throw IllegalSaveFormatException("value of tags must be a Map<String, String>")
                opts.forEach {
                    it.key as? String ?: IllegalSaveFormatException("name of tag option must be a String")
                    it.value as? String ?: IllegalSaveFormatException("value of tag option must be a String")
                }
            }
            return data as Map<String, Map<String, String>>
        }
        private fun parseMetaData(data: Any?): Map<String, ImageMetaData> {
            data ?: return mapOf()
            data as? Map<*, *> ?: throw IllegalSaveFormatException("metaData must be a Map")
            return data.map {
                val path = it.key as? String ?: throw IllegalSaveFormatException("key of metaData must be a String")
                val metaData = it.value as? Map<*, *> ?: throw IllegalSaveFormatException("value of metaData must be a Map")
                val tags = parseTagData(metaData["tags"])
                Pair(path, ImageMetaData(tags))
            }.toMap()
        }
        private fun parseTagData(data: Any?): List<Tag> {
            data ?: return listOf()
            val list = data as? List<*> ?: throw IllegalSaveFormatException("tags in metaData must be a List<String>")
            return list.map {
                if (it !is String) throw IllegalSaveFormatException("tag in metaData must be a List<String>")
                DefaultTag(it)
            }
        }
    }
}
open class IllegalSaveFormatException(message: String = "") : RuntimeException(message)
class VersionNotSpecifiedException(message: String = ""): IllegalSaveFormatException(message)

