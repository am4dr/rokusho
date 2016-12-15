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
}