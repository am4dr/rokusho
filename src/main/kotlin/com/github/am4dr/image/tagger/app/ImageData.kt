package com.github.am4dr.image.tagger.app

import javafx.scene.image.Image
import java.lang.ref.SoftReference
import java.nio.file.Path

private const val thumbnailMaxWidth: Double = 500.0
private const val thumbnailMaxHeight: Double = 200.0

class ImageData(path: Path, val tags: List<String>) {
    constructor(path: Path) : this(path, mutableListOf())
    val path: Path = path.toRealPath()
    val thumnail: Image by lazy { loadImage(thumbnailMaxWidth, thumbnailMaxHeight) }
    private var tempThumbnailRef: SoftReference<Image> = SoftReference<Image>(null)
    val tempThumbnail: Image
        get() = tempThumbnailRef.get() ?: loadImage(thumbnailMaxWidth, thumbnailMaxHeight)
                .apply { tempThumbnailRef = SoftReference<Image>(this) }
    val image: Image by lazy { loadImage(0.0, 0.0) }
    private var tempImageRef: SoftReference<Image> = SoftReference<Image>(null)
    val tempImage: Image
        get() = tempImageRef.get() ?: loadImage(0.0, 0.0, false)
                .apply { tempImageRef = SoftReference<Image>(this) }
    private fun loadImage(width: Double, height: Double, background: Boolean = true): Image {
        return Image(path.toUri().toURL().toString(), width, height, true, true, background)
    }
    override fun toString(): String = "path: $path, tags: $tags"
}