package com.github.am4dr.image.tagger.core

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

private const val defaultMetaDataFileName = "image_tag_info.tsv"
private val imageFileNameMatcher = Regex(".*\\.(bmp|gif|jpe?g|png)$", RegexOption.IGNORE_CASE)
private fun isSupportedImageFile(path: Path) =
        Files.exists(path)
                && Files.isRegularFile(path)
                && imageFileNameMatcher.matches(path.fileName.toString())
// TODO add test
class Library(root: Path) {
    val root: Path
    val images: List<Path>
    val metaDataFilePath: Path
    val metaDataStore: MutableMap<Path, ImageMetaData>
    val pictures: List<Picture>

    init {
        this.root = root.toAbsolutePath()
        images =
                Files.list(root)
                        .filter(::isSupportedImageFile)
                        .collect(Collectors.toList<Path>())
        metaDataFilePath = root.resolve(defaultMetaDataFileName)
        metaDataStore =
                if (Files.exists(metaDataFilePath)) loadImageMataData(metaDataFilePath.toFile())
                else mutableMapOf()
        pictures = mutableListOf()
        images.mapTo(pictures) { path ->
            val url = path.toUri().toURL()
            val metaIndex = root.relativize(path)
            Picture(URLImageLoader(url), metaDataStore.getOrPut(metaIndex) { ImageMetaData() })
        }
    }
}