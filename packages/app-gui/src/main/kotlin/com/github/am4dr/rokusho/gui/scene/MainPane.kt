package com.github.am4dr.rokusho.gui.scene

import com.github.am4dr.rokusho.gui.old.sidemenu.SimpleSideMenu
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

class MainPane : BorderPane() {

    val addLibraryEventHandler: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()
    val showAddLibrarySuggestion: BooleanProperty = SimpleBooleanProperty(true)
    val sideMenu: ObjectProperty<Node?> = SimpleObjectProperty()
    val libraryViewer: ObjectProperty<Node?> = SimpleObjectProperty()

    private val directorySelectorPane = createDirectorySelectorPane()

    init {
        top = createTopPane()
        leftProperty().bind(When(sideMenu.isNotNull)
                .then(sideMenu)
                .otherwise(object : ObjectBinding<Node>() {
                    override fun computeValue(): Node = createSideMenu()
                }))
        centerProperty().bind(When(showAddLibrarySuggestion)
                .then(directorySelectorPane)
                .otherwise(When(libraryViewer.isNotNull)
                        .then(libraryViewer)
                        .otherwise(Pane())))
    }

    private fun createTopPane(): Node =
            FlowPane(Orientation.HORIZONTAL,
                    Button("追加").apply {
                        setOnAction { addLibraryEventHandler.get()?.invoke() }
                    }
            )

    private fun createSideMenu(): Node =
            SimpleSideMenu { addLibraryEventHandler.get()?.invoke() }.apply {
                width.set(40.0)
            }

    private fun createDirectorySelectorPane(): Node =
            HBox(
                    Label("対象とする画像があるディレクトリを選択してください: "),
                    Hyperlink("選択ウィンドウを開く").apply {
                        setOnAction { addLibraryEventHandler.get()?.invoke() }
                    }
            ).apply { alignment = Pos.CENTER }
}