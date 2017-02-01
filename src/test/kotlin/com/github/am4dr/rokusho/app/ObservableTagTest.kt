package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType
import javafx.beans.property.SimpleObjectProperty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class ObservableTagTest {
    @Test
    fun withoutBaseTag() {
        val tag = SimpleObservableTag("id", TagType.TEXT, mutableMapOf<String, Any>("key" to "value"))
        assertAll(
                Executable { tag.id == "id" },
                Executable { tag.type == TagType.TEXT },
                Executable { tag.data.containsKey("key") })
    }
    @Test
    fun withBaseTag() {
        val base = SimpleObservableTag("base", TagType.TEXT, mutableMapOf<String, Any>("keyA" to 0, "keyB" to 1))
        val tag = DerivedObservableTag(base, mutableMapOf("keyB" to "override", "keyC" to 100))
        val data = tag.data
        assertAll(
                Executable { data.keys.containsAll(setOf("keyA", "keyB", "keyC")) },
                Executable { data["keyB"] as String == "override"}
        )
    }
    @Test
    fun baseChangeAware() {
        val base = SimpleObservableTag("base", TagType.TEXT, mutableMapOf())
        val tag = DerivedObservableTag(base)
        val binding = SimpleObjectProperty<Tag>().apply { bind(tag) }
        assertTrue { tag.data.isEmpty() }
        assertTrue { binding.value.data.isEmpty() }
        base.putData("key", "value")
        assertFalse { tag.data.isEmpty() }
        assertTrue { binding.value.data.containsKey("key") }
        tag.putData("key2", "value")
        assertTrue { binding.value.data.containsKey("key2") }
    }
    @Test
    fun DerivedFromDerived() {
        val base = SimpleObservableTag("base", TagType.TEXT, mapOf("keyA" to "valueA"))
        val middle = DerivedObservableTag(base, mapOf("keyB" to "valueB"))
        val end = DerivedObservableTag(middle, mapOf("keyC" to "valueC"))
        assertAll(
                Executable { base.data.containsKey("keyA") },
                Executable { middle.data.keys.containsAll(setOf("keyA", "keyB")) },
                Executable { end.data.keys.containsAll(setOf("keyA", "keyB", "keyC")) },
                Executable { !base.data.keys.containsAll(setOf("keyB", "keyC")) }
        )
    }
}