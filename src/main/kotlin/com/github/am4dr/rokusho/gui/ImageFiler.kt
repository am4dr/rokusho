package com.github.am4dr.rokusho.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class ImageFiler(
        filterInputNode: Node,
        listNode: ListNode,
        thumbnailNode: ThumbnailNode) : VBox() {
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentNode: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    init {
        children.addAll(
                HBox(
                        Button("リスト").apply { onAction = EventHandler {
                            selectedView.set(listNode)
                            listNode.refresh()
                        } },
                        Button("サムネイル").apply { onAction = EventHandler { selectedView.set(thumbnailNode) } },
                        Label("フィルター", filterInputNode).apply { contentDisplay = ContentDisplay.RIGHT}),
                currentNode)
    }
}