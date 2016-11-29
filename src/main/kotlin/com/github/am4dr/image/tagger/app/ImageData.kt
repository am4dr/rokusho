package com.github.am4dr.image.tagger.app

import javafx.scene.image.Image
import java.lang.ref.SoftReference
import java.nio.file.Path

class ImageData(path: Path, val tags: List<String>) {
    constructor(path: Path) : this(path, mutableListOf())
    val path: Path = path.toRealPath()
    val thumnail: Image by lazy { loadImage(200.0, 200.0) }
    val image: Image by lazy { loadImage(0.0, 0.0) }
    private var tempImageRef: SoftReference<Image> = SoftReference<Image>(null)
    val tempImage: Image
        get() = tempImageRef.get() ?: loadImage(0.0, 0.0, false).apply { tempImageRef = SoftReference<Image>(this) }
    private fun loadImage(width: Double, height: Double, background: Boolean = true): Image {
        return Image(path.toUri().toURL().toString(), width, height, true, true, background)
    }
}