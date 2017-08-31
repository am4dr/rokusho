package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.RecordListWatcher
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage

class RecordsViewer<T>(val records: RecordListWatcher<T>.Records) {
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
        val recordList = object : ObservableList<Record<T>> by FXCollections.observableArrayList(records.records), ListChangeListener<Record<T>> {
            override fun onChanged(change: ListChangeListener.Change<out Record<T>>) {
                while (change.next()) {
                    removeAll(change.removed)
                    addAll(change.addedSubList)
                }
            }
        }
        records.records.addListener(WeakListChangeListener(recordList))
        return Scene(VBox(ListView<Record<T>>(recordList)), w, h)
    }
}