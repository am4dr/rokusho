package com.github.am4dr.rokusho.javafx.collection

import javafx.collections.FXCollections
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UtilsTest {
    @Nested
    inner class ObservableMapToObservableListTest {
        @Test
        fun initializedWithGivenMap() {
            val map = FXCollections.observableMap(mutableMapOf(1 to "1", 2 to "2"))
            val list = toObservableList(map)

            assertEquals(2, list.size)
            assertTrue(list.containsAll(listOf("1", "2")))
        }

        @Test
        fun observingRemoveAndPut() {
            val map = FXCollections.observableMap(mutableMapOf(1 to "1", 2 to "2"))
            val list = toObservableList(map)

            map.remove(2)
            assertEquals(1, list.size)
            assertTrue(list.containsAll(listOf("1")))

            map[10] = "10"
            map[1]  = "100"
            assertEquals(2, list.size)
            assertTrue(list.containsAll(listOf("100", "10")))
        }
    }
    @Nested
    inner class ObservableSetToObservableListTest {
        @Test
        fun initializedWithGivenSet() {
            val set = FXCollections.observableSet(1, 2, 3)
            val list = toObservableList(set)

            assertTrue(list.containsAll(listOf(1, 2, 3)))
        }

        @Test
        fun observingRemoveAndAdd() {
            val set = FXCollections.observableSet(1, 2, 3)
            val list = toObservableList(set)

            set.remove(2)
            assertEquals(2, list.size)
            assertTrue(list.containsAll(listOf(1, 3)))

            set.add(100)
            assertEquals(3, list.size)
            assertTrue(list.containsAll(listOf(1, 3, 100)))
        }
    }
}
