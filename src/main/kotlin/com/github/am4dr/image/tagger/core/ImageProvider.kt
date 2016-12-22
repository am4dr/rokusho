package com.github.am4dr.image.tagger.core

import javafx.scene.image.Image
import java.net.URL

// TODO のちのImageLoaderである
interface ImageProvider {
    val image: Image get () = getImage(0.0, 0.0, false)
    fun getImage(width: Number, height: Number, async: Boolean): Image
}

class URLImageLoader(val url: URL) : ImageProvider {
    override fun getImage(width: Number, height: Number, async: Boolean): Image =
        Image(url.toString(), width.toDouble(), height.toDouble(), true, true, async)
}