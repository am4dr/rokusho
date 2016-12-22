package com.github.am4dr.image.tagger.app

import javafx.beans.binding.When
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.layout.BorderPane

//private const val defaultMetaDataFileName = "image_tag_info.tsv"
//private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
//
//class MainFrame(private val commandline: CommandLine) {
//    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
//    internal val mainPane = BorderPane()
//    private val targetDirProperty: ObjectProperty<Path?> = SimpleObjectProperty()
//    private val imageMetaDataStore = mutableMapOf<Path, ImageMetaData>()
//    private val imageLoader = ImageLoader()
//    init {
//        val imagesProperty: ListProperty<ImageData> = createEmptyListProperty()
//        val filer = ImageFiler()
//        filer.imagesProperty.bind(imagesProperty)
//        mainPane.centerProperty().bind(
//                When(imagesProperty.sizeProperty().greaterThan(0))
//                        .then(filer.node)
//                        .otherwise(makeDirectorySelectorPane()))
//        targetDirProperty.addListener { observable, old, new ->
//            log.debug("target directory changed: $old -> $new")
//            imagesProperty.clear()
//            imageMetaDataStore.clear()
//            new ?: return@addListener
//            loadMetaData(new)
//            imagesProperty.setAll(loadImageData(new))
//        }
//        if (commandline.args.size == 1) {
//            Paths.get(commandline.args[0])?.let { path ->
//                if (Files.isDirectory(path)) {
//                    targetDirProperty.set(path)
//                }
//            }
//        }
//    }
//    private fun loadMetaData(metaDataFilePath: Path) {
//        val metaDataFile = metaDataFilePath.resolve(defaultMetaDataFileName).toFile()
//        if (metaDataFile.exists()) {
//            log.info("load imageProperty info from file: $metaDataFile")
//            imageMetaDataStore.putAll(loadImageMataData(metaDataFile))
//            log.info("loaded imageProperty info number: ${imageMetaDataStore.size}")
//        }
//        else {
//            log.info("info file not found: $metaDataFile")
//        }
//    }
//    private fun saveMetaData(metaDataFilePath: Path) {
//        val metaDataFile = metaDataFilePath.resolve(defaultMetaDataFileName).toFile()
//        log.info("save imageProperty mata data to: $metaDataFile")
//        if (metaDataFile.exists()) {
//            log.info("$metaDataFile already exists, overwrite with new data")
//        }
//        saveImageMetaData(imageMetaDataStore, metaDataFile)
//        log.info("saved ${imageMetaDataStore.size} imageProperty mata data to: $metaDataFile")
//    }
//    private fun loadImageData(targetDirPath: Path): List<ImageData> {
//        fun Path.toImageData(): ImageData =
//                imageLoader.getImageData(targetDirPath.resolve(this).toUri().toURL(),
//                        imageMetaDataStore.getOrPut(this.normalize()) { ImageMetaData() })
//        return Files.list(targetDirPath)
//                .filter { Files.isRegularFile(it) && imageFileNameMatcher.matches(it.fileName.toString()) }
//                .map { targetDirPath.relativize(it).toImageData() }
//                .collect(Collectors.toList<ImageData>())
//    }
//
//    private fun selectTargetDirectory() {
//        DirectoryChooser().run {
//            title = "画像があるディレクトリを選択してください"
//            initialDirectory = targetDirProperty.get()?.toFile()
//            targetDirProperty.set(showDialog(mainPane.scene.window)?.toPath())
//        }
//    }
//
//    private fun makeDirectorySelectorPane(): Pane {
//        val link = Hyperlink("選択ウィンドウを開く")
//        link.onAction = EventHandler { selectTargetDirectory() }
//        return HBox(Label("対象とする画像があるディレクトリを選択してください: "), link).apply {
//            alignment = Pos.CENTER
//        }
//    }
//}
class MainFrame(val filer: ImageFiler, val directorySelectorPane: Node) : BorderPane() {
    val librariesNotSelectedProperty: BooleanProperty
    init {
        librariesNotSelectedProperty = SimpleBooleanProperty(true)
        centerProperty().bind(
                When(librariesNotSelectedProperty)
                        .then<Node>(directorySelectorPane)
                        .otherwise(filer))
    }
}