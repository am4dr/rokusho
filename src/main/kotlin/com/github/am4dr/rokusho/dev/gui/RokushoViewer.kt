package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage

class RokushoViewer(val rokusho: Rokusho) {
    private val metaDataRegistries = rokusho.metaDataRegistries
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
        val libraryList = ListView<MetaDataRegistry<ImageUrl>>(metaDataRegistries)
        val itemSetList = ListView<ObservableRecordList<ImageUrl>>(recordLists).apply {
            setOnMouseClicked { e ->
                if (e.clickCount == 2) {
                    selectionModel.selectedItems[0]?.let {
                        // TODO open the view of selected ObservableRecordList
                    }
                }
            }
        }
        val vbox = VBox(libraryList, itemSetList)
        return Scene(vbox, w, h)
    }
}