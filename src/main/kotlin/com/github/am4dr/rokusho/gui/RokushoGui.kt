package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.MetaDataRegistry
import com.github.am4dr.rokusho.core.library.ObservableRecordList
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.util.ConcatenatedList
import com.github.am4dr.rokusho.util.TransformedList
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.SimpleListProperty
import javafx.beans.value.ObservableObjectValue
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
import java.util.function.Predicate

class RokushoGui(val rokusho: Rokusho, val stage: Stage) {
    private val recordLists = SimpleListProperty(rokusho.recordLists)
    private val allRecords = ConcatenatedList<Record<ImageUrl>>(TransformedList(recordLists, ObservableRecordList<ImageUrl>::records))
    val mainParent: Parent = createMainScene()

    private fun createMainScene(): MainLayout {
        val saveButton = Button("保存").apply {
            setOnAction { /* TODO onSaveClicked() */ }
        }
        val addLibraryButton = Button("追加").apply {
            setOnAction { selectLibraryDirectory(stage) }
        }
        val filer = createImageFiler(allRecords)
        return MainLayout(saveButton, addLibraryButton, filer, makeDirectorySelectorPane(stage)).apply {
            librariesNotSelectedProperty.bind(Bindings.isEmpty(allRecords))
        }
    }
    // TODO ImageFilerNode クラスに切り出し
    private fun createImageFiler(records: ObservableList<Record<ImageUrl>>): FilerLayout {
        val filterInput = TextField()
        val recordFilter = SimpleObservableFilter<String, Record<ImageUrl>> { input ->
            { item ->
                if (input == null || input == "") true
                else item.itemTags.any { it.name.contains(input) }
            }
        }
        recordFilter.inputProperty.bind(filterInput.textProperty())
        val filteredItems = FilteredList(records).apply {
            predicateProperty().bind(createObjectBinding({ Predicate(recordFilter.filterProperty.value) }, arrayOf(recordFilter.filterProperty)))
        }
        val listNode = ListView(filteredItems)
        val thumbnailFilter = SimpleObservableFilter<String, Thumbnail> { input ->
            { t ->
                if (input == null || input == "") true
                else t.tags.any { it.name.contains(input) }
            }
        }
        thumbnailFilter.inputProperty.bind(filterInput.textProperty())
        return FilerLayout(filterInput, listNode, createThumbnailNode(records, thumbnailFilter.filterProperty), Bindings.size(records), Bindings.size(filteredItems))
    }
    // TODO ThumbnailNode クラスに切り出し
    private fun createThumbnailNode(records: ObservableList<Record<ImageUrl>>, filter: ObservableObjectValue<(Thumbnail) -> Boolean>): Node {
        val overlay = ImageOverlay().apply {
            isVisible = false
            onMouseClicked = EventHandler { isVisible = false }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }
        val layout = ThumbnailLayout(listOf(), filter)
        val imageLoader = UrlImageLoader()
        // TODO Libraryの内容を反映するようなparserを実装する
        val parser = { text: String -> ItemTag(text, text) }
        val defaultTagNodeFactory = { tag: ItemTag -> TextTagNode(tag.name) }
        val registryToTagNodeFactory = mutableMapOf<MetaDataRegistry<ImageUrl>, TagNodeFactory>()
        val thumbnails = TransformedList(records) { item ->
            val image = imageLoader.getImage(item.key.url, 500.0, 200.0, true)

            val tagNodeFactory = rokusho.recordLists.find { it.records.contains(item) }?.let {
                registryToTagNodeFactory.getOrPut(it.metaDataRegistry, { TagNodeFactory(it.metaDataRegistry.getTags()) })::createTagNode
            } ?: defaultTagNodeFactory

            Thumbnail(image, item.itemTags, parser, tagNodeFactory).apply {
                tags.addListener({ _, _, new -> rokusho.updateItemTags(item, new) })
                onMouseClicked = EventHandler {
                    overlay.imageProperty.value = imageLoader.getImage(item.key.url)
                    overlay.isVisible = true
                }
            }
        }
        return StackPane(layout.also { it.thumbnails.bind(SimpleListProperty(thumbnails)) }, overlay)
    }
    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(window: Window) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = lastSelectedDirectory
            showDialog(window)?.let {
                lastSelectedDirectory = it
                rokusho.addDirectory(it.toPath(), Int.MAX_VALUE)
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