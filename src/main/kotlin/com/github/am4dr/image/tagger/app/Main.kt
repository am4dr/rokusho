package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Library
import com.github.am4dr.image.tagger.core.Picture
import javafx.application.Application
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.*
import javafx.collections.FXCollections.observableList
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.Path
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
    private val MainModel = MainModel()

    override fun init() {
        log.info("launched with the params: ${parameters.raw}")
    }
    override fun start(stage: Stage) {
        val mainNode: Parent = MainFrame(parseArgs(parameters.raw.toTypedArray())).mainPane
        stage.run {
            title = "Image Tagger"
            scene = Scene(mainNode, 800.0, 500.0)
            show()
        }
    }
    private fun parseArgs(args: Array<String>): CommandLine = DefaultParser().parse(options, args)
    private fun makeOptions(): Options = with(Options()) {
        addOption(null, "saveto", true, "specify the directory path to save the tag file")
    }
}

// TODO 複数Libraryへの対応
class MainModel {
    private val _libraryProperty: ObjectProperty<Library>
    val libraryProperty: ReadOnlyObjectProperty<Library>
    val pictures: ReadOnlyListProperty<Picture>
    init {
        _libraryProperty = SimpleObjectProperty()
        libraryProperty = SimpleObjectProperty<Library>()
        libraryProperty.bind(_libraryProperty)
        pictures = SimpleListProperty()
        pictures.bind(createObjectBinding(
                Callable { observableList(libraryProperty.get()?.pictures ?: mutableListOf()) },
                libraryProperty))
    }
    fun setLibrary(path: Path) = _libraryProperty.set(Library(path))
}