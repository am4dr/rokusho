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

class RecordThumbnailViewer<T>(private val thumbnailFactory: (Record<T>, Double, Double) -> ThumbnailFlowPane.Thumbnail,
                               val thumbnailMaxWidth: Double = 500.0, val thumbnailMaxHeight: Double = 200.0) : StackPane() {

    val records: ReadOnlyListProperty<Record<T>> = SimpleListProperty(observableArrayList())
    val updateTagsProperty: ObjectProperty<(Record<T>, List<ItemTag>) -> Unit> = SimpleObjectProperty { _, _ -> }
    val onActionProperty: ObjectProperty<(List<Record<T>>) -> Unit> = SimpleObjectProperty { _ -> }
    val parserProperty: ObjectProperty<(String) -> ItemTag> = SimpleObjectProperty { text: String -> ItemTag(Tag(text, Tag.Type.TEXT, mapOf("value" to text)), null) }
    val tagNodeFactoryProperty: ObjectProperty<(ItemTag) -> TagView> = SimpleObjectProperty { tag: ItemTag -> TagNode(tag).view }

    private val thumbnailCache = MutableValueCache(::createThumbnail)
    private val thumbnails = TransformedList(records, thumbnailCache::getOrPut)
    init {
        children.add(ThumbnailFlowPane().apply {
            thumbnails.bindContent(this@RecordThumbnailViewer.thumbnails)
        })
    }

    private fun createThumbnail(record: Record<T>): ThumbnailFlowPane.Thumbnail {
        val thumbnail = thumbnailFactory(record, thumbnailMaxWidth, thumbnailMaxHeight)
        return OverlayThumbnailDecorator(thumbnail, parserProperty.get(), tagNodeFactoryProperty.get(), false).apply {
            tags.setAll(record.itemTags)
            onEditEndedProperty.set { new -> updateTagsProperty.value?.invoke(record, new) }
            setOnMouseClicked {
                onActionProperty.get()?.invoke(listOf(record))
            }
        }
    }

    /**
     * @param K key which must be immutable class but must not be Flyweight pattern
     * @param V value which may be mutable class
     */
    internal class MutableValueCache<K, V>(private val function: (K) -> V) {
        private val cache = WeakHashMap(mutableMapOf<K, SoftReference<V>>())

        operator fun get(key: K): V? {
            // ignore K::equals because of the mutability of V
            if (cache.keys.all { it !== key }) return  null
            return cache[key]?.get()
        }

        fun getOrPut(key: K): V = get(key) ?: function(key).also { set(key, it) }

        operator fun set(key: K, value: V) {
            cache[key] = SoftReference(value)
        }
    }
}