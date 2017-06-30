package com.github.am4dr.rokusho.javafx.collection

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
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
            val spreadedList = ConcatenatedList(lists)
            assertIterableEquals(list, spreadedList)
        }
    }

    @Nested
    inner class BindableFromOthers {
        @Test
        fun AddElementsToAListInTheLists() {
            val list = observableArrayList<Int>()
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            list.addAll(1,2,3,4,5)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveElementsFromAListInTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            list.remove(2,4)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveElementsFromAListInTheListsWithBindingsBindContent() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            Bindings.bindContent(binder, spreaded)

            list.remove(2,4)

            assertIterableEquals(list, binder)
        }
        @Test
        fun AddAListToTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>()
            val spreaded = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            lists.add(list)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveAListFromTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = ConcatenatedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

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
