package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.core.library.Library
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ListView
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

// TODO remove save button
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
    val viewer = RecordsViewerContainer<ImageUrl>()

    val recordFilter = { input: String? ->
        Predicate { item: Record<*> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }.bindLeft(viewer.filterProperty)

    viewer.apply {
        records.bind(SimpleListProperty(FilteredList(library.records).apply { predicateProperty().bind(recordFilter) }))
        totalCount.bind(Bindings.size(library.records))
        filteredCount.bind(Bindings.size(records))
    }

    val listViewer = ListView<Record<Any>>()
    Bindings.bindContent(listViewer.items, viewer.records)
    viewer.add("リスト", listViewer)

    val imageLoader = UrlImageLoader()
    val thumbnailPane = RecordThumbnailViewer<ImageUrl>({ record, width, height ->
        ImageThumbnail(imageLoader.getImage(record.key.url, width, height, true))
    })
    val (thumbnailViewer) = ImageOverlay.attach(thumbnailPane).also { (_, thumbnailPane, overlay) ->
        thumbnailPane.apply {
            records.bindContent(viewer.records)
            onActionProperty.set {
                overlay.imageProperty.value = imageLoader.getImage(it.first().key.url)
                overlay.isVisible = true
            }
            updateTagsProperty.set({ record, tags -> library.updateItemTags(record.key, tags) })
        }
        overlay.apply {
            isVisible = false
            onMouseClicked = EventHandler { isVisible = false }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
    }
    viewer.add("サムネイル", thumbnailViewer)

    return viewer
}