package com.github.am4dr.rokusho.gui

import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

class MainScene(
        onAddLibraryClicked: () -> Unit,
        onSaveClicked: () -> Unit,
        val filer: ImageFiler, val directorySelectorPane: Node) : BorderPane() {
    val librariesNotSelectedProperty: BooleanProperty = SimpleBooleanProperty(true)
    init {
        centerProperty().bind(
                When(librariesNotSelectedProperty)
                        .then<Node>(directorySelectorPane)
                        .otherwise(filer))
        top = HBox(
                Label("[仮実装]"),
                Button("保存").apply {
                    setOnAction { onSaveClicked() }
                    disableProperty().bind(librariesNotSelectedProperty)
                },
                Button("追加").apply { setOnAction { onAddLibraryClicked() } })
    }
}