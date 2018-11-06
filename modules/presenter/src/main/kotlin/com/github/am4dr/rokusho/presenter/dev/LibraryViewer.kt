package com.github.am4dr.rokusho.presenter.dev

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.metadata.BaseTag
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener
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
        val tags = library.getTags()
        val tagList = object : ObservableList<BaseTag> by FXCollections.observableArrayList(library.getTags()), SetChangeListener<BaseTag> {
            override fun onChanged(change: SetChangeListener.Change<out BaseTag>) {
                when {
                    change.wasRemoved() && change.wasAdded() ->
                        indexOfFirst { it === change.elementRemoved }
                                .takeIf { it >= 0 }
                                ?.let { index -> set(index, change.elementAdded) }
                    change.wasRemoved() -> remove(change.elementRemoved)
                    change.wasAdded() -> add(change.elementAdded)
                }
            }
        }
        tags.addListener(WeakSetChangeListener(tagList))
        val tagListView = ListView<BaseTag>(tagList)
        return Scene(VBox(tagListView), w, h)
    }
}