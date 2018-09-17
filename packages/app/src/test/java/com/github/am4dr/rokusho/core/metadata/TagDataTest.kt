package com.github.am4dr.rokusho.core.metadata

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TagDataTest {

    @Test
    fun equality() {
        val a = TagData(mapOf("id" to "value", "ID" to "VALUE"))
        val b = TagData(mapOf("ID" to "VALUE", "id" to "value"))
        assertTrue(a == b)
        assertEquals(a.hashCode(), b.hashCode())
    }
}