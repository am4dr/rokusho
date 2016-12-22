package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Library
import com.github.am4dr.image.tagger.core.Picture
import com.github.am4dr.image.tagger.node.ImageTile
import com.github.am4dr.image.tagger.node.ImageTileScrollPane
import javafx.application.Application
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.*
import javafx.collections.FXCollections.observableList
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
import java.util.concurrent.Callable

fun main(args: Array<String>) = Application.launch(Main::class.java, *args)

/*
    アプリケーションを構築するためのクラス
    - JavaFXコンテキストの生成
    - メインとなるフレームの作成
    - 構成要素の生成と相互接続
 */
class Main : Application() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val options = makeOptions()
    private val mainModel = MainModel()

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
    private fun createMainFrame(stage: Stage): MainFrame =
        MainFrame(
                ImageFiler(
                        mainModel.picturesProperty,
                        ListView(),
                        ThumbnailPane(ImageTileScrollPane(::ImageTile))),
                makeDirectorySelectorPane(stage)).apply {
            librariesNotSelectedProperty.bind(mainModel.picturesProperty.emptyProperty())
        }
    private fun selectLibraryDirectory(window: Window) {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = mainModel.libraryProperty.get()?.let { it.root.toFile() }
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

// TODO 複数Libraryへの対応
class MainModel {
    private val _libraryProperty: ObjectProperty<Library>
    val libraryProperty: ReadOnlyObjectProperty<Library>
    val picturesProperty: ReadOnlyListProperty<Picture>
    init {
        _libraryProperty = SimpleObjectProperty()
        libraryProperty = SimpleObjectProperty<Library>()
        libraryProperty.bind(_libraryProperty)
        picturesProperty = SimpleListProperty()
        picturesProperty.bind(createObjectBinding(
                Callable { observableList(libraryProperty.get()?.pictures ?: mutableListOf()) },
                libraryProperty))
    }
    fun setLibrary(path: Path) = _libraryProperty.set(Library(path))
}