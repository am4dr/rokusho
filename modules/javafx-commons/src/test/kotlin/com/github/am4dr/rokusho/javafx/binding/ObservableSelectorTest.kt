package com.github.am4dr.rokusho.javafx.binding

import javafx.collections.FXCollections.observableArrayList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ObservableSelectorTest {

    val selector = ObservableSelector<String>(observableArrayList("first", "second", "third"))

    @Test
    fun selectByIndexTest() {
        selector.select(0)
        assertEquals("first", selector.selected.value)
        selector.select(1)
        assertEquals("second", selector.selected.value)
    }

    @Test
    fun selectByItemTest() {
        selector.select("first")
        assertEquals("first", selector.selected.value)
        selector.select("second")
        assertEquals("second", selector.selected.value)
    }

    @Test
    fun ignoreIllegalIndexSelectionTest() {
        selector.select(1)
        assertEquals("second", selector.selected.value)
        selector.select(-100)
        assertEquals("second", selector.selected.value)

        selector.select(0)
        assertEquals("first", selector.selected.value)
        selector.select(100)
        assertEquals("first", selector.selected.value)
    }

    @Test
    fun clearTest() {
        selector.clearSelection()
        assertNull(selector.selected.value)
    }

    @Test
    fun selectAddedItemAutomatically() {
        selector.clearSelection()
        selector.list.add("first")
        assertEquals("first", selector.selected.value)
    }

    class RemoveSelectedItemTest {
        val selector = ObservableSelector<String>(observableArrayList("first", "second", "third"))
        @Test
        fun firstIsSelected() {
            selector.select(0)
            selector.list.removeAt(0)
            assertEquals("second", selector.selected.value)
        }
        @Test
        fun lastIsSelected() {
            selector.select(selector.list.lastIndex)
            selector.list.removeAt(selector.list.lastIndex)
            assertEquals("second", selector.selected.value)
        }
        @Test
        fun middleIsSelected() {
            selector.select(1)
            selector.list.removeAt(1)
            assertNotEquals("second", selector.selected.value)
        }
    }
}