package com.github.am4dr.rokusho.javafx.collection

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConcatenatedListTest {
    @Nested
    inner class ValuesAreBoundToTheElementsOfTheLists {
        @Test
        fun consistsOfOnlyOneObservableList() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreadList = ConcatenatedList(lists)
            assertIterableEquals(list, spreadList)
        }
    }
    @Test
    fun UpdateListToSameToOtherList() {
        val list1 = observableArrayList("hoge")
        val list2 = observableArrayList("piyo")

        val lists = observableArrayList<ObservableList<String>>(list1, list2)
        val spread = ConcatenatedList(lists)
        val binder = SimpleListProperty<String>(observableArrayList())
        binder.bindContent(spread)

        list2[0] = "hoge"
        assertEquals(list1, list2)

        assertIterableEquals(listOf("hoge", "hoge"), binder)
    }

    @Nested
    inner class BindableFromOthers {
        @Test
        fun AddElementsToAListInTheLists() {
            val list = observableArrayList<Int>()
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spread = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spread)

            list.addAll(1,2,3,4,5)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveElementsFromAListInTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spread = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spread)

            list.remove(2,4)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveElementsFromAListInTheListsWithBindingsBindContent() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spread = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            Bindings.bindContent(binder, spread)

            list.remove(2,4)

            assertIterableEquals(list, binder)
        }
        @Test
        fun AddAListToTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>()
            val spread = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spread)

            lists.add(list)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveAListFromTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spread = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spread)

            lists.remove(list)

            assertIterableEquals(listOf<Int>(), binder)
        }
    }
    @Nested
    inner class FXCollectionsConcat {
        @Test
        fun fxConcatIsNotBoundedToOriginalLists() {
            val expected = listOf(1,2,3,10,20,30,100,200,300)
            val mid = FXCollections.observableList(mutableListOf(10, 20, 30))
            val concat = FXCollections.concat(observableArrayList(1,2,3), mid, observableArrayList(100,200,300))
            assertIterableEquals(expected, concat, "before the mid cleared")
            mid.clear()
            assertIterableEquals(expected, concat, "after the mid cleared")
        }
    }
}
