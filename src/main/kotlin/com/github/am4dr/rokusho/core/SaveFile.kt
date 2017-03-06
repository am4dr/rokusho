package com.github.am4dr.rokusho.core

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path

data class SaveFile(
        val version: String,
        val tags: Map<String, Tag>,
        val metaData: Map<Path, ImageMetaData>) {
    companion object {
        private val log = LoggerFactory.getLogger(SaveFile::class.java)
        const val pathSeparator: String = "/"
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

