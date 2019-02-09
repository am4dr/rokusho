package com.github.am4dr.rokusho.presenter.dev

import com.github.am4dr.rokusho.library.LibraryItem
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.stage.Stage

class RecordsViewer(
    val name: String,
    val items: ObservableList<out LibraryItem<*>>
) {

    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }

    val stage = Stage()

    init {

        stage.apply {
            title = "[dev] ItemListViewer - $name"
            scene = createScene(
                initialWidth,
                initialHeight
            )
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        return Scene(ListView(items), w, h)
    }
}