package com.github.am4dr.image.tagger.app

import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import org.apache.commons.cli.CommandLine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

/*
シーングラフのルート。全体で共有したいデータを保持し、子ノードにプロパティとして提供する。
    TODO 対象のディレクトリの監視機能をつける
    TODO 対象が画像を含まないときに表示するためのNodeをつくる
    TODO imagedatabaseをファイルから構築する
 */
class MainFrame(private val commandline: CommandLine) {
    val saveFileName = "info.tsv"
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val directorySelectorPane = makeDirectorySelectorPane()
    internal val mainPane = BorderPane().apply { center = directorySelectorPane }
    private var contents: Node
        get() = mainPane.center
        set(value) { mainPane.center = value }
    internal val targetDirProperty: ObjectProperty<Path?> = SimpleObjectProperty()
    private var targetDir: Path?
        get() = targetDirProperty.get()
        set(value) = targetDirProperty.set(value)
    internal val imagesProperty: ListProperty<ImageData> = SimpleListProperty(FXCollections.observableArrayList<ImageData>())
    private val imageDatabase = mutableMapOf<Path, ImageData>()
    private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
    init {
        val filer = ImageFiler(this)
        targetDirProperty.addListener { observable, old, new ->
            log.debug("target directory changed: $old -> $new")
            contents = if (new == null) directorySelectorPane
                       else BorderPane().apply { center = filer.node }
            if (old != new) {
                if (targetDir != null) {
                    Files.list(targetDir)
                            .filter { imageFileNameMatcher.matches(it.fileName.toString()) }
                            .collect(Collectors.toList<Path>())
                            .let { imagesProperty.setAll(it.map { lookupOrCreateImageData(it) }) }
                }
                else { imagesProperty.clear() }
            }
        }
        if (commandline.args.size == 1) {
            Paths.get(commandline.args[0])?.let { path ->
                if (Files.isDirectory(path)) { targetDir = path }
            }
        }
    }
    // TODO クラスImageStoreに抽出
    private fun lookupOrCreateImageData(path: Path): ImageData {
        val data: ImageData? = imageDatabase[path]
        return data ?: ImageData(path).apply { imageDatabase[path] = this }
    }
    fun selectTargetDirectory() {
        DirectoryChooser().run {
            title = "対象ディレクトリの選択"
            targetDir?.let { initialDirectory = it.toFile() }
            targetDir = showDialog(mainPane.scene.window)?.toPath()
        }
    }
    internal fun makeDirectorySelectorPane(): Pane {
        val link = Hyperlink("選択ダイアログを開く")
        link.onAction = EventHandler { selectTargetDirectory() }
        return HBox(Label("対象のディレクトリを選択してください: "), link).apply {
            alignment = Pos.CENTER
        }
    }
}