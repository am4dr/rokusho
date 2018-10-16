package com.github.am4dr.rokusho.app.gui.dev

import com.github.am4dr.rokusho.core.library.LibraryItem
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.stage.Stage

class RecordsViewer(val items: ObservableList<out LibraryItem<out Any>>) {
    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }
    val stage = Stage()

    init {
        stage.apply {
            title = "[dev] RecordListWatcher<*>.Records viewer $items"
            scene = createScene(initialWidth, initialHeight)
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val recordList = object : ObservableList<Any> by FXCollections.observableArrayList(items), ListChangeListener<Any> {
            override fun onChanged(change: ListChangeListener.Change<out Any>) {
                while (change.next()) {
                    removeAll(change.removed)
                    addAll(change.addedSubList)
                }
            }
        }
        items.addListener(WeakListChangeListener(recordList))
        return Scene(ListView<Any>(recordList), w, h)
    }
}