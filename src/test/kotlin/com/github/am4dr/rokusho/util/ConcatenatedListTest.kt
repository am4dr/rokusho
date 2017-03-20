package com.github.am4dr.rokusho.util

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.*
import javafx.collections.ObservableList
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConcatenatedListTest {
    @Nested
    class ListAndListConcatenationTest {
        private var left: ObservableList<Int> = emptyObservableList()
        private var right: ObservableList<Int> = emptyObservableList()
        private var concat: ObservableList<Int> = emptyObservableList()

        @BeforeEach
        fun beforeEach() {
            left = observableList(mutableListOf(1, 2, 3))
            right = observableList(mutableListOf(100, 200, 300))
            concat = ConcatenatedList(left, right)
        }

        @Test
        fun simpleListAndListConcatenation() {
            assertIterableEquals(listOf(1,2,3,100,200,300), concat)
        }
        @Test
        fun modifyLeft() {
            left[1] = 0
            assertIterableEquals(listOf(1,0,3,100,200,300), concat)
        }
        @Test
        fun modifyRight() {
            right[1] = 0
            assertIterableEquals(listOf(1,2,3,100,0,300), concat)
        }
        @Test
        fun resizeLeft() {
            left.add(-1)
            assertIterableEquals(listOf(1,2,3,-1,100,200,300), concat)
            left.clear()
            assertIterableEquals(listOf(100,200,300), concat)
        }
        @Test
        fun resizeRight() {
            right.add(-1)
            assertIterableEquals(listOf(1,2,3,100,200,300,-1), concat)
            right.clear()
            assertIterableEquals(listOf(1,2,3), concat)
        }
        @Test
        fun resizeLeftAndRight() {
            left.removeAt(0)
            right.removeAt(0)
            assertIterableEquals(listOf(2,3,200,300), concat)
        }
        @Test
        fun isObservable() {
            val listProperty = SimpleListProperty(observableList(mutableListOf<Int>())).apply { bindContent(concat) }
            left[1] = 0
            assertIterableEquals(listOf(1,0,3,100,200,300), listProperty)
            right[1] = 0
            assertIterableEquals(listOf(1,0,3,100,0,300), listProperty)
            left.clear()
            assertIterableEquals(listOf(100,0,300), concat)
        }
    }
    @Nested
    class MultipleListConcatenationTest {
        private var left = observableList(mutableListOf(1, 2, 3))
        private var middle = observableList(mutableListOf(0))
        private var right = observableList(mutableListOf(100, 200, 300))
        var concat = ConcatenatedList(left, middle, right)
        @BeforeEach
        fun beforeEach() {
            left = observableList(mutableListOf(1, 2, 3))
            middle = observableList(mutableListOf(0))
            right = observableList(mutableListOf(100, 200, 300))
            concat = ConcatenatedList(left, middle, right)
        }
        @Test
        fun threeListsConcatenation() {
            assertIterableEquals(listOf(1,2,3,0,100,200,300), concat)
        }
        @Test
        fun modifyTheMiddleOfThreeListsConcatenation() {
            middle.addAll(0, 0)
            assertIterableEquals(listOf(1,2,3,0,0,0,100,200,300), concat)
        }
    }
    @Nested
    class ConcatTest {
        @Test
        fun concatMethod() {
            val concat = ConcatenatedList<Int>()
            val mid = observableList(mutableListOf(10,20,30))
            concat.concat(observableList(listOf(1,2,3)))
            concat.concat(mid)
            concat.concat(observableList(listOf(100,200,300)))
            assertIterableEquals(listOf(1,2,3,10,20,30,100,200,300), concat)
            mid.clear()
            assertIterableEquals(listOf(1,2,3,100,200,300), concat)
        }
    }
    @Nested
    class FXCollectionsConcat {
        @Test
        fun fxConcatIsNotBoundedToOriginalLists() {
            val expected = listOf(1,2,3,10,20,30,100,200,300)
            val mid = observableList(mutableListOf(10,20,30))
            val concat = FXCollections.concat(observableArrayList(1,2,3), mid, observableArrayList(100,200,300))
            assertIterableEquals(expected, concat, "before the mid cleared")
            mid.clear()
            assertIterableEquals(expected, concat, "after the mid cleared")
        }
    }
}