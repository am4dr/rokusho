package com.github.am4dr.image.tagger.app

import javafx.beans.binding.When
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
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

const val defaultSaveFileName = "image_tag_info.tsv"
/*
シーングラフのルート。全体で共有したいデータを保持し、子ノードにプロパティとして提供する。
 */
class MainFrame(private val commandline: CommandLine) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val directorySelectorPane = makeDirectorySelectorPane()
    internal val mainPane = BorderPane().apply { center = directorySelectorPane }
    internal val targetDirProperty: ObjectProperty<Path?> = SimpleObjectProperty()
    private var targetDir: Path?
        get() = targetDirProperty.get()
        set(value) = targetDirProperty.set(value)
    private val imageDataStore = ImageDataStore()
    private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
    init {
        val imagesProperty: ListProperty<ImageData> = SimpleListProperty(FXCollections.observableArrayList<ImageData>())
        val filer = ImageFiler()
        filer.imagesProperty.bind(imagesProperty)
        mainPane.centerProperty().bind(
                When(imagesProperty.sizeProperty().greaterThan(0))
                        .then(filer.node)
                        .otherwise(directorySelectorPane))
        targetDirProperty.addListener { observable, old, new ->
            log.debug("target directory changed: $old -> $new")
            if (new == null) {
                imagesProperty.clear()
                return@addListener
            }
            val saveFile = new.resolve(defaultSaveFileName)
            if(Files.exists(saveFile)) {
                log.info("load save file: $saveFile")
                imageDataStore.load(new, saveFile)
            }
            else { log.info("save file not found: $saveFile") }
            Files.list(new)
                    .filter { imageFileNameMatcher.matches(it.fileName.toString()) }
                    .map { imageDataStore.getData(it) }
                    .collect(Collectors.toList<ImageData>())
                    .let { imagesProperty.setAll(it) } // listener of imagesProperty should be called only once
        }
        if (commandline.args.size == 1) {
            Paths.get(commandline.args[0])?.let { path ->
                if (Files.isDirectory(path)) { targetDir = path }
            }
        }
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