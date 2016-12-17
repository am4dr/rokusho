package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.BaseImageData
import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.core.ImageMetaData
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * tag editor for development
 */
internal class DraftTagEditor(val image: ImageData) : Stage() {
    private val tagsText = SimpleStringProperty(image.metaData.tags.joinToString(","))
    var onUpdate: (DraftTagEditor, ImageData) -> Unit = { e, data -> }
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
                                        val new = createImageData()
                                        if (new.metaData != image.metaData) { onUpdate(this@DraftTagEditor, new) }
                                    }
                                })
                           )
                      )
                      , 600.0, 400.0)
    }
    private fun createImageData(): ImageData =
        object : BaseImageData() {
            override val image: Image get() = this@DraftTagEditor.image.image
            override val tempImage: Image get() = this@DraftTagEditor.image.tempImage
            override val thumbnail: Image get() = this@DraftTagEditor.image.thumbnail
            override val tempThumbnail: Image get() = this@DraftTagEditor.image.tempThumbnail
            override val metaData: ImageMetaData =
                    ImageMetaData(tagsText.get().split(Regex("\\s*,\\s*")).filter(String::isNotBlank).toMutableList())
        }
}