package com.github.am4dr.rokusho.core.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DataObjectTest {

    @Test
    fun equality() {
        val a = DataObject(mapOf("id" to "value", "ID" to "VALUE"))
        val b = DataObject(mapOf("ID" to "VALUE", "id" to "value"))
        assertTrue(a == b)
        assertEquals(a.hashCode(), b.hashCode())
    }
}