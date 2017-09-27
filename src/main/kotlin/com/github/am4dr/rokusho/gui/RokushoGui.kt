package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.app.RokushoLibrary
import com.github.am4dr.rokusho.core.library.*
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuItem
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuPane
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.ThumbnailFlowPane
import com.github.am4dr.rokusho.javafx.collection.ConcatenatedList
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.Window
import java.io.File
import java.lang.ref.SoftReference
import java.util.*
import java.util.function.Predicate

class RokushoGui(val rokusho: Rokusho, val stage: Stage) {
    private val recordLists = SimpleListProperty(rokusho.recordLists)
    private val allRecords = ConcatenatedList<Record<ImageUrl>>(TransformedList(recordLists, RecordListWatcher<ImageUrl>.Records::records))
    private val currentRecords = SimpleListProperty<Record<ImageUrl>>(FXCollections.observableArrayList()).apply { bindContent(allRecords) }
    val mainParent: Parent = createMainScene()

    private fun createMainScene(): MainLayout {
        val saveButton = Button("保存").apply {
            setOnAction { rokusho.save() }
        }
        val addLibraryButton = Button("追加").apply {
            setOnAction { selectLibraryDirectory(stage) }
        }
        val filer = createImageFiler(currentRecords)
        val sideMenu = createSideMenu()
        return MainLayout(saveButton, addLibraryButton, filer, makeDirectorySelectorPane(stage), sideMenu).apply {
            librariesNotSelectedProperty.bind(Bindings.isEmpty(currentRecords))
        }
    }
    private fun createSideMenu(): SideMenuPane {
        val icon = SideMenuIcon().apply {
            border = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths(2.0)))
            children.add(Label("Lib"))
        }
        val expansion = createRecordsListView()
        return SideMenuPane().apply {
            items.add(SideMenuItem(icon, expansion))
            showExpansion.value = false
        }
    }
    private fun createRecordsListView(): Pane {
        val transformed = TransformedList(recordLists) { r ->
            RecordsListCell(r.toString()).apply {
                setOnMouseClicked {
                    currentRecords.bindContent(r.records)
                }
            }
        }
        val allCell = RecordsListCell("全て").apply {
            setOnMouseClicked {
                currentRecords.bindContent(allRecords)
            }
        }
        val cells = ConcatenatedList.concat(FXCollections.singletonObservableList(allCell), transformed)
        val listView = ListView(cells).apply { VBox.setVgrow(this, Priority.ALWAYS)}
        return VBox(Label("Record Lists"), listView)
    }
    // TODO ImageFilerNode クラスに切り出し
    private fun createImageFiler(records: ObservableList<Record<ImageUrl>>): FilerLayout {
        val filterInput = TextField()
        val recordFilter = SimpleObservableFilter<String, Record<ImageUrl>> { input ->
            { item ->
                if (input == null || input == "") true
                else item.itemTags.any { it.tag.id.contains(input) }
            }
        }
        recordFilter.inputProperty.bind(filterInput.textProperty())
        val filteredItems = FilteredList(records).apply {
            predicateProperty().bind(createObjectBinding({ Predicate(recordFilter.filterProperty.value) }, arrayOf(recordFilter.filterProperty)))
        }
        val listNode = ListView(filteredItems)
        return FilerLayout(filterInput, listNode, createThumbnailNode(filteredItems), Bindings.size(records), Bindings.size(filteredItems))
    }
    // TODO ThumbnailNode クラスに切り出し
    private fun createThumbnailNode(records: ObservableList<Record<ImageUrl>>): Node {
        val overlay = ImageOverlay().apply {
            isVisible = false
            onMouseClicked = EventHandler { isVisible = false }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }

        val imageLoader = UrlImageLoader()
        // TODO Libraryの内容を反映するようなparserを実装する
        val parser = { text: String -> ItemTag(SimpleTag(text, TagType.TEXT, mapOf("value" to text)), null) }
        val defaultTagNodeFactory = { tag: ItemTag -> TextTagNode(tag.tag.id) }
        val libraryToTagNodeFactory = WeakHashMap(mutableMapOf<RokushoLibrary<ImageUrl>, TagNodeFactory>())
        val thumbnailCache = WeakHashMap(mutableMapOf<Record<ImageUrl>, SoftReference<ThumbnailFlowPane.Thumbnail>>())
        fun createThumbnail(record: Record<ImageUrl>): ThumbnailFlowPane.Thumbnail {
            thumbnailCache[record]?.get()?.let { return it }

            val image = imageLoader.getImage(record.key.url, 500.0, 200.0, true)

            val tagNodeFactory = rokusho.getLibrary(record)?.let { lib ->
                return@let libraryToTagNodeFactory.getOrPut(lib, { TagNodeFactory(SimpleMapProperty(lib.tags)) })::createTagNode
            } ?: defaultTagNodeFactory

            return ImageThumbnail(image, parser, tagNodeFactory).apply {
                setTags(record.itemTags)
                tags.addListener({ _, _, new -> rokusho.updateItemTags(record, new) })
                view.onMouseClicked = EventHandler {
                    overlay.imageProperty.value = imageLoader.getImage(record.key.url)
                    overlay.isVisible = true
                }
                thumbnailCache[record] = SoftReference(this as ThumbnailFlowPane.Thumbnail)
            }
        }
        val pane = ThumbnailFlowPane().apply {
            thumbnails.value = TransformedList(records, ::createThumbnail)
        }
        return StackPane(pane, overlay)
    }
    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(window: Window) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = lastSelectedDirectory
            showDialog(window)?.let {
                lastSelectedDirectory = it
                rokusho.addDirectory(it.toPath())
            }
        }
    }
    private fun makeDirectorySelectorPane(window: Window): Pane {
        val link = Hyperlink("選択ウィンドウを開く")
        link.onAction = EventHandler { selectLibraryDirectory(window) }
        return HBox(Label("対象とする画像があるディレクトリを選択してください: "), link).apply {
            alignment = Pos.CENTER
        }
    }
}