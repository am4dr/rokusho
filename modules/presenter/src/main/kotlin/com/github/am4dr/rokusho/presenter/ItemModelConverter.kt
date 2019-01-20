package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library.Library
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class ItemModelConverter(
    libraries: ObservableList<Library<*>>
) {

    private val convertedItemModels: MutableMap<Library<*>, ObservableList<out ItemViewModel<*>>> = mutableMapOf()

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

    fun getOrCreate(library: Library<*>): ObservableList<out ItemViewModel<*>> =
        convertedItemModels.getOrPut(library) { createItemViewModels(library) }

    private fun createItemViewModels(library: Library<*>): ObservableList<out ItemViewModel<*>> {
        val models = FXCollections.observableArrayList<LibraryItemItemViewModel>()
        library.subscribeFor(models) { event, list ->
            runLater {
                when (event) {
                    is Library.Event.AddItem<*> -> list.add(LibraryItemItemViewModel(library, event.item))
                    is Library.Event.RemoveItem<*> -> list.removeAll { it.has(event.item) }
                    is Library.Event.UpdateItem<*> -> {
                        list.indexOfFirst { it.has(event.item) }
                            .takeIf { it >= 0 }
                            ?.let { index -> list[index] = LibraryItemItemViewModel(library, event.item) }
                    }
                }
            }
        }
        models.addAll(library.getItems().map { LibraryItemItemViewModel(library, it) })
        return models
    }
}