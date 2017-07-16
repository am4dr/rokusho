package com.github.am4dr.rokusho.dev.gui

import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.Tag
import javafx.beans.binding.ListBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MetaDataRegistryViewer<T>(val registry: MetaDataRegistry<T>) {
    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }
    val stage = Stage()

    init {
        stage.apply {
            title = "[dev] MetaDataRegistry viewer $registry"
            scene = createScene(initialWidth, initialHeight)
        }
    }
    fun show() = stage.show()
    private fun createScene(w: Double, h: Double): Scene {
        val tags = registry.getTags()
        val tagList = object : ListBinding<Tag>() {
            init { super.bind(tags) }
            override fun computeValue(): ObservableList<Tag> = FXCollections.observableList(tags.values.toList())
        }
        val tagListView = ListView<Tag>(tagList)
        return Scene(VBox(tagListView), w, h)
    }
}