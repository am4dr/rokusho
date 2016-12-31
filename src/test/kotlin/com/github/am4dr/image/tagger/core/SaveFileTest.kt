package com.github.am4dr.image.tagger.core;

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class SaveFileTest {
    @Test
    fun emptyStringTest() {
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("")
        }
    }
    @Test
    fun versionOnlyTest() {
        val save = SaveFile.parse("version: \"1\"")
        assertEquals("1", save.version)
        assertEquals(0, save.tags.size)
        assertEquals(0, save.metaData.size)
    }
    @Test
    fun intVersionTest() {
        val res = assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("version: 1")
        }
        assertNotEquals(res.javaClass, VersionNotSpecifiedException::class.java)
    }
    @Test
    fun nullTagsMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |tags: null
            |""".trimMargin())
        assertEquals(0, save.tags.size)
    }
    @Test
    fun emptyTagsMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |tags: {}
            |""".trimMargin())
        assertEquals("1", save.version)
        assertEquals(0, save.tags.size)
        assertEquals(0, save.metaData.size)
    }
    @Test
    fun tagsMetaDataIsNotAMapTest() {
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |tags: ["tagA", "tagB"]
            |""".trimMargin())
        }
    }
    @Test
    fun singleTagsMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |tags: { tagA: {} }
            |""".trimMargin())
        assertEquals(1, save.tags.size)
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |tags: { tagA: data }
            |""".trimMargin())
        }
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |tags: { tagA: { type: 1 } }
            |""".trimMargin())
        }
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |tags: { tagA: { null: value } }
            |""".trimMargin())
        }
    }
    @Test
    fun tagsMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |tags: { tagA: {}, tagB: {}, tagC: {} }
            |""".trimMargin())
        assertEquals(3, save.tags.size)
        with(save.tags) {
            assert(contains("tagA"))
            assert(contains("tagB"))
            assert(contains("tagC"))
        }
    }
    @Test
    fun emptyMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |metaData: {}
            |""".trimMargin())
        assertEquals(0, save.metaData.size)
    }
    @Test
    fun nullMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |metaData: null
            |""".trimMargin())
        assertEquals(0, save.metaData.size)
    }
    @Test
    fun metaDataIsNotAMapTest() {
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |metaData: data
            |""".trimMargin())
        }
    }
    @Test
    fun keyOfMetaDataMustBeString() {
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |metaData: { null: {} }
            |""".trimMargin())
        }
    }
    @Test
    fun emptyTagMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |metaData: { path/to/image: {} }
            |""".trimMargin())
        assert(save.metaData.containsKey(Paths.get("path/to/image")))
    }
    @Test
    fun tagsInMetaDataMustBeMap() {
        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
            SaveFile.parse("""
            |version: "1"
            |metaData: { path/to/image: [tagA] }
            |""".trimMargin())
        }
    }
    @Test
    fun singleTagInMetaDataTest() {
        val save = SaveFile.parse("""
            |version: "1"
            |metaData: { path/to/image: { tags: [tagA] } }
            |""".trimMargin())
        assertEquals(1, save.metaData.size)
        assert(save.metaData.containsKey(Paths.get("path/to/image")))
        assert(save.metaData[Paths.get("path/to/image")]!!.let { it.tags.first().text == "tagA" })
    }
}
