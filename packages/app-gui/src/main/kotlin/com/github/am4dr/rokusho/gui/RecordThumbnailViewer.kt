package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.Tag
import com.github.am4dr.rokusho.gui.tag.TagNode
import com.github.am4dr.rokusho.gui.tag.TagView
import com.github.am4dr.rokusho.gui.thumbnail.OverlayThumbnailDecorator
import com.github.am4dr.rokusho.gui.thumbnail.ThumbnailFlowPane
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.scene.layout.StackPane
import java.lang.ref.SoftReference
import java.util.*

class RecordThumbnailViewer<T>(thumbnailFactory: (Record<T>, Double, Double) -> ThumbnailFlowPane.Thumbnail,
                               val thumbnailMaxWidth: Double = 500.0, val thumbnailMaxHeight: Double = 200.0) : StackPane() {

    val records: ReadOnlyListProperty<Record<T>> = SimpleListProperty(observableArrayList())
    val updateTagsProperty: ObjectProperty<(Record<T>, List<ItemTag>) -> Unit> = SimpleObjectProperty { _, _ -> }
    val onActionProperty: ObjectProperty<(List<Record<T>>) -> Unit> = SimpleObjectProperty { _ -> }
    val parserProperty: ObjectProperty<(String) -> ItemTag> = SimpleObjectProperty { text: String -> ItemTag(Tag(text, Tag.Type.TEXT, mapOf("value" to text)), null) }
    val tagNodeFactoryProperty: ObjectProperty<(ItemTag) -> TagView> = SimpleObjectProperty { tag: ItemTag -> TagNode(tag).view }

    init {
        val thumbnailCache = WeakHashMap(mutableMapOf<Record<*>, SoftReference<ThumbnailFlowPane.Thumbnail>>())

        fun createThumbnail(record: Record<T>): ThumbnailFlowPane.Thumbnail {
            val thumbnail = thumbnailFactory(record, thumbnailMaxWidth, thumbnailMaxHeight)
            return OverlayThumbnailDecorator(thumbnail, parserProperty.get(), tagNodeFactoryProperty.get()).apply {
                tags.setAll(record.itemTags)
                onEditEndedProperty.set { new -> updateTagsProperty.value?.invoke(record, new) }
                setOnMouseClicked {
                    onActionProperty.get()?.invoke(listOf(record))
                }
            }
        }
        fun createAndCacheThumbnail(record: Record<T>): ThumbnailFlowPane.Thumbnail =
                createThumbnail(record).apply {
                    thumbnailCache[record] = SoftReference(this)
                }
        fun getThumbnail(record: Record<T>): ThumbnailFlowPane.Thumbnail = thumbnailCache[record]?.get() ?: createAndCacheThumbnail(record)

        children.add(ThumbnailFlowPane().apply {
            thumbnails.value = TransformedList(records, ::getThumbnail)
        })
    }
}