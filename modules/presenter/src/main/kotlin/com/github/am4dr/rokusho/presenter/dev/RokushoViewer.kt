package com.github.am4dr.rokusho.presenter.dev

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.LibraryItem
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.stage.Stage

class RokushoViewer(val libraries: ObservableList<Library<*>>) {
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
    private fun createScene(w: Double, h: Double): Scene {
        val libraryList = ListView(TransformedList(libraries,
            RokushoViewer::LibraryListCell
        ))
        return Scene(libraryList, w, h)
    }


    private class LibraryListCell(library: Library<*>) : FlowPane() {
        private val items = FXCollections.observableArrayList<LibraryItem<*>>()
        init {
            items.addAll(library.getItems())
            library.subscribeFor(this) { event, cell ->
                runLater {
                    val items = cell.items
                    when (event) {
                        is Library.Event.AddItem<*> -> items.add(event.item)
                        is Library.Event.RemoveItem<*> -> items.removeAll(event.item)
                        is Library.Event.UpdateItem<*> -> {
                            items.indexOfFirst { it === event.item }
                                .takeIf { it >= 0 }
                                ?.let { index -> items.set(index, event.item) }
                        }
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
                            RecordsViewer(items).show()
                        }
                    },
                    Label(library.name),
                    Label(library.toString()))
        }
    }
}