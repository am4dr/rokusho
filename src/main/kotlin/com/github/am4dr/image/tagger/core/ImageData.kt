package com.github.am4dr.image.tagger.core

import javafx.scene.image.Image

const val thumbnailMaxWidth: Double = 500.0
const val thumbnailMaxHeight: Double = 200.0

interface ImageData {
    val metaData: ImageMetaData
    val thumbnail: Image
    val tempThumbnail: Image
    val image: Image
    val tempImage: Image
    fun copy(metaData: ImageMetaData): ImageData =
            object : BaseImageData() {
                override val image: Image get() = this@ImageData.image
                override val tempImage: Image get() = this@ImageData.tempImage
                override val thumbnail: Image get() = this@ImageData.thumbnail
                override val tempThumbnail: Image get() = this@ImageData.tempThumbnail
                override val metaData: ImageMetaData get() = metaData
            }
}
abstract class BaseImageData : ImageData {
    override fun toString(): String = "$metaData"
}