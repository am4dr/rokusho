package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Tag
import com.github.am4dr.image.tagger.core.TagParser
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
internal class DraftMetaDataEditor(val metaData: ImageMetaData, val image: Image? = null) : Stage() {
    private val tagsText = SimpleStringProperty(metaData.tags.map(Tag::text).joinToString(","))
    var onUpdate: (ImageMetaData) -> Unit = {}
    init {
        title = "tags editor"
        val root = HBox()
        image?.let { root.children.add(ImageView(it)) }
        root.children.add(
                VBox(Label("Tags"),
                        TextField().apply { textProperty().bindBidirectional(tagsText) },
                        HBox(Button("更新").apply {
                            // TODO 変更がない場合は無効化
                            defaultButtonProperty().set(true)
                            onAction = EventHandler {
                                val new = createImageMetaData()
                                if (new != metaData) { onUpdate(new) }
                            }
                        })
                )
        )
        scene = Scene(root, 600.0, 400.0)
    }
    private fun createImageMetaData(): ImageMetaData =
            tagsText.get()
                    .split(Regex("\\s*,\\s*"))
                    .filter(String::isNotBlank)
                    .map { TagParser.parse(it) }
                    .let(::ImageMetaData)
}