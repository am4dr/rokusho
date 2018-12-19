package com.github.am4dr.rokusho.presenter.scene

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.javafx.sidemenu.CharacterIcon
import com.github.am4dr.rokusho.javafx.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.javafx.sidemenu.SimpleSideMenu
import com.github.am4dr.rokusho.library.Library
import javafx.beans.binding.ObjectExpression
import javafx.beans.binding.When
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

class CharacterIconSideMenu(
    private val libraries: ObservableList<Library<*>>,
    private val selectLibrary: (Library<*>) -> Unit,
    private val selectedLibrary: ObjectExpression<Library<*>?>,
    private val openLibraryChooserAndAddLibrary: () -> Unit,
    iconFactory: (Library<*>) -> SideMenuIcon = Companion::createCharacterIcon
) : StackPane() {

    companion object {
        fun createCharacterIcon(library: Library<*>): SideMenuIcon =
            CharacterIcon().apply {
                Tooltip.install(this, Tooltip(library.name))
                backgroundProperty().bind(
                    When(selectedProperty)
                        .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                        .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(8.0), Insets.EMPTY)))
                )
                character.set(library.shortName.first().toString())
            }
    }

    private val icons = TransformedList(libraries) { library ->
        iconFactory(library).apply {
            setOnMouseClicked {
                selectLibrary(library)
            }
            selectedProperty.bind(selectedLibrary.isEqualTo(library))
        }
    }
    private val sideMenu = SimpleSideMenu(icons).apply {
        width.value = 40.0
        onAddClicked.set {
            openLibraryChooserAndAddLibrary()
        }
    }

    init {
        children.add(sideMenu)
    }
}