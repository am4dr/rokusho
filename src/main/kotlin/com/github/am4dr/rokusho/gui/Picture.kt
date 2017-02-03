package com.github.am4dr.image.tagger.core

import com.github.am4dr.rokusho.app.ImageItem

data class Picture(val loader: ImageLoader, val metaData: ImageMetaData, val item: ImageItem) {
    constructor(item: ImageItem)
            : this(URLImageLoader(item.url), item.tags.let(::ImageMetaData), item)
}