package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.gui.old.sidemenu.CharacterIcon
import com.github.am4dr.rokusho.gui.old.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color

class GUIModel(val libraryCollection: LibraryCollection,
               private val libraryViewerCache: LibraryViewerCache) {

    val libraryIcons: ObservableList<SideMenuIcon> = TransformedList(libraryCollection.libraries) { library ->
        library.toSideMenuIcon().apply {
            setOnMouseClicked {
                libraryCollection.select(library)
            }
            selectedProperty.bind(libraryCollection.selectedProperty().isEqualTo(library))
        }
    }

    val currentLibraryViewer: ObservableValue<Node?> = Bindings.createObjectBinding({
        libraryCollection.selectedProperty().get()?.let(this::getOrCreateLibraryViewer)?.node
    }, arrayOf(libraryCollection.selectedProperty()))

    private fun getOrCreateLibraryViewer(library: RokushoLibrary<*>): LibraryViewer<*> {
        libraryViewerCache.getOrNull(library)?.let { return it }
        return libraryViewerCache.getOrCreate(library).also {
            Bindings.bindContent(it.records, library.records)
        }
    }

    private fun RokushoLibrary<*>.toSideMenuIcon(): SideMenuIcon =
            CharacterIcon().apply {
                Tooltip.install(this, Tooltip(name))
                backgroundProperty().bind(When(selectedProperty)
                        .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                        .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(4.0), Insets.EMPTY))))
                character.set(this@toSideMenuIcon.shortName.first().toString())
            }
}