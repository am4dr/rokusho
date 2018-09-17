package com.github.am4dr.rokusho.gui.thumbnail

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.scene.layout.StackPane

class CachedThumbnailFlowPane<T>(thumbnailFactory: (T) -> ThumbnailFlowPane.Thumbnail) : StackPane() {

    val records: ReadOnlyListProperty<T> = SimpleListProperty(observableArrayList())

    private val thumbnailCache = MemoizedThumbnailFactory(thumbnailFactory)
    private val thumbnails = TransformedList(records, thumbnailCache::invoke)

    init {
        children.add(ThumbnailFlowPane().apply {
            thumbnails.bindContent(this@CachedThumbnailFlowPane.thumbnails)
        })
    }
}