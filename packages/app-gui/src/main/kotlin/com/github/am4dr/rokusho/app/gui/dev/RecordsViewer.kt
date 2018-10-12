package com.github.am4dr.rokusho.app.gui.dev

import com.github.am4dr.rokusho.old.core.library.Record
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.stage.Stage

class RecordsViewer<T>(val records: ObservableList<Record<T>>) {
    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }
    val stage = Stage()

    init {
        stage.apply {
            title = "[dev] RecordListWatcher<T>.Records viewer $records"
            scene = createScene(initialWidth, initialHeight)
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val recordList = object : ObservableList<Record<T>> by FXCollections.observableArrayList(records), ListChangeListener<Record<T>> {
            override fun onChanged(change: ListChangeListener.Change<out Record<T>>) {
                while (change.next()) {
                    removeAll(change.removed)
                    addAll(change.addedSubList)
                }
            }
        }
        records.addListener(WeakListChangeListener(recordList))
        return Scene(ListView<Record<T>>(recordList), w, h)
    }
}