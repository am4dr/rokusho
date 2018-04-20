package com.github.am4dr.rokusho.gui

import javafx.scene.image.Image
import java.lang.ref.SoftReference
import java.net.URL

class UrlImageLoader {
    private val cache: MutableMap<Triple<URL, Double, Double>, SoftReference<Image>> = mutableMapOf()

    fun getImage(url: URL, width: Number = 0, height: Number = 0, async: Boolean = false): Image {
        val key = Triple(url, width.toDouble(), height.toDouble())
        cache[key]?.get()?.let { return it }
        return Image(url.toString(), width.toDouble(), height.toDouble(), true, true, async).also {
            cache[key] = SoftReference(it)
        }
    }
}