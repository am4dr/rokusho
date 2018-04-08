package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.app.Rokusho
import com.github.am4dr.rokusho.core.library.RokushoLibrary
import com.github.am4dr.rokusho.app.library.lfs.LocalFileSystemLibrary
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.Tag
import com.github.am4dr.rokusho.gui.sidemenu.SideMenuIcon
import com.github.am4dr.rokusho.gui.sidemenu.SimpleSideMenu
import com.github.am4dr.rokusho.gui.tag.TagNode
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.ThumbnailFlowPane
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import com.github.am4dr.rokusho.javafx.function.bindLeft
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.geometry.Insets
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
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate

class RokushoGui(val rokusho: Rokusho, val stage: Stage, val getLibrary: (Path) -> LocalFileSystemLibrary) {

    private val libs: ObservableList<Triple<RokushoLibrary<ImageUrl>, SideMenuIcon, FilerLayout>> = TransformedList(rokusho.libraries) { Triple(it, it.toSideMenuIcon(), createImageFiler(it)) }
    private val currentLibrary: ObjectProperty<RokushoLibrary<ImageUrl>> = SimpleObjectProperty()
    val mainParent: Parent  = createMainScene()

    private fun createMainScene(): MainLayout {
        val saveButton = Button("保存").apply {
            setOnAction {
                currentLibrary.get()?.let { lib ->
                    (lib as? LocalFileSystemLibrary)?.save()
                }
            }
        }
        val addLibraryButton = Button("追加").apply {
            setOnAction { selectLibraryDirectory() }
        }
        val sideMenu = SimpleSideMenu({ selectLibraryDirectory() }).apply {
            width.set(40.0)
        }
        val filerPane = BorderPane()
        libs.addListener(InvalidationListener {
            val icons = libs.map { (lib, icon, filer) ->
                icon.apply {
                    setOnMouseClicked {
                        filerPane.center = filer
                        currentLibrary.set(lib)
                    }
                }
            }
            sideMenu.setIcons(icons)
        })

        return MainLayout(saveButton, addLibraryButton, filerPane, makeDirectorySelectorPane({ selectLibraryDirectory() }), sideMenu.node).apply {
            librariesNotSelectedProperty.bind(currentLibrary.isNull)
        }
    }

    private var lastSelectedDirectory: File? = null
    private fun selectLibraryDirectory(window: Window = stage) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = lastSelectedDirectory
            showDialog(window)?.let {
                lastSelectedDirectory = it
                rokusho.addLibrary(getLibrary(it.toPath()))
            }
        }
    }
}

private fun RokushoLibrary<ImageUrl>.toSideMenuIcon(): SideMenuIcon =
        SideMenuIcon().apply {
            background = Background(BackgroundFill(Color.INDIANRED, CornerRadii(4.0), Insets.EMPTY))
            children.add(Label("Lib: ${System.identityHashCode(this@toSideMenuIcon)}"))
        }

private fun createImageFiler(library: RokushoLibrary<ImageUrl>): FilerLayout =
        createImageFiler(library.records, { record, tags -> library.updateItemTags(record.key, tags) })

private fun createImageFiler(records: ObservableList<Record<ImageUrl>>, updateTags: (Record<ImageUrl>, List<ItemTag>) -> Unit): FilerLayout {
    val filterInput = TextField()
    val recordFilter = { input: String? ->
        Predicate { item: Record<ImageUrl> ->
            if (input == null || input == "") true
            else item.itemTags.any { it.tag.id.contains(input) }
        }
    }.bindLeft(filterInput.textProperty())
    val filteredItems = FilteredList(records).apply { predicateProperty().bind(recordFilter) }
    val listNode = ListView(filteredItems)
    val thumbnailNode = createThumbnailNode(filteredItems, updateTags)
    return FilerLayout(filterInput, listNode, thumbnailNode, Bindings.size(records), Bindings.size(filteredItems))
}

private fun createThumbnailNode(records: ObservableList<Record<ImageUrl>>, updateTags: (Record<ImageUrl>, List<ItemTag>) -> Unit): Node {
    val overlay = ImageOverlay().apply {
        isVisible = false
        onMouseClicked = EventHandler { isVisible = false }
        background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
    }

    val imageLoader = UrlImageLoader()
    // TODO Libraryの内容を反映するようなparserを実装する
    val parser = { text: String -> ItemTag(Tag(text, Tag.Type.TEXT, mapOf("value" to text)), null) }
    val tagNodeFactory = { tag: ItemTag -> TagNode(tag).view }

    val thumbnailCache = WeakHashMap(mutableMapOf<Record<ImageUrl>, SoftReference<ThumbnailFlowPane.Thumbnail>>())

    fun createAndCacheThumbnail(record: Record<ImageUrl>): ThumbnailFlowPane.Thumbnail =
            ImageThumbnail(imageLoader.getImage(record.key.url, 500.0, 200.0, true), parser, tagNodeFactory).apply {
                setTags(record.itemTags)
                tags.addListener({ _, _, new -> updateTags(record, new) })
                view.onMouseClicked = EventHandler {
                    overlay.imageProperty.value = imageLoader.getImage(record.key.url)
                    overlay.isVisible = true
                }
                thumbnailCache[record] = SoftReference(this as ThumbnailFlowPane.Thumbnail)
            }

    fun getThumbnail(record: Record<ImageUrl>): ThumbnailFlowPane.Thumbnail = thumbnailCache[record]?.get()
            ?: createAndCacheThumbnail(record)

    val pane = ThumbnailFlowPane().apply {
        thumbnails.value = TransformedList(records, ::getThumbnail)
    }
    return StackPane(pane, overlay)
}

private fun makeDirectorySelectorPane(openSelectWindow: () -> Unit): Pane {
    val link = Hyperlink("選択ウィンドウを開く")
    link.onAction = EventHandler { openSelectWindow.invoke() }
    return HBox(Label("対象とする画像があるディレクトリを選択してください: "), link).apply {
        alignment = Pos.CENTER
    }
}
