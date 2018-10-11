package com.github.am4dr.rokusho.gui.scene

import com.github.am4dr.rokusho.gui.sidemenu.SimpleSideMenu
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

class MainPane : BorderPane() {

    val addLibraryEventHandler: ObjectProperty<(() -> Unit)?> = SimpleObjectProperty()
    val showAddLibrarySuggestion: BooleanProperty = SimpleBooleanProperty(true)
    val sideMenu: ObjectProperty<Node?> = SimpleObjectProperty()
    val libraryViewer: ObjectProperty<Node?> = SimpleObjectProperty()

    private val directorySelectorPane = createDirectorySelectorPane()

    init {
        leftProperty().bind(When(sideMenu.isNotNull)
                .then(sideMenu)
                .otherwise(object : ObjectBinding<Node>() {
                    override fun computeValue(): Node = createDefaultSideMenu()
                }))
        centerProperty().bind(When(showAddLibrarySuggestion)
                .then(directorySelectorPane)
                .otherwise(When(libraryViewer.isNotNull)
                        .then(libraryViewer)
                        .otherwise(Pane())))
    }

    private fun createDefaultSideMenu(): Node =
            SimpleSideMenu().apply {
                onAddClicked.set { addLibraryEventHandler.get()?.invoke() }
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