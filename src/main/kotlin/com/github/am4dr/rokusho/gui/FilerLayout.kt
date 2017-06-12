package com.github.am4dr.rokusho.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class FilerLayout(filterInputNode: Node,
                  listNode: Node,
                  thumbnailNode: Node) : VBox() {
    private val selectedView = SimpleObjectProperty<Node>().apply { set(thumbnailNode) }
    private val currentNode: Node = BorderPane().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        centerProperty().bind(selectedView)
    }
    init {
        children.addAll(
                HBox(
                        Button("リスト").apply { setOnAction {
                            selectedView.set(listNode)
                        } },
                        Button("サムネイル").apply { setOnAction { selectedView.set(thumbnailNode) } },
                        Label("フィルター", filterInputNode).apply { contentDisplay = ContentDisplay.RIGHT }),
                currentNode)
    }
}