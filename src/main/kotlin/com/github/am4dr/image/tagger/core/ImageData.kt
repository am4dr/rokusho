package com.github.am4dr.image.tagger.core

import javafx.scene.image.Image
import java.net.URL

private const val thumbnailMaxWidth: Double = 500.0
private const val thumbnailMaxHeight: Double = 200.0

class ImageData(val url: URL, val metaData: ImageMetaData, private val loader: ImageLoader) {
    val thumnail: Image by lazy { loader.getImage(url, thumbnailMaxWidth, thumbnailMaxHeight, true) }
    val tempThumbnail: Image
        get() = loader.getTemporaryImage(url, thumbnailMaxWidth, thumbnailMaxHeight)
    val image: Image by lazy { loader.getImage(url) }
    val tempImage: Image
        get() = loader.getTemporaryImage(url)
    override fun toString(): String = "url: $url"
}