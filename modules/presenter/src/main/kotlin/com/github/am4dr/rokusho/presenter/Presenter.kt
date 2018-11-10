package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.core.library.Library
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.ObjectExpression
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import java.nio.file.Path

class Presenter(
    val libraries: ObservableList<Library<*>> = FXCollections.observableArrayList(),
    viewerFactory: LibraryItemListViewerFactory,
    val addLibraryByPath: (Path) -> Library<*>?,
    val pathChooser: () -> Path?
) {

    private val selector: LibrarySelector = LibrarySelector(libraries)
    val selectedLibrary: ObjectExpression<Library<*>?> = selector.selectedProperty()
    val selectedViewer: ObjectExpression<Node?> = createObjectBinding(
        { getOrCreateViewer()?.node },
        arrayOf(selectedLibrary)
    )
    private val viewerCache = CachedLibraryItemListViewerFactory(viewerFactory)

    private fun getOrCreateViewer(): LibraryItemListViewer<*>? {
        return selector.selectedProperty().get()?.let {
            viewerCache.create(it)
        }
    }

    fun select(library: Library<*>) {
        selector.select(library)
    }

    fun chooseAndAddLibraryByPath() {
        pathChooser()?.let(addLibraryByPath)
    }
}