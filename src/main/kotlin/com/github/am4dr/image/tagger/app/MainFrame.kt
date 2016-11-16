package com.github.am4dr.image.tagger.app

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.apache.commons.cli.CommandLine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

/*
    プロパティのゲッターはread onlyなプロパティを返すようにする。
 */
class MainFrame(private val stage: Stage,
                private val commandline: CommandLine) {
    val saveFileName = "info.tsv"
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    internal val emptyTargetPane = makeEmptyTargetPane()
    private val mainPane = BorderPane().apply { center = emptyTargetPane }
    private var contents: Node?
        get() = mainPane.center
        set(value) { mainPane.center = value }
    internal val targetDirProperty = SimpleObjectProperty<Path?>()
    internal val imagesProperty = FXCollections.observableArrayList<ImageData>()
    internal var targetDir: Path?
        get() = targetDirProperty.get()
        set(value) {
            targetDirProperty.value = value?.normalize()?.toAbsolutePath()
            log.info("set target directory: $targetDir")
            targetDir?.let { dir: Path ->
                Files.list(dir).collect(Collectors.toList<Path>())
                               .let { imagesProperty.setAll(it.map(::ImageData)) }
            }
        }
    init {
        stage.title = "Image Tagger"
        stage.scene = Scene(mainPane, 400.0, 300.0)
        val filer = ImageFiler(this)
        targetDirProperty.addListener { observable, old, new ->
            log.debug("target directory changed: $old -> $new")
            contents = if (new == null) emptyTargetPane
                       else BorderPane().apply { center = filer.pane }
        }
        setDirectories()
    }
    fun show() = stage.show()
    fun selectTargetDirectory() {
        DirectoryChooser().run {
            title = "対象ディレクトリの選択"
            targetDir?.let { initialDirectory = it.toFile() }
            targetDir = showDialog(stage).toPath()
        }
    }
    private fun setDirectories() {
        when (commandline.args.size) {
            0 -> {
                System.err.println("対象のディレクトリを引数に指定せよ")
            }
            1 -> {
                val path = Paths.get(commandline.args[0])
                if (Files.isDirectory(path)) {
                    targetDir = path
                }
            }
            else -> {
                System.err.println("対象のディレクトリはひとつまでにせよ")
            }
        }
    }
    private fun makeEmptyTargetPane(): Pane {
        val link = Hyperlink("選択ダイアログを開く")
        link.onAction = EventHandler { selectTargetDirectory() }
        return HBox(Label("対象のディレクトリを選択してください: "), link).apply {
            alignment = Pos.CENTER
        }
    }
}