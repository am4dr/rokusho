package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.*
import com.github.am4dr.rokusho.gui.ThumbnailNode.Companion.thumbnailMaxHeight
import com.github.am4dr.rokusho.gui.ThumbnailNode.Companion.thumbnailMaxWidth
import com.github.am4dr.rokusho.util.createEmptyListProperty
import javafx.application.Application
import javafx.beans.binding.ListBinding
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.collections.FXCollections.observableMap
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.Window
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) = Application.launch(Main::class.java, *args)

class Main : Application() {
    private val model = DefaultMainModel()
    companion object {
        private val log = LoggerFactory.getLogger(Main::class.java)
    }

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
        val commandline = parseArgs(parameters.raw.toTypedArray())
        if (commandline.args.size == 1) {
            Paths.get(commandline.args[0])?.let { path ->
                if (Files.isDirectory(path)) {
                    model.addLibrary(path)
                }
            }
        }
    }
    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(Options(), args)
    override fun start(stage: Stage) {
        stage.run {
            title = "Rokusho"
            scene = Scene(createMainScene(stage), 800.0, 500.0)
            show()
        }
    }
    private fun createMainScene(stage: Stage) : Parent {
        val filterInputNode = TextField()
        val filter = StringImageItemFilter()
        filter.inputProperty.bind(filterInputNode.textProperty())
        val filteredItems = object : ListBinding<ImageItem>() {
            init { super.bind(model.items, filter.filterProperty) }
            override fun computeValue(): ObservableList<ImageItem> =
                    FilteredList(model.items, filter.filterProperty.value)
        }
        val listNode = ListNode(filteredItems)
        val imageLoader = UrlImageLoader()
        val thumbnailFactory: (ImageItem) -> Thumbnail = { item ->
            val tagNodeFactory = model.getTagNodeFactory(item)
            val tagParser = model.getTagParser(item)
            Thumbnail(
                    imageLoader.getImage(item.url, thumbnailMaxWidth, thumbnailMaxHeight, true),
                    item.tags,
                    { tagParser.parse(it) },
                    { tagNodeFactory.createTagNode(it) })
        }
        val thumbnailNode =
                ThumbnailNode(model.items, filter.filterProperty, thumbnailFactory, imageLoader)
        val mainScene =
                MainScene(
                        ImageFiler(filterInputNode, listNode, thumbnailNode),
                        makeDirectorySelectorPane(stage))
        mainScene.librariesNotSelectedProperty.bind(model.libraries.emptyProperty())
        return mainScene
    }
    private fun selectLibraryDirectory(window: Window) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            //initialDirectory = mainModel.libraryProperty.get()?.let { it.fileWalkRoot.toFile() }
            val selected = showDialog(window)?.toPath()
            if (selected != null) { model.addLibrary(selected) }
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
interface MainModel {
    val libraries: ReadOnlyListProperty<ImagePathLibrary>
    val items: ObservableList<ImageItem>
    fun addLibrary(path: Path)
    fun saveLibraries()
    fun getLibrary(item: ImageItem): ImagePathLibrary
    fun getTagNodeFactory(item: ImageItem): TagNodeFactory
    fun getTagParser(item: ImageItem): TagStringParser
}
class DefaultMainModel : MainModel {
    private val _libraries = createEmptyListProperty<ImagePathLibrary>()
    override val libraries: ReadOnlyListProperty<ImagePathLibrary> = _libraries
    private val _items = createEmptyListProperty<ImageItem>()
    override val items: ObservableList<ImageItem> = _items
    private val itemToLibrary = mutableMapOf<ImageItem, ImagePathLibrary>()
    private val libToTagNodeFactory = mutableMapOf<ImagePathLibrary, TagNodeFactory>()
    private val libToTagParser = mutableMapOf<ImagePathLibrary, TagStringParser>()
    override fun addLibrary(path: Path) {
        val lib = ImagePathLibrary(path)
        _libraries.add(lib)
        _items.addAll(lib.images.values)
        itemToLibrary.putAll(lib.images.values.map { Pair(it, lib) })
        libToTagNodeFactory[lib] = TagNodeFactory(ReadOnlyMapWrapper(observableMap(lib.baseTags)))
        libToTagParser[lib] = DefaultTagStringParser(lib.baseTags)
    }
    override fun saveLibraries() {
        TODO() // TODO
    }
    override fun getLibrary(item: ImageItem): ImagePathLibrary {
        return itemToLibrary[item] ?: throw IllegalStateException()
    }
    override fun getTagNodeFactory(item: ImageItem): TagNodeFactory {
        return itemToLibrary[item]?.let { libToTagNodeFactory[it] } ?: throw IllegalStateException()
    }
    override fun getTagParser(item: ImageItem): TagStringParser {
        return itemToLibrary[item]?.let { libToTagParser[it] } ?: throw IllegalStateException()
    }
}
