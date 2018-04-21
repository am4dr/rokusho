package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.sidemenu.SimpleSideMenu
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.InvalidationListener
import javafx.beans.binding.When
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

class MainView<T>(private val libraryIconFactory: (Library<T>) -> SideMenuIcon,
                  private val libraryViewerFactory: (Library<T>) -> Node) : BorderPane() {

    val libraries: ReadOnlyListProperty<Library<T>> = SimpleListProperty(observableArrayList())
    val onSaveClickedProperty: ObjectProperty<() -> Unit> = SimpleObjectProperty { }
    val openLibrarySelectorProperty: ObjectProperty<() -> Unit> = SimpleObjectProperty { }

    val currentLibrary: ReadOnlyObjectProperty<Library<T>> = SimpleObjectProperty()


    private val icons = TransformedList(libraries, this::createIcon)
    private val libraryViewer = SimpleObjectProperty<Node>()
    private val sideMenu = SimpleSideMenu({ openLibrarySelectorProperty.get()?.invoke() }).apply {
        width.set(40.0)
    }

    init {
        val librariesNotSelectedProperty: ReadOnlyBooleanProperty = SimpleBooleanProperty().apply { bind(currentLibrary.isNull) }
        top = HBox(Button("保存").apply {
            setOnAction { onSaveClickedProperty.get()?.invoke() }
            disableProperty().bind(librariesNotSelectedProperty)
        })
        left = sideMenu
        icons.addListener(InvalidationListener {
            sideMenu.setIcons(icons)
        })

        val directorySelectorPane = createDirectorySelectorPane()
        centerProperty().bind(When(librariesNotSelectedProperty)
                .then<Node>(directorySelectorPane)
                .otherwise(libraryViewer))
    }
    private fun createDirectorySelectorPane(): Node = HBox(
            Label("対象とする画像があるディレクトリを選択してください: "),
            Hyperlink("選択ウィンドウを開く").apply { setOnAction { openLibrarySelectorProperty.get()?.invoke() } }
    ).apply {
        alignment = Pos.CENTER
    }

    private fun createIcon(library: Library<T>): SideMenuIcon =
            libraryIconFactory(library).apply {
                setOnMouseClicked {
                    libraryViewer.set(libraryViewerFactory(library))
                    (currentLibrary as SimpleObjectProperty).set(library)
                }
            }
}