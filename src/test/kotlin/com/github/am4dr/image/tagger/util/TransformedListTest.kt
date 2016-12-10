package com.github.am4dr.image.tagger.util

import javafx.collections.FXCollections
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TransformedListTest {
    @Test
    fun sizeTest() {
        val source = FXCollections.observableList(mutableListOf<Int>())
        val transformed = TransformedList(source, Int::toString)
        source.addAll(0, 1, 2, 3, 4)
        assertEquals(5, transformed.size)
        source.removeAt(1)
        assertEquals(4, transformed.size)
        source.setAll(10, 20, 30)
        assertEquals(3, transformed.size)
        source.set(0, 100)
        assertEquals(3, transformed.size)
    }
    @Test
    fun addTest() {
        val source = FXCollections.observableList(mutableListOf<Int>())
        val transformed = TransformedList(source, Int::toString)
        source.addAll(0, 1, 2)
        assertEquals("0", transformed[0])
        assertEquals("1", transformed[1])
        assertEquals("2", transformed[2])
    }
    @Test
    fun addInTheMiddleTest() {
        val source = FXCollections.observableList(mutableListOf<Int>())
        val transformed = TransformedList(source, Int::toString)
        source.addAll(0, 2)
        source.add(1, 1)
        assertEquals("0", transformed[0])
        assertEquals("1", transformed[1])
        assertEquals("2", transformed[2])
    }
    @Test
    fun removeTest() {
        val source = FXCollections.observableList(mutableListOf<Int>())
        val transformed = TransformedList(source, Int::toString)
        source.addAll(0, 1, 2)
        source.removeAt(1)
        assertEquals("0", transformed[0])
        assertEquals("2", transformed[1])
    }
    @Test
    fun updateTest() {
        val source = FXCollections.observableList(mutableListOf<Int>())
        val transformed = TransformedList(source, Int::toString)
        source.addAll(0, 1, 2)
        source.set(1, 100)
        assertEquals("0", transformed[0])
        assertEquals("2", transformed[2])
        assertEquals("100", transformed[1])
    }
    @Test
    fun bindingTest() {
        val source = FXCollections.observableList(mutableListOf<Int>())
        val transformed = TransformedList(source, Int::toString)
        source.addAll(0, 1, 2)
        val listProperty = createEmptyListProperty<String>()
        listProperty.bindContent(transformed)
        assertEquals(3, listProperty.size)
        source.add(100)
        assertEquals(4, transformed.size)
        assertEquals(4, listProperty.size)
        source.clear()
        assertEquals(0, listProperty.size)
    }
}