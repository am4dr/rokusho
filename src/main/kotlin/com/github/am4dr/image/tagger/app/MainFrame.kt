package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.util.createEmptyListProperty
import javafx.beans.binding.When
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
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

const val defaultSaveFileName = "image_tag_info.tsv"

/*
シーングラフのルート。全体で共有したいデータを保持し、子ノードにプロパティとして提供する。
 */
class MainFrame(private val commandline: CommandLine) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    internal val mainPane = BorderPane()
    private val targetDirProperty: ObjectProperty<Path?> = SimpleObjectProperty()
    private val imageDataStore = ImageDataStore()
    init {
        val imagesProperty: ListProperty<ImageData> = createEmptyListProperty()
        val filer = ImageFiler()
        filer.imagesProperty.bind(imagesProperty)
        mainPane.centerProperty().bind(
                When(imagesProperty.sizeProperty().greaterThan(0))
                        .then(filer.node)
                        .otherwise(makeDirectorySelectorPane()))
        targetDirProperty.addListener { observable, old, new ->
            log.debug("target directory changed: $old -> $new")
            if (new == null) {
                imagesProperty.clear()
                return@addListener
            }
            imagesProperty.setAll(imageDataStore.loadImageData(new, new.resolve(defaultSaveFileName)))
        }
        if (commandline.args.size == 1) {
            Paths.get(commandline.args[0])?.let { path ->
                if (Files.isDirectory(path)) { targetDirProperty.set(path) }
            }
        }
    }
    private fun selectTargetDirectory() {
        DirectoryChooser().run {
            title = "対象ディレクトリの選択"
            initialDirectory = targetDirProperty.get()?.toFile()
            targetDirProperty.set(showDialog(mainPane.scene.window)?.toPath())
        }
    }
    private fun makeDirectorySelectorPane(): Pane {
        val link = Hyperlink("選択ダイアログを開く")
        link.onAction = EventHandler { selectTargetDirectory() }
        return HBox(Label("対象のディレクトリを選択してください: "), link).apply {
            alignment = Pos.CENTER
        }
    }
}