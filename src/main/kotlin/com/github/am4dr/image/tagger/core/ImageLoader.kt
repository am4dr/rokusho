package com.github.am4dr.image.tagger.core

import javafx.scene.image.Image
import java.net.URL

class ImageLoader {
    private val cache: MutableMap<Triple<URL, Double, Double>, Image> = mutableMapOf()

    fun getImage(url: URL, requestWidth: Double, requestHeight: Double,
                 backgroundLoading: Boolean = false): Image =
            cache.getOrPut(Triple(url, requestWidth, requestHeight)) {
                Image(url.toString(), requestWidth, requestHeight, true, true, backgroundLoading)
            }
    fun getImage(url: URL, backgroundLoading: Boolean = false): Image =
            getImage(url, 0.0, 0.0, backgroundLoading)

    fun getTemporaryImage(url: URL, requestWidth: Double, requestHeight: Double,
                          backgroundLoading: Boolean = false): Image =
            cache.getOrElse(Triple(url, requestWidth, requestHeight)) {
                Image(url.toString(), requestWidth, requestHeight, true, true, backgroundLoading)
            }
    fun getTemporaryImage(url: URL, backgroundLoading: Boolean = false): Image =
            getTemporaryImage(url, 0.0, 0.0, backgroundLoading)
    fun getImageData(url: URL, metaData: ImageMetaData): ImageData =
            URLImageData(url, metaData, this)
}
private class URLImageData(private val url: URL,
                           override val metaData: ImageMetaData,
                           private val loader: ImageLoader) : BaseImageData() {
    override val thumbnail: Image
            by lazy { loader.getImage(url, thumbnailMaxWidth, thumbnailMaxHeight, true) }
    override val tempThumbnail: Image
        get() = loader.getTemporaryImage(url, thumbnailMaxWidth, thumbnailMaxHeight)
    override val image: Image by lazy { loader.getImage(url) }
    override val tempImage: Image get() = loader.getTemporaryImage(url)
}