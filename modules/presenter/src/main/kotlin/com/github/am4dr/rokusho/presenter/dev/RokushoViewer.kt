package com.github.am4dr.rokusho.presenter.dev

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.library2.Library
import com.github.am4dr.rokusho.library2.LibraryItem
import com.github.am4dr.rokusho.library2.LoadedLibrary
import com.github.am4dr.rokusho.library2.addOrReplaceEntity
import com.github.am4dr.rokusho.util.log.idHash
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RokushoViewer(val libraries: ObservableList<LoadedLibrary>) {

    companion object {
        const val initialWidth: Double = 300.0
        const val initialHeight: Double = 500.0
    }

    val stage = Stage()

    init {
        stage.apply {
            title = "[dev] Rokusho viewer"
            scene = createScene(
                initialWidth,
                initialHeight
            )
        }
    }

    fun show() = stage.show()
    @ExperimentalCoroutinesApi
    private fun createScene(w: Double, h: Double): Scene {
        val libraryList = ListView(TransformedList(libraries,
            RokushoViewer::LibraryListCell
        ))
        return Scene(libraryList, w, h)
    }


    @ExperimentalCoroutinesApi
    private class LibraryListCell(library: LoadedLibrary) : FlowPane() {
        private val items = FXCollections.observableArrayList<LibraryItem<*>>()
        init {
            library.library.getDataAndSubscribe { data ->
                items.addAll(data.items)
                subscribeFor(this@LibraryListCell) { event, cell ->
                    runLater {
                        val items = cell.items
                        when (event) {
                            is Library.Event.TagEvent -> TODO()
                            is Library.Event.ItemEvent -> when (event) {
                                is Library.Event.ItemEvent.Loaded,
                                is Library.Event.ItemEvent.Added -> items.add(event.item)
                                is Library.Event.ItemEvent.Removed -> items.removeAll { it.isSameEntity(event.item) }
                                is Library.Event.ItemEvent.Updated -> { items.addOrReplaceEntity(event.item) }
                            }
                        }.let { /* 網羅性チェックを強制するために必要 */ }
                    }
                }
            }
            children.addAll(
                Hyperlink("tags").apply {
                    setOnAction {
                        LibraryViewer(library).show()
                    }
                },
                Hyperlink("records").apply {
                    setOnMouseClicked {
                        RecordsViewer(library.idHash, items).show()
                    }
                },
                Label(library.name),
                Label(library.toString()))
        }
    }
}