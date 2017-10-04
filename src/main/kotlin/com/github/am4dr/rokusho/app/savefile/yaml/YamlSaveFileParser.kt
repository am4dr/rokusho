package com.github.am4dr.rokusho.app.savefile.yaml

import com.github.am4dr.rokusho.app.savedata.ImageMetaData
import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savefile.yaml.SaveFileParser.IllegalSaveFormatException
import com.github.am4dr.rokusho.app.savefile.yaml.SaveFileParser.VersionNotSpecifiedException
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Tag
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.nio.file.Paths

class YamlSaveFileParser : SaveFileParser {
    companion object {
        private val log = LoggerFactory.getLogger(YamlSaveFileParser::class.java)
        fun parse(string: String): SaveData {
            val yaml = Yaml().load(string)
            if (yaml == null || yaml !is Map<*,*>) { throw IllegalSaveFormatException("top level of save file must be a Map") }
            val versionString = parseVersion(yaml["version"])
            val version = SaveData.Version.of(versionString) ?: throw IllegalSaveFormatException("version $versionString is not supported")
            val tags = parseTagInfo(yaml["tags"])
            val metaData = parseMetaData(yaml["metaData"], tags)
            return SaveData(version, tags, metaData)
        }
        private fun parseVersion(data: Any?): String {
            data ?: throw VersionNotSpecifiedException()
            return data as? String ?: throw IllegalSaveFormatException("version must be a String")
        }
        private fun parseTagInfo(data: Any?): MutableMap<String, Tag> {
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
                Pair(name, Tag(name , Tag.Type.from(type), opts))
            }.toMap(mutableMapOf())
        }
        private fun parseMetaData(data: Any?, tagInfo: MutableMap<String, Tag>): Map<Path, ImageMetaData> {
            data ?: return mapOf()
            data as? Map<*, *> ?: throw IllegalSaveFormatException("metaData must be a Map")
            return data.map {
                val path = it.key as? String ?: throw IllegalSaveFormatException("key of metaData must be a String")
                val metaData = it.value as? Map<*, *> ?: throw IllegalSaveFormatException("value of metaData must be a Map")
                val tags = parseTagData(metaData["tags"], tagInfo)
                Pair(Paths.get(path), ImageMetaData(tags))
            }.toMap()
        }
        private fun parseTagData(data: Any?, tagInfo: MutableMap<String, Tag>): List<ItemTag> {
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
                ItemTag(tagInfo.getOrPut(name, { Tag(name , Tag.Type.TEXT, mapOf("value" to name)) }) , ops["value"]?.toString())
            }
        }
    }

    override fun parse(path: Path): FileBasedSaveData = FileBasedSaveData(path, parse(path.toFile().readText()))
}