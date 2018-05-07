package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.library.RokushoLibrary
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.When
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.Window
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.function.Predicate

// TODO remove save button
class RokushoGui(val rokusho: Rokusho, val stage: Stage, val addLibraryFromPath: (Path) -> Unit, val saveLibrary: (RokushoLibrary<*>) -> Unit) {

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

// TODO move into SideMenuIcon and redesign
private fun RokushoLibrary<ImageUrl>.toSideMenuIcon(): SideMenuIcon =
        SideMenuIcon().apply {
            Tooltip.install(this, Tooltip(name))
            backgroundProperty().bind(When(selectedProperty)
                    .then(Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY)))
                    .otherwise(Background(BackgroundFill(Color.ANTIQUEWHITE, CornerRadii(4.0), Insets.EMPTY))))
            val firstLetter = Label(this@toSideMenuIcon.shortName.first().toString()).apply {
                fontProperty().bind(createObjectBinding(Callable { Font(size.get() * 0.7) }, size))
            }
            AnchorPane.setTopAnchor(firstLetter, 0.0)
            AnchorPane.setLeftAnchor(firstLetter, 6.0)
            val libLabel = Label("Lib:")
            AnchorPane.setTopAnchor(libLabel, 0.0)
            AnchorPane.setLeftAnchor(libLabel, 0.0)
            val labels = AnchorPane(firstLetter, libLabel)
            children.add(labels)
        }

private val byTagNameRecordFilterFactory = { input: String? ->
    Predicate { item: Record<*> ->
        if (input == null || input == "") true
        else item.itemTags.any { it.tag.id.contains(input) }
    }
}

private fun createLibraryViewer(library: RokushoLibrary<ImageUrl>): Node = RecordsViewerContainer<ImageUrl>().apply {
    records.bind(SimpleListProperty(FilteredList(library.records).apply {
        val recordFilter = byTagNameRecordFilterFactory.bindLeft(filterProperty)
        predicateProperty().bind(recordFilter)
    }))
    totalCount.bind(Bindings.size(library.records))
    filteredCount.bind(Bindings.size(records))

    add("リスト", createListRecordsViewer(this))
    add("サムネイル", createThumbnailRecordsViewer(library, this))
}

private fun <T> createListRecordsViewer(container: RecordsViewerContainer<T>): Node =
        ListView<Record<T>>().also { Bindings.bindContent(it.items, container.records) }

private fun createThumbnailRecordsViewer(library: RokushoLibrary<ImageUrl>, container: RecordsViewerContainer<ImageUrl>): Node {
    val imageLoader = UrlImageLoader()
    val thumbnailMaxWidth = 500.0
    val thumbnailMaxHeight = 200.0
    val thumbnailPane = RecordThumbnailViewer<ImageUrl>({ record ->
        ImageThumbnail(imageLoader.getImage(record.key.url, thumbnailMaxWidth, thumbnailMaxHeight, true))
    })
    val (thumbnailViewer) = ImageOverlay.attach(thumbnailPane).also { (_, thumbnailPane, overlay) ->
        thumbnailPane.apply {
            records.bindContent(container.records)
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
    return thumbnailViewer
}