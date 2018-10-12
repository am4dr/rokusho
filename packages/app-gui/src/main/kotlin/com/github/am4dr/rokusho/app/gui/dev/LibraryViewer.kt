package com.github.am4dr.rokusho.app.gui.dev

import com.github.am4dr.rokusho.old.core.library.Library
import com.github.am4dr.rokusho.old.core.library.Tag
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakMapChangeListener
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage

class LibraryViewer<T>(val library: Library<T>) {
    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }
    val stage = Stage()

    init {
        stage.apply {
            title = "[dev] RokushoLibrary viewer $library"
            scene = createScene(initialWidth, initialHeight)
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val tags = library.tags
        val tagList = object : ObservableList<Tag> by FXCollections.observableArrayList(library.tags.values), MapChangeListener<String, Tag> {
            override fun onChanged(change: MapChangeListener.Change<out String, out Tag>) {
                when {
                    change.wasRemoved() && change.wasAdded() -> indexOfFirst { it === change.valueRemoved }.takeIf { it >= 0 }?.let { set(it, change.valueAdded) }
                    change.wasRemoved() -> remove(change.valueRemoved)
                    change.wasAdded() -> add(change.valueAdded)
                }
            }
        }
        tags.addListener(WeakMapChangeListener(tagList))
        val tagListView = ListView<Tag>(tagList)
        return Scene(VBox(tagListView), w, h)
    }
}