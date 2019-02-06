package com.github.am4dr.rokusho.presenter

import com.github.am4dr.rokusho.library2.LoadedLibrary
import javafx.beans.binding.Bindings.bindContent
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.ObjectExpression
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import java.nio.file.Path

class Presenter(
    val libraries: ObservableList<LoadedLibrary> = FXCollections.observableArrayList(),
    viewerFactory: ItemListViewerFactory2,
    val addLibraryByPath: (Path) -> LoadedLibrary?,
    val pathChooser: () -> Path?
) {

    private val selector: LibrarySelector = LibrarySelector(libraries)
    val selectedLibrary: ObjectExpression<LoadedLibrary?> = selector.selectedProperty()
    val selectedViewer: ObjectExpression<Node?> = createObjectBinding(
        { getOrCreateViewer()?.node },
        arrayOf(selectedLibrary)
    )
    private val itemLists = ItemModelConverter(libraries)
    private val viewerCache = CachedLibraryViewerFactory(viewerFactory)

    private fun getOrCreateViewer(): ItemListViewer? =
        selector.selectedProperty().get()?.let { library ->
            viewerCache.getOrNull(library)?.let { return it }
            viewerCache.getOrCreate(library).apply {
                bindContent(items, itemLists.getOrCreate(library))
            }
        }

    fun select(library: LoadedLibrary) {
        selector.select(library)
    }

    fun chooseAndAddLibraryByPath() {
        pathChooser()?.let(addLibraryByPath)
    }
}