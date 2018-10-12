package com.github.am4dr.rokusho.old.savedata.yaml.v1

import com.github.am4dr.rokusho.old.core.library.Tag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream


class ParserTest {

    companion object {
        fun <T> List<T>.dynamicTest(nameGenerator: (T) -> String, testExecutor: (T) -> Unit): Stream<DynamicTest> =
                DynamicTest.stream(iterator(), nameGenerator, testExecutor)
        fun <T> List<T>.dynamicTest(testExecutor: (T) -> Unit): Stream<DynamicTest> =
                DynamicTest.stream(iterator(), { "test $it" }, testExecutor)
    }

    @Nested
    class BaseTagParserTest {
        companion object {
            fun tags(vararg pairs: Pair<*, *>): Map<*, *> = mapOf("tags" to mapOf(*pairs))
            fun tag(name: Any, vararg data: Pair<*, *>): Pair<*, *> = name to mapOf(*data)
        }

        @TestFactory
        fun extractTagsFromData(): Stream<DynamicTest> = listOf(
                Pair(listOf(), mapOf<Any, Any>()),
                Pair(listOf(), tags()),
                Pair(listOf(TagEntry("tagID")), tags(tag("tagID"))),
                Pair(listOf(TagEntry("tagID", mapOf("type" to "selection"))), tags(tag("tagID", "type" to "selection")))
        ).dynamicTest({ (expectedTags, data) -> "$data is $expectedTags" }) { (expectedTags, data) ->
            val tags = extractTags(data)
            assertEquals(expectedTags.size, tags.size, "number of tags must be same")
            assertTrue(tags.containsAll(expectedTags), "parsed tag list contains all expected tags")
        }

        @TestFactory
        fun detectTagTypeTest(): Stream<DynamicTest> = listOf(
                Pair(Tag.Type.TEXT, mapOf()),
                Pair(Tag.Type.VALUE, mapOf("type" to "value")),
                Pair(Tag.Type.TEXT, mapOf("type" to 0)),
                Pair(Tag.Type.OTHERS, mapOf("type" to "hogehogehugahuga"))
        ).dynamicTest({ (type, data) -> "$data is $type" }) { (type, data) ->
            assertEquals(type, detectTagType(data))
        }
    }

    @Nested
    class ItemParserTest {
        companion object {
            fun items(vararg items: Pair<String, *>): Map<*, *> = mapOf("metaData" to mapOf(*items))
            fun item(id: String, data: Map<*, *>): Pair<String, Map<*, *>> = id to data
            fun item(id: String, vararg data: Pair<*, *>): Pair<String, Map<*, *>> = id to  mapOf(*data)
            fun tags(vararg pairs: Pair<*, *>): Pair<*, *> = "tags" to mapOf(*pairs)
            fun tag(name: String, vararg data: Pair<*, *>): Pair<*, *> = name to mapOf(*data)
        }

        @TestFactory
        fun itemExtractionFromDataTest(): Stream<DynamicTest> = listOf(
                mapOf<Any, Any>() to listOf(),
                items() to listOf(),
                items(item("itemID")) to listOf(ItemEntry("itemID")),
                items(item("itemID", tags())) to listOf(ItemEntry("itemID")),
                items(item("itemID", tags(tag("A")))) to listOf(ItemEntry("itemID", listOf(ItemTagEntry("A"))))
        ).dynamicTest { (data, expected) ->
            val extracted = extractItems(data)
            assertEquals(expected.size, extracted.size)
            assertTrue(extracted.containsAll(expected))
        }
    }
}
