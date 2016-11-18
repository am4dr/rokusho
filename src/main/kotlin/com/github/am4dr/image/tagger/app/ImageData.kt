package com.github.am4dr.image.tagger.app

import javafx.scene.image.Image
import java.nio.file.Path

class ImageData(path: Path, val tags: MutableList<String>) {
    constructor(path: Path) : this(path, mutableListOf())
    val path: Path = path.toRealPath()
    val thumnail: Image by lazy { Image(path.toUri().toURL().toString(), 200.0, 200.0, true, true, true) }
    val image: Image by lazy { Image(path.toUri().toURL().toString()) }
}