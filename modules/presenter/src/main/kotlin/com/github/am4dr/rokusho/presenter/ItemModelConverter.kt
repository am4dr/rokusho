package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library2.Library
import com.github.am4dr.rokusho.library2.LoadedLibrary
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ItemModelConverter(
    libraries: ObservableList<LoadedLibrary>
) {

    private val convertedItemModels: MutableMap<LoadedLibrary, ObservableList<out ItemViewModel<*>>> = mutableMapOf()

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

    fun getOrCreate(library: LoadedLibrary): ObservableList<out ItemViewModel<*>> =
        convertedItemModels.getOrPut(library) { createItemViewModels(library.library) }

    @ExperimentalCoroutinesApi
    private fun createItemViewModels(library: Library): ObservableList<out ItemViewModel<*>> {
        val models = FXCollections.observableArrayList<LibraryItemViewModel>()
        library.getDataAndSubscribe { data ->
            models.addAll(data.items.map { LibraryItemViewModel(library, it) })

            subscribeFor(models) { event, list ->
                runLater {
                    when (event) {
                        is Library.Event.ItemEvent -> when (event) {
                            is Library.Event.ItemEvent.Loaded,
                            is Library.Event.ItemEvent.Added -> { list.add(LibraryItemViewModel(library, event.item)) }
                            is Library.Event.ItemEvent.Removed -> { list.removeAll { it.has(event.item) } }
                            is Library.Event.ItemEvent.Updated -> {
                                for (i in 0..list.lastIndex) {
                                    if (list[i].has(event.item)) {
                                        list[i] = LibraryItemViewModel(library, event.item)
                                    }
                                }
                            }
                        }
                        is Library.Event.TagEvent -> {}
                    }.let { /* 網羅性チェック */ }
                }
            }
        }
        return models
    }
}