package com.github.am4dr.rokusho.app.savedata.store.yaml

import com.github.am4dr.rokusho.app.savedata.SaveData
import com.github.am4dr.rokusho.app.savedata.SaveDataDeserializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class YamlSaveFileParserTest {

    companion object {
        fun String.parse(): SaveDataDeserializer.Result<SaveData> =
                YamlSaveDataDeserializer().deserialize(trimMargin().toByteArray())
    }
    @Test
    fun emptyStringRepresentsEmptySaveDataTest() {
        val save = "".parse().result!!
        assertEquals(SaveData.Version.VERSION_1, save.version)
        assertEquals(0, save.tags.size)
        assertEquals(0, save.items.size)
    }
    @Test
    fun versionOnlyTest() {
        val save = "version: \"1\"".parse().result!!
        assertEquals(SaveData.Version.VERSION_1, save.version)
        assertEquals(0, save.tags.size)
        assertEquals(0, save.items.size)
    }
    @Test
    fun invalidVersionStringTest() {
        val (result, _) = "version: \"INVALID VERSION STRING\"".parse()
        assertNull(result)
    }
    @Test
    fun emptyTagsMetaDataTest() {
        val result = """
            |version: "1"
            |tags: {}
            |""".parse().result!!
        assertEquals(SaveData.Version.VERSION_1, result.version)
        assertEquals(0, result.tags.size)
        assertEquals(0, result.items.size)
    }
    @Test
    fun tagsMetaDataTest() {
        val result = """
            |version: "1"
            |tags: { tagA: {}, tagB: {}, tagC: {} }
            |""".parse().result!!
        assertEquals(3, result.tags.size)
        with(result.tags.map { it.id }) {
            assert(contains("tagA"))
            assert(contains("tagB"))
            assert(contains("tagC"))
        }
    }
    @Test
    fun emptyTagMetaDataTest() {
        val result = """
            |version: "1"
            |metaData: { path/to/image: {} }
            |""".parse().result!!
        assert(result.items.map { it.id }.contains("path/to/image"))
    }
    @Test
    fun singleTagInMetaDataTest() {
        val save = """
            |version: "1"
            |tags: { tagA: {}}
            |metaData: { path/to/image: { tags: { tagA: {}} } }
            |""".parse().result!!
        assertEquals(1, save.items.size)
        assertNotNull(save.items.find { it.id == "path/to/image" })
        assert(save.items.find { it.id == "path/to/image" }!!.data.tags.first().tag.id == "tagA")

        val withOption = """
            |version: "1"
            |tags: { tagA: {}}
            |metaData: { path/to/image: { tags: { tagA: { option: value }} } }
            |""".parse().result!!
        assertEquals(1, withOption.items.size)
        assertNotNull(withOption.items.find { it.id == "path/to/image" })
        assert(withOption.items.find { it.id == "path/to/image" }!!.data.tags.first().tag.id == "tagA")
    }
}
