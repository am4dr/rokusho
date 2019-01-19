package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.library.Library
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class ItemModelConverter(
    libraries: ObservableList<Library<*>>
) {

    private val convertedItemModels: MutableMap<Library<*>, ObservableList<ItemViewModel<*>>> = mutableMapOf()

    init {
        libraries.forEach {
            getOrCreate(it)
        }
        libraries.addListener(ListChangeListener { change ->
            while (change.next()) {
                change.removed.forEach { lib ->
                    convertedItemModels.remove(lib)
                }
                change.addedSubList.forEach { lib ->
                    getOrCreate(lib)
                }
            }
        })
    }

    fun getOrCreate(library: Library<*>): ObservableList<ItemViewModel<*>> =
        convertedItemModels.getOrPut(library) { createItemViewModels(library) }

    private fun createItemViewModels(library: Library<*>): ObservableList<ItemViewModel<*>> =
        TransformedList(library.getItems()) {
            LibraryItemItemViewModel(library, it)
        }

    fun remove(library: Library<*>) {
        convertedItemModels.remove(library)
    }
}