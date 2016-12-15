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
}
abstract class BaseImageData : ImageData {
    override fun toString(): String = "$metaData"
}