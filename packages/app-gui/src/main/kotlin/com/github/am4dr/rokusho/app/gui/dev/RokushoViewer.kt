package com.github.am4dr.rokusho.app.gui.dev

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.old.core.library.Library
import com.github.am4dr.rokusho.old.core.library.Record
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
            scene = createScene(initialWidth, initialHeight)
        }
    }

    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val libraryList = ListView(TransformedList(libraries, RokushoViewer::LibraryListCell))
        return Scene(libraryList, w, h)
    }

    private class LibraryListCell(library: Library<*>) : FlowPane() {
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