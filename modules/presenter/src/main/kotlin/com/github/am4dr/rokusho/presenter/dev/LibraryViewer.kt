package com.github.am4dr.rokusho.presenter.dev

import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.LibraryItemTagTemplate
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage

class LibraryViewer<T : Any>(val library: Library<T>) {

    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }

    val stage = Stage()
    private val tagList = FXCollections.observableArrayList<LibraryItemTagTemplate>()

    init {
        stage.apply {
            title = "[dev] RokushoLibrary viewer $library"
            scene = createScene(
                initialWidth,
                initialHeight
            )
        }
    }

    fun show() = stage.show()

    private fun createScene(w: Double, h: Double): Scene {
        library.subscribeFor(this) { event, viewer ->
            runLater {
                val list = viewer.tagList
                when (event) {
                    is Library.Event.AddTag -> list.add(event.tag)
                    is Library.Event.RemoveTag -> list.remove(event.tag)
                    is Library.Event.UpdateTag -> {
                        val tag = event.tag
                        list.indexOfFirst { it === tag }
                            .takeIf { it >= 0 }
                            ?.let { index -> list.set(index, tag) }
                    }
                }
            }
        }
        tagList.addAll(library.getTags())
        val tagListView = ListView<LibraryItemTagTemplate>(tagList)
        return Scene(VBox(tagListView), w, h)
    }
}