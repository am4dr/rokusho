package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageUrl
import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
import com.github.am4dr.rokusho.core.library.Tag
import com.github.am4dr.rokusho.gui.tag.TagNode
import com.github.am4dr.rokusho.gui.tag.TagView
import com.github.am4dr.rokusho.gui.thumbnail.ImageThumbnail
import com.github.am4dr.rokusho.gui.thumbnail.ThumbnailFlowPane
import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.event.EventHandler
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import java.lang.ref.SoftReference
import java.util.*

// TODO remove dependency on ImageUrl
class RecordThumbnailViewer : StackPane() {

    val records: ReadOnlyListProperty<Record<ImageUrl>> = SimpleListProperty(observableArrayList())
    val updateTagsProperty: ObjectProperty<(Record<ImageUrl>, List<ItemTag>) -> Unit> = SimpleObjectProperty()
    val parserProperty: ObjectProperty<(String) -> ItemTag> = SimpleObjectProperty { text: String -> ItemTag(Tag(text, Tag.Type.TEXT, mapOf("value" to text)), null) }
    val tagNodeFactoryProperty: ObjectProperty<(ItemTag) -> TagView> = SimpleObjectProperty { tag: ItemTag -> TagNode(tag).view }

    init {
        val overlay = ImageOverlay().apply {
            isVisible = false
            onMouseClicked = EventHandler { isVisible = false }
            background = Background(BackgroundFill(Color.rgb(30, 30, 30, 0.75), null, null))
        }

        val imageLoader = UrlImageLoader()
        val thumbnailCache = WeakHashMap(mutableMapOf<Record<ImageUrl>, SoftReference<ThumbnailFlowPane.Thumbnail>>())

        fun createAndCacheThumbnail(record: Record<ImageUrl>): ThumbnailFlowPane.Thumbnail =
                ImageThumbnail(imageLoader.getImage(record.key.url, 500.0, 200.0, true), parserProperty.get(), tagNodeFactoryProperty.get()).apply {
                    setTags(record.itemTags)
                    tags.addListener({ _, _, new -> updateTagsProperty.value?.invoke(record, new) })
                    view.onMouseClicked = EventHandler {
                        overlay.imageProperty.value = imageLoader.getImage(record.key.url)
                        overlay.isVisible = true
                    }
                    thumbnailCache[record] = SoftReference(this as ThumbnailFlowPane.Thumbnail)
                }

        fun getThumbnail(record: Record<ImageUrl>): ThumbnailFlowPane.Thumbnail = thumbnailCache[record]?.get()
                ?: createAndCacheThumbnail(record)

        val pane = ThumbnailFlowPane().apply {
            thumbnails.value = TransformedList(records, ::getThumbnail)
        }
        children.addAll(pane, overlay)
    }
}