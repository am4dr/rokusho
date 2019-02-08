package com.github.am4dr.rokusho.presenter.dev

import com.github.am4dr.rokusho.library2.Library
import com.github.am4dr.rokusho.library2.LoadedLibrary
import com.github.am4dr.rokusho.library2.Tag
import com.github.am4dr.rokusho.library2.addOrReplaceEntity
import javafx.application.Platform.runLater
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage

class LibraryViewer(
    val library: LoadedLibrary
) {

    companion object {
        const val initialWidth: Double  = 300.0
        const val initialHeight: Double = 300.0
    }

    val stage = Stage()
    private val tagList = FXCollections.observableArrayList<Tag>()

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
        library.library.getDataAndSubscribe { (tags) ->
            tagList.addAll(tags)
            subscribeFor(this@LibraryViewer) { event, viewer ->
                runLater {
                    val list = viewer.tagList
                    when (event) {
                        is Library.Event.ItemEvent -> {}
                        is Library.Event.TagEvent -> when (event) {
                            is Library.Event.TagEvent.Loaded,
                            is Library.Event.TagEvent.Added -> list.add(event.tag)
                            is Library.Event.TagEvent.Removed -> list.removeAll { it.isSameEntity(event.tag) }
                            is Library.Event.TagEvent.Updated -> list.addOrReplaceEntity(event.tag)
                        }.let { /* 網羅性チェック */ }
                    }.let { /* 網羅性チェック */ }
                }
            }
        }
        val tagListView = ListView<Tag>(tagList)
        return Scene(VBox(tagListView), w, h)
    }
}