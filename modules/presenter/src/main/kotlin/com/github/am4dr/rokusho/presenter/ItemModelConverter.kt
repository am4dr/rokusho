package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library.Library
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
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

    private fun createItemViewModels(library: Library<*>): ObservableList<ItemViewModel<*>> {
        val models = FXCollections.observableArrayList<ItemViewModel<*>>()
        library.subscribeFor(models) { event, list ->
            runLater {
                when (event) {
                    is Library.Event.AddItem<*> -> list.add(LibraryItemItemViewModel(library, event.item))
                    is Library.Event.RemoveItem<*> -> list.removeAll { it.item === event.item }
                    is Library.Event.UpdateItem<*> -> {
                        list.indexOfFirst { it.item === event.item }
                            .takeIf { it >= 0 }
                            ?.let { index -> list[index] = LibraryItemItemViewModel(library, event.item) }
                    }
                }
            }
        }
        models.addAll(library.getItems().map { LibraryItemItemViewModel(library, it) })
        return models
    }

    fun remove(library: Library<*>) {
        convertedItemModels.remove(library)
    }
}