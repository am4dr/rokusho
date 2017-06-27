package com.github.am4dr.rokusho.util

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpreadedListTest {
    @Nested
    class ValuesAreBoundToTheElementsOfTheLists {
        @Test
        fun consistsOfOnlyOneObservableList() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreadedList = SpreadedList(lists)
            assertIterableEquals(list, spreadedList)
        }
    }

    @Nested
    class BindableFromOthers {
        @Test
        fun AddElementsToAListInTheLists() {
            val list = observableArrayList<Int>()
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = SpreadedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            list.addAll(1,2,3,4,5)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveElementsFromAListInTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = SpreadedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            list.remove(2,4)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveElementsFromAListInTheListsWithBindingsBindContent() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = SpreadedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            Bindings.bindContent(binder, spreaded)

            list.remove(2,4)

            assertIterableEquals(list, binder)
        }
        @Test
        fun AddAListToTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>()
            val spreaded = SpreadedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            lists.add(list)

            assertIterableEquals(list, binder)
        }
        @Test
        fun RemoveAListFromTheLists() {
            val list = observableArrayList(1,2,3,4,5)
            val lists = observableArrayList<ObservableList<Int>>(list)
            val spreaded = SpreadedList(lists)
            val binder = SimpleListProperty<Int>(observableArrayList())
            binder.bindContent(spreaded)

            lists.remove(list)

            assertIterableEquals(listOf<Int>(), binder)
        }
    }
    @Test
    fun real() {
        val list = observableArrayList<Int>(1,2,3)
        val lists = observableArrayList<ObservableList<Int>>(TransformedList(list) { it * 100 })
        val spreaded = SpreadedList(lists)

        assertIterableEquals(listOf(100, 200, 300), spreaded)

        list.remove(0, 3)

        assertIterableEquals(listOf<Int>(), spreaded)
    }
}
