package com.github.am4dr.image.tagger.core

import javafx.scene.image.Image
import java.net.URL

interface ImageLoader {
    val image: Image get () = getImage(0.0, 0.0, false)
    fun getImage(width: Number, height: Number, async: Boolean): Image
}

class URLImageLoader(val url: URL) : ImageLoader {
    private val cache: MutableMap<Triple<URL, Double, Double>, Image> = mutableMapOf()

    override fun getImage(width: Number, height: Number, async: Boolean): Image =
            cache.getOrPut(Triple(url, width.toDouble(), height.toDouble())) {
                Image(url.toString(), width.toDouble(), height.toDouble(), true, true, async)
            }
}