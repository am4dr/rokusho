package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.RokushoLibrary
import com.github.am4dr.rokusho.core.library.RecordListWatcher
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class RokushoViewer(val rokusho: Rokusho) {
    private val recordLists = rokusho.recordLists
    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 500.0
    }
    val stage = Stage()

    init {
        stage.apply {
            title = "[dev] Rokusho viewer"
            scene = createScene(initialWidth, initialHeight)
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val libraryList = ListView(TransformedList(rokusho.libraries, ::LibraryListCell))
        val itemSetList = ListView<RecordListWatcher<ImageUrl>.Records>(recordLists).apply {
            setOnMouseClicked { e ->
                if (e.clickCount == 2) {
                    selectionModel.selectedItems[0]?.let {
                        // TODO open the view of selected `RecordListWatcher.Records`
                    }
                }
            }
        }
        return Scene(VBox(libraryList, itemSetList), w, h)
    }
    private class LibraryListCell<T>(library: RokushoLibrary<T>) : FlowPane() {
        init {
            children.addAll(Hyperlink("show").apply {
                setOnAction {
                    LibraryViewer(library).show()
                }
            }, Label(library.toString()))
        }
    }
}