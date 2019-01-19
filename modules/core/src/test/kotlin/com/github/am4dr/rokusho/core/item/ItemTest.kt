package com.github.am4dr.rokusho.core.item

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ItemTest {

    @Test
    fun itemTypeTest() {
        assertEquals(String::class, Item(Item.ID(""), "").type)
    }
}