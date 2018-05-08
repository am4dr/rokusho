package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.stage.Stage

class RokushoViewer(val rokusho: Rokusho) {
    companion object {
        const val initialWidth: Double = 300.0
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
        return Scene(libraryList, w, h)
    }

    private class LibraryListCell(library: RokushoLibrary<*>) : FlowPane() {
        init {
            children.addAll(
                    Hyperlink("tags").apply {
                        setOnAction {
                            LibraryViewer(library).show()
                        }
                    },
                    Hyperlink("records").apply {
                        setOnMouseClicked {
                            @Suppress("UNCHECKED_CAST")
                            RecordsViewer(library.records as ObservableList<Record<*>>).show()
                        }
                    },
                    Label(library.name),
                    Label(library.toString()))
        }
    }
}