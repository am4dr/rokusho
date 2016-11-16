package com.github.am4dr.image.tagger.app

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImageFiler(val mainFrame: MainFrame) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val pane: Pane =
        VBox(Label("画像ファイラー 仮置きテキスト"),
             ListView<ImageData>().apply {
                 VBox.setVgrow(this, Priority.ALWAYS)
                 itemsProperty().bind(SimpleObjectProperty(mainFrame.imagesProperty)) })
}