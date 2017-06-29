package com.github.am4dr.rokusho.util

import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ObservableSubMapTest {
    @Test
    fun watchingSingleKey() {
        val src = FXCollections.observableMap(mutableMapOf(1 to "1", 2 to "2", 3 to "3"))
        val subMap = ObservableSubMap(src, listOf(2))
        src[2] = "200"

        assertEquals(1, subMap.size)
        assertEquals("200", subMap[2])
    }

    @Test
    fun canBeBoundFromOthers() {
        val src = FXCollections.observableMap(mutableMapOf(1 to "1", 2 to "2", 3 to "3"))
        val subMap = ObservableSubMap(src, listOf(2))
        val binder = SimpleMapProperty(subMap)
        src[2] = "200"

        assertEquals(1, binder.size)
        assertEquals("200", binder[2])
    }
}