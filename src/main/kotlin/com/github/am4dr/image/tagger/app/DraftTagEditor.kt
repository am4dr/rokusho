package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageData
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * tag editor for development
 */
internal class DraftTagEditor(val image: ImageData) : Stage() {
    private val tagsText = SimpleStringProperty(image.metaData.tags.joinToString(","))
    init {
        title = "tags editor"
        scene =
                Scene(HBox(ImageView(image.tempThumbnail),
                           VBox(Label("Tags"),
                                TextField().apply { textProperty().bindBidirectional(tagsText) },
                                HBox(Button("更新").apply {
                                    defaultButtonProperty().set(true)
                                    onAction = EventHandler { onUpdate(this@DraftTagEditor, tagsText.get()) }
                                })
                           )
                      )
                      , 600.0, 400.0)
    }
    fun update() {
        image.metaData.tags.clear()
        val tags = tagsText.get().split(Regex("\\s*,\\s*")).filter(String::isNotBlank)
        image.metaData.tags.addAll(tags)
    }
    var onUpdate: (DraftTagEditor, String) -> Unit = { e, str -> }
}