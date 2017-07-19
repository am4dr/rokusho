package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.Tag
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
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
            title = "[dev] Library viewer $library"
            scene = createScene(initialWidth, initialHeight)
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val tags = library.tagRegistry.tags
        val tagList = object : ObservableList<Tag> by FXCollections.observableArrayList(library.tagRegistry.tags), SetChangeListener<Tag> {
            override fun onChanged(change: SetChangeListener.Change<out Tag>) {
                when {
                    change.wasRemoved() && change.wasAdded() -> indexOf(change.elementRemoved).takeIf { it >= 0 }?.let { set(it, change.elementAdded) }
                    change.wasRemoved() -> remove(change.elementRemoved)
                    change.wasAdded() -> add(change.elementAdded)
                }
            }
        }
        tags.addListener(WeakSetChangeListener(tagList))
        val tagListView = ListView<Tag>(tagList)
        return Scene(VBox(tagListView), w, h)
    }
}