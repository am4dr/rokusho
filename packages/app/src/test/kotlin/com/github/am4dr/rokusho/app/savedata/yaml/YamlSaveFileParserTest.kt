package com.github.am4dr.rokusho.app.savedata.yaml

//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.nio.file.Paths
//
// TODO reuse
//class YamlSaveDataDeserializerTest {
//    @Test
//    fun emptyStringRepresentsEmptySaveDataTest() {
//        val save = YamlSaveDataDeserializer.parse("")
//        assertEquals(SaveData.Version.VERSION_1, save.version)
//        assertEquals(0, save.tags.size)
//        assertEquals(0, save.metaData.size)
//    }
//    @Test
//    fun versionOnlyTest() {
//        val save = YamlSaveDataDeserializer.parse("version: \"1\"")
//        assertEquals(SaveData.Version.VERSION_1, save.version)
//        assertEquals(0, save.tags.size)
//        assertEquals(0, save.metaData.size)
//    }
//    @Test
//    fun intVersionTest() {
//        val res = assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("version: 1")
//        }
//        assertNotEquals(res.javaClass, VersionNotSpecifiedException::class.java)
//    }
//    @Test
//    fun invalidVersionStringTest() {
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("version: \"INVALID VERSION STRING\"")
//        }
//    }
//    @Test
//    fun nullTagsMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: null
//            |""".trimMargin())
//        assertEquals(0, save.tags.size)
//    }
//    @Test
//    fun emptyTagsMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: {}
//            |""".trimMargin())
//        assertEquals(SaveData.Version.VERSION_1, save.version)
//        assertEquals(0, save.tags.size)
//        assertEquals(0, save.metaData.size)
//    }
//    @Test
//    fun tagsMetaDataIsNotAMapTest() {
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: ["tagA", "tagB"]
//            |""".trimMargin())
//        }
//    }
//    @Test
//    fun singleTagsMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: { tagA: {} }
//            |""".trimMargin())
//        assertEquals(1, save.tags.size)
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: { tagA: data }
//            |""".trimMargin())
//        }
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: { tagA: { key: null } }
//            |""".trimMargin())
//        }
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: { tagA: { type: 1 } }
//            |""".trimMargin())
//        }
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: { tagA: { null: value } }
//            |""".trimMargin())
//        }
//    }
//    @Test
//    fun tagsMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |tags: { tagA: {}, tagB: {}, tagC: {} }
//            |""".trimMargin())
//        assertEquals(3, save.tags.size)
//        with(save.tags) {
//            assert(contains("tagA"))
//            assert(contains("tagB"))
//            assert(contains("tagC"))
//        }
//    }
//    @Test
//    fun emptyMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: {}
//            |""".trimMargin())
//        assertEquals(0, save.metaData.size)
//    }
//    @Test
//    fun nullMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: null
//            |""".trimMargin())
//        assertEquals(0, save.metaData.size)
//    }
//    @Test
//    fun metaDataIsNotAMapTest() {
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: data
//            |""".trimMargin())
//        }
//    }
//    @Test
//    fun keyOfMetaDataMustBeString() {
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: { null: {} }
//            |""".trimMargin())
//        }
//    }
//    @Test
//    fun emptyTagMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: { path/to/image: {} }
//            |""".trimMargin())
//        assert(save.metaData.containsKey(Paths.get("path/to/image")))
//    }
//    @Test
//    fun tagsInMetaDataMustBeMap() {
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: { path/to/image: [tagA] }
//            |""".trimMargin())
//        }
//    }
//    @Test
//    fun singleTagInMetaDataTest() {
//        val save = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: { path/to/image: { tags: { tagA: {}} } }
//            |""".trimMargin())
//        assertEquals(1, save.metaData.size)
//        assert(save.metaData.containsKey(Paths.get("path/to/image")))
//        assert(save.metaData[Paths.get("path/to/image")]!!.tags.first().tag.id == "tagA")
//
//        val withOption = YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: { path/to/image: { tags: { tagA: { option: value }} } }
//            |""".trimMargin())
//        assertEquals(1, withOption.metaData.size)
//        assert(withOption.metaData.containsKey(Paths.get("path/to/image")))
//        assert(withOption.metaData[Paths.get("path/to/image")]!!.tags.first().tag.id == "tagA")
//
//        assertThrows<IllegalSaveFormatException>(IllegalSaveFormatException::class.java) {
//            YamlSaveDataDeserializer.parse("""
//            |version: "1"
//            |metaData: { path/to/image: { tags: { tagA: { opt: null } } } }
//            |""".trimMargin())
//        }
//    }
//}
