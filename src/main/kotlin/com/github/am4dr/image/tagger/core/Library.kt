package com.github.am4dr.image.tagger.core

import javafx.collections.FXCollections.observableList
import javafx.collections.ObservableList
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

private const val defaultMetaDataFileName = "image_tag_info.tsv"
private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
private fun isSupportedImageFile(path: Path) =
        Files.exists(path)
                && Files.isRegularFile(path)
                && imageFileNameMatcher.matches(path.fileName.toString())
// TODO add test 特にupdateMetaData
class Library(root: Path) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    val root: Path
    val images: List<Path>
    val metaDataFilePath: Path
    val metaDataStore: MutableMap<Path, ImageMetaData>
    val pictures: ObservableList<Picture>

    init {
        this.root = root.toAbsolutePath()
        images =
                Files.list(root)
                        .filter(::isSupportedImageFile)
                        .collect(Collectors.toList<Path>())
        metaDataFilePath = root.resolve(defaultMetaDataFileName)
        log.info("load imageProperty info from file: $metaDataFilePath")
        metaDataStore =
                if (Files.exists(metaDataFilePath)) {
                    loadImageMataData(metaDataFilePath.toFile()).apply {
                        log.info("loaded imageProperty info number: $size")
                    }
                }
                else {
                    log.info("info file not found: $metaDataFilePath")
                    mutableMapOf()
                }

        val pics = images.map { path ->
            val url = path.toUri().toURL()
            val metaIndex = root.relativize(path)
            Picture(URLImageLoader(url), metaDataStore.getOrElse(metaIndex) { ImageMetaData() })
        }.toMutableList()
        pictures = observableList(pics)
    }
    fun updateMetaData(picture: Picture, newMetaData: ImageMetaData) {
        val i = pictures.indexOf(picture)
        if (i >= 0) {
            pictures[i] = picture.copy(metaData = newMetaData)
            metaDataStore[root.relativize(images[i])] = newMetaData
        }
    }
}