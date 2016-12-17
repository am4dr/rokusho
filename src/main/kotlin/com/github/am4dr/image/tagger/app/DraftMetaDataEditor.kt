package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.core.ImageMetaData
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
internal class DraftMetaDataEditor(val image: ImageData) : Stage() {
    private val tagsText = SimpleStringProperty(image.metaData.tags.joinToString(","))
    var onUpdate: (ImageMetaData) -> Unit = {}
    init {
        title = "tags editor"
        scene =
                Scene(HBox(ImageView(image.tempThumbnail),
                           VBox(Label("Tags"),
                                TextField().apply { textProperty().bindBidirectional(tagsText) },
                                HBox(Button("更新").apply {
                                    // TODO 変更がない場合は無効化
                                    defaultButtonProperty().set(true)
                                    onAction = EventHandler {
                                        val new = ImageMetaData(tagsText.get().split(Regex("\\s*,\\s*")).filter(String::isNotBlank).toMutableList())
                                        if (new != image.metaData) { onUpdate(new) }
                                    }
                                })
                           )
                      )
                      , 600.0, 400.0)
    }
}