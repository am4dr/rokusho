package com.github.am4dr.image.tagger.app

import javafx.scene.image.Image
import java.nio.file.Path

data class ImageData(val path: Path, val tags: MutableList<String>) {
    constructor(path: Path) : this(path, mutableListOf())
    val imageData: Image by lazy { TODO() }
}