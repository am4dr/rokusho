package com.github.am4dr.rokusho.gui

import javafx.scene.image.Image
import java.net.URL

class UrlImageLoader {
    private val cache: MutableMap<Triple<URL, Double, Double>, Image> = mutableMapOf()
    fun getImage(url: URL, width: Number = 0, height: Number = 0, async: Boolean = false): Image =
            cache.getOrPut(Triple(url, width.toDouble(), height.toDouble())) {
                Image(url.toString(), width.toDouble(), height.toDouble(), true, true, async)
            }
}