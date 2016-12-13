package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.ImageData
import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.ImageDataStore
import com.github.am4dr.image.tagger.core.loadImageMataData
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
import java.util.stream.Collectors

const val defaultSaveFileName = "image_tag_info.tsv"
private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)

/*
シーングラフのルート。全体で共有したいデータを保持し、子ノードにプロパティとして提供する。
 */
class MainFrame(private val commandline: CommandLine) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    internal val mainPane = BorderPane()
    private val targetDirProperty: ObjectProperty<Path?> = SimpleObjectProperty()
    private val imageMetaData = mutableMapOf<Path, ImageMetaData>()
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
            imagesProperty.clear()
            imageMetaData.clear()
            new ?: return@addListener

            val metaDataFile = new.resolve(defaultSaveFileName).toFile()
            if (metaDataFile.exists()) {
                log.info("load image info from file: $metaDataFile")
                imageMetaData.putAll(loadImageMataData(metaDataFile).map { Pair(it.path.normalize(), it) })
                log.info("loaded image info number: ${imageMetaData.size}")
            }
            else {
                log.info("info file not found: $metaDataFile")
            }
            log.debug("metadata: ${imageMetaData}")
            fun Path.toImageData(): ImageData =
                    ImageData(new.resolve(this).toUri().toURL(),
                            imageMetaData.getOrPut(this.normalize()) { ImageMetaData(this) },
                            imageDataStore)
            val dataList =
                    Files.list(new)
                            .filter { Files.isRegularFile(it) }
                            .filter { imageFileNameMatcher.matches(it.fileName.toString()) }
                            .map { new.relativize(it).toImageData() }
                            .collect(Collectors.toList<ImageData>())
            imagesProperty.setAll(dataList)
        }
        if (commandline.args.size == 1) {
            Paths.get(commandline.args[0])?.let { path ->
                if (Files.isDirectory(path)) {
                    targetDirProperty.set(path)
                }
            }
        }
    }

    private fun selectTargetDirectory() {
        DirectoryChooser().run {
            title = "画像があるディレクトリを選択してください"
            initialDirectory = targetDirProperty.get()?.toFile()
            targetDirProperty.set(showDialog(mainPane.scene.window)?.toPath())
        }
    }

    private fun makeDirectorySelectorPane(): Pane {
        val link = Hyperlink("選択ウィンドウを開く")
        link.onAction = EventHandler { selectTargetDirectory() }
        return HBox(Label("対象とする画像があるディレクトリを選択してください: "), link).apply {
            alignment = Pos.CENTER
        }
    }
}