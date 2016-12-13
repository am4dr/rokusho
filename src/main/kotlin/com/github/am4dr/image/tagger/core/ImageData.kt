package com.github.am4dr.image.tagger.core

import com.github.am4dr.image.tagger.core.ImageMetaData
import com.github.am4dr.image.tagger.core.ImageDataStore
import javafx.scene.image.Image
import java.net.URL

private const val thumbnailMaxWidth: Double = 500.0
private const val thumbnailMaxHeight: Double = 200.0

class ImageData(val url: URL, val metaData: ImageMetaData, private val dataStore: ImageDataStore) {
    val thumnail: Image by lazy { dataStore.getImage(url, thumbnailMaxWidth, thumbnailMaxHeight, true) }
    val tempThumbnail: Image
        get() = dataStore.getTemporaryImage(url, thumbnailMaxWidth, thumbnailMaxHeight)
    val image: Image by lazy { dataStore.getImage(url) }
    val tempImage: Image
        get() = dataStore.getTemporaryImage(url)
    override fun toString(): String = "url: $url"
}