package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.gui.sidemenu.SideMenuPane
import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

class MainLayout(saveButton: Button, addLibraryButton: Button, filerLayout: FilerLayout, directorySelectorPane: Node, sideMenu: SideMenuPane) : BorderPane() {
    val librariesNotSelectedProperty: BooleanProperty = SimpleBooleanProperty(true)
    init {
        sideMenu.content.bind(When(librariesNotSelectedProperty)
                .then<Node>(directorySelectorPane)
                .otherwise(filerLayout))
        center = sideMenu
        top = HBox(
                Label("[仮実装]"),
                saveButton.apply { disableProperty().bind(librariesNotSelectedProperty) },
                addLibraryButton)
    }
}