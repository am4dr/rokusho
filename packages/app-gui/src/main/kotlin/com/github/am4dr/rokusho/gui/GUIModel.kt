package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.gui.old.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.util.concurrent.Callable

class GUIModel(val libraryCollection: LibraryCollection,
               private val libraryViewerRepository: LibraryViewerRepository) {

    val libraryIcons: ObservableList<SideMenuIcon> = TransformedList(libraryCollection.libraries) { library ->
        library.toSideMenuIcon().apply {
            setOnMouseClicked {
                libraryCollection.select(library)
            }
            selectedProperty.bind(libraryCollection.selectedProperty().isEqualTo(library))
        }
    }

    val currentLibraryViewer: ObservableValue<Node?> = Bindings.createObjectBinding({
        libraryCollection.selectedProperty().get()?.let(libraryViewerRepository::get)
    }, arrayOf(libraryCollection.selectedProperty()))

    // TODO separate data and styling
    private fun RokushoLibrary<*>.toSideMenuIcon(): SideMenuIcon =
            SideMenuIcon().apply {
                Tooltip.install(this, Tooltip(name))
                backgroundProperty().bind(When(selectedProperty)
                        .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                        .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(4.0), Insets.EMPTY))))
                val firstLetter = Label(this@toSideMenuIcon.shortName.first().toString()).apply {
                    fontProperty().bind(Bindings.createObjectBinding(Callable { Font(size.get() * 0.7) }, size))
                }
                AnchorPane.setTopAnchor(firstLetter, 0.0)
                AnchorPane.setLeftAnchor(firstLetter, 6.0)
                val libLabel = Label("Lib:")
                AnchorPane.setTopAnchor(libLabel, 0.0)
                AnchorPane.setLeftAnchor(libLabel, 0.0)
                val labels = AnchorPane(firstLetter, libLabel)
                children.add(labels)
            }
}