package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Picture
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class ImageFiler(
        val picturesProperty: ReadOnlyListProperty<Picture>,
        val listNode: ListView<Picture>,
        val thumbnailNode: ThumbnailPane) : VBox() {
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentView: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    init {
        listNode.itemsProperty().bind(picturesProperty)
        thumbnailNode.picturesProperty.bind(picturesProperty)
        children.addAll(
                HBox(
                        Button("リスト").apply { onAction = EventHandler { selectedView.set(listNode) } },
                        Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } }),
                currentView)
    }
}