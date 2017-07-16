package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class RokushoViewer(val rokusho: Rokusho) {
    private val metaDataRegistries = TransformedList(rokusho.libraries, Library<ImageUrl>::metaDataRegistry)
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
        val libraryList = ListView(rokusho.libraries)
        val registryList = ListView(TransformedList(metaDataRegistries, ::MetaDataRegistryListCell))
        val itemSetList = ListView<ObservableRecordList<ImageUrl>>(recordLists).apply {
            setOnMouseClicked { e ->
                if (e.clickCount == 2) {
                    selectionModel.selectedItems[0]?.let {
                        // TODO open the view of selected ObservableRecordList
                    }
                }
            }
        }
        return Scene(VBox(libraryList, registryList, itemSetList), w, h)
    }
    private class MetaDataRegistryListCell<T>(registry: MetaDataRegistry<T>) : FlowPane() {
        init {
            children.addAll(Hyperlink("show").apply {
                setOnAction {
                    MetaDataRegistryViewer(registry).show()
                }
            }, Label(registry.toString()))
        }
    }
}