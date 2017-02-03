package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
import com.github.am4dr.image.tagger.node.TagNode
import com.github.am4dr.image.tagger.node.ThumbnailPane
import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.gui.AdaptedDefaultMainModel
import javafx.application.Application
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ListView
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
import com.github.am4dr.rokusho.gui.DefaultMainModel as NewDefaultMainModel

fun main(args: Array<String>) = Application.launch(Main::class.java, *args)

/*
    アプリケーションを構築するためのクラス
    - JavaFXコンテキストの生成
    - メインとなるフレームの作成
    - 構成要素の生成と相互接続
 */
class Main : Application() {
    private val options = makeOptions()
    private val mainModel: MainModel = AdaptedDefaultMainModel()
    companion object {
        private val log = LoggerFactory.getLogger(Main::class.java)
    }

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
        val commandline = parseArgs(parameters.raw.toTypedArray())
        if (commandline.args.size == 1) {
            Paths.get(commandline.args[0])?.let { path ->
                if (Files.isDirectory(path)) {
                    mainModel.setLibrary(path)
                }
            }
        }
    }
    override fun start(stage: Stage) {
        stage.apply {
            title = "Image Tagger"
            scene = Scene(createMainFrame(stage), 800.0, 500.0)
        }.show()
    }
    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(options, args)
    private fun makeOptions(): Options = with(Options()) {
        addOption(null, "saveto", true, "specify the directory path to save the tag file")
    }
    private fun createMainFrame(stage: Stage): MainFrame {
        val tagNodeFactory: (Tag) -> TagNode = { mainModel.tagNodeFactory.createTagNode(it) }
        val tileFactory: (Picture) -> ImageTile = { pic ->
            ImageTile(pic, tagNodeFactory).apply {
                metaDataProperty.addListener { it -> mainModel.updateMetaData(pic, metaDataProperty.get()) }
            }
        }
        return MainFrame(
                ImageFiler(
                        mainModel.picturesProperty,
                        StringPictureFilter(),
                        ListView(),
                        ThumbnailPane(ImageTileScrollPane(tileFactory))),
                makeDirectorySelectorPane(stage))
                .apply {
                    librariesNotSelectedProperty.bind(mainModel.picturesProperty.emptyProperty())
                }
    }
    private fun selectLibraryDirectory(window: Window) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            //initialDirectory = mainModel.libraryProperty.get()?.let { it.fileWalkRoot.toFile() }
            val selected = showDialog(window)?.toPath()
            if (selected != null) { mainModel.setLibrary(selected) }
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
//    val libraryProperty: ReadOnlyObjectProperty<Library>
    val picturesProperty: ReadOnlyListProperty<Picture>
    val tagsProperty: ReadOnlyMapProperty<String, Tag>
    val tagNodeFactory: TagNodeFactory
    fun setLibrary(path: Path)
    fun updateMetaData(picture: Picture, metaData: ImageMetaData)
    fun updateTagInfo(name: String, tag: Tag)
    fun save()
}
/*
// TODO 複数Libraryへの対応
class DefaultMainModel : MainModel {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultMainModel::class.java)
    }
    private val _libraryProperty: ObjectProperty<Library>
    val libraryProperty: ReadOnlyObjectProperty<Library>
    override val picturesProperty: ReadOnlyListProperty<Picture>
    override val tagsProperty: ReadOnlyMapProperty<String, Tag>
    override val tagNodeFactory: TagNodeFactory
    init {
        _libraryProperty = SimpleObjectProperty()
        libraryProperty = SimpleObjectProperty<Library>()
        libraryProperty.bind(_libraryProperty)
        picturesProperty = SimpleListProperty()
        picturesProperty.bind(createObjectBinding(
                Callable { libraryProperty.get()?.pictures ?: observableList(mutableListOf()) },
                libraryProperty))
        tagsProperty = SimpleMapProperty()
        tagsProperty.bind(createObjectBinding(
                Callable { observableMap(libraryProperty.get()?.tags ?: mutableMapOf()) },
                libraryProperty))
        tagNodeFactory = TagNodeFactory(tagsProperty)
    }
    override fun setLibrary(path: Path) {
        log.info("select library: $path")
        _libraryProperty.set(Library(path))
    }
    override fun updateMetaData(picture: Picture, metaData: ImageMetaData) {
        log.info("update metadata: $picture, $metaData")
        libraryProperty.get().updateMetaData(picture, metaData)
    }
    override fun updateTagInfo(name: String, tag: Tag) {
        log.info("update tag: id=$name, tag=$tag")
        libraryProperty.get().updateTagInfo(name, tag)
    }
    override fun save() {
        val metaDataFile = libraryProperty.get().metaDataFilePath.toFile()
        log.info("save imageProperty mata data to: $metaDataFile")
        if (metaDataFile.exists()) {
            log.info("$metaDataFile already exists, overwrite with new data")
        }
        metaDataFile.writeText(libraryProperty.get().toSaveFormat())
        log.info("saved ${libraryProperty.get().metaDataStore.size} imageProperty mata data to: $metaDataFile")
    }
}*/