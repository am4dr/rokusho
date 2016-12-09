package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImageFiler {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val imagesProperty: ListProperty<ImageData> = createEmptyListProperty()
    private val listNode = ListView<ImageData>().apply {
        itemsProperty().bind(imagesProperty)
    }
    private val thumbnailNode = ThumbnailPane(imagesProperty)
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    val node: Node = VBox(
            HBox(Label("画像ファイラー 仮置きテキスト")),
            HBox(
                Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } }),
            currentView)
}