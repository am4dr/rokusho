package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.*
import com.github.am4dr.rokusho.core.ImageMetaData
import com.github.am4dr.rokusho.core.SaveFile
import com.github.am4dr.rokusho.gui.ThumbnailNode.Companion.thumbnailMaxHeight
import com.github.am4dr.rokusho.gui.ThumbnailNode.Companion.thumbnailMaxWidth
import javafx.application.Application
import javafx.beans.binding.ListBinding
import javafx.beans.property.ReadOnlyListProperty
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
            Thumbnail(
                    imageLoader.getImage(item.url, thumbnailMaxWidth, thumbnailMaxHeight, true),
                    item.tags,
                    model.getTagParser(item)::parse,
                    model.getTagNodeFactory(item)::createTagNode)
        }
        val thumbnailNode =
                ThumbnailNode(model.items, filter.filterProperty, thumbnailFactory, imageLoader)
        val mainScene =
                MainScene(
                        ImageFiler({ selectLibraryDirectory(stage) },
                                { model.saveLibraries() },
                                filterInputNode, listNode, thumbnailNode),
                        makeDirectorySelectorPane(stage))
        mainScene.librariesNotSelectedProperty.bind(model.libraries.emptyProperty())
        return mainScene
    }
    private fun selectLibraryDirectory(window: Window) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = model.libraries.lastOrNull()?.let {
                it.savefilePath.parent.toFile()
            }
            showDialog(window)?.toPath()?.let(model::addLibrary)
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
    val libraries: ReadOnlyListProperty<ImageLibrary>
    val items: ObservableList<ImageItem>
    fun addLibrary(path: Path)
    fun saveLibraries()
    fun getLibrary(item: ImageItem): ImageLibrary
    fun getTagNodeFactory(item: ImageItem): TagNodeFactory
    fun getTagParser(item: ImageItem): TagStringParser
}
class DefaultMainModel : MainModel {
    private val _libraries = ImageLibraryCollection()
    override val libraries: ReadOnlyListProperty<ImageLibrary> = _libraries.librariesProperty
    override val items: ObservableList<ImageItem> = _libraries.itemsProperty
    override fun addLibrary(path: Path) = _libraries.addDirectory(path)
    override fun saveLibraries() {
        items.groupBy(ImageItem::library).forEach { lib, items ->
            val metaDataList = items.map { Pair(Paths.get(it.id), ImageMetaData(it.tags)) }.toMap()
            val savefile = SaveFile("1", lib.baseTagsProperty.get(), metaDataList)
            lib.save(savefile.toTextFormat())
        }
    }
    override fun getLibrary(item: ImageItem): ImageLibrary  = item.library
    override fun getTagNodeFactory(item: ImageItem): TagNodeFactory = item.library.tagNodeFactory
    override fun getTagParser(item: ImageItem): TagStringParser = item.library.tagStringParser
}
