package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.collections.transformation.FilteredList
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.Window
import java.io.File
import java.nio.file.Path
import java.util.function.Predicate

class RokushoGui(val rokusho: Rokusho, val stage: Stage, val addLibraryFromPath: (Path) -> Unit, val saveLibrary: (Library<*>) -> Unit) {

    val mainParent: Parent  = createMainScene()

    private fun createMainScene(): Parent {
        return MainView({ it.toSideMenuIcon() }, ::createLibraryViewer).apply {
            libraries.bindContent(rokusho.libraries)
            onSaveClickedProperty.set {
                currentLibrary.get()?.let(saveLibrary)
            }
            openLibrarySelectorProperty.set { selectLibraryDirectory() }
        }
    }

    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(window: Window = stage) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = lastSelectedDirectory
            showDialog(window)?.let {
                lastSelectedDirectory = it
                addLibraryFromPath(it.toPath())
            }
        }
    }
}

private fun Library<ImageUrl>.toSideMenuIcon(): SideMenuIcon =
        SideMenuIcon().apply {
            background = Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY))
            children.add(Label("Lib: ${System.identityHashCode(this@toSideMenuIcon)}"))
        }

private fun createLibraryViewer(library: Library<ImageUrl>): Node {
    val viewer = RecordListViewer()

    val recordFilter = { input: String? ->
        Predicate { item: Record<*> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }.bindLeft(viewer.filterProperty)
    val filteredItems = FilteredList(library.records).apply { predicateProperty().bind(recordFilter) }

    return viewer.apply {
        records.bindContent(filteredItems)
        thumbnailViewer.updateTagsProperty.set({ record, tags -> library.updateItemTags(record.key, tags) })
        totalCount.bind(Bindings.size(library.records))
        filteredCount.bind(Bindings.size(filteredItems))
    }
}
