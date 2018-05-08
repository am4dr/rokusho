package com.github.am4dr.rokusho.gui.viewer

import com.github.am4dr.rokusho.core.library.ItemTag
import com.github.am4dr.rokusho.core.library.Record
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

class RecordThumbnailViewer<T>(private val baseThumbnailFactory: (Record<T>) -> ThumbnailFlowPane.Thumbnail) : StackPane() {

    val records: ReadOnlyListProperty<Record<T>> = SimpleListProperty(observableArrayList())
    val updateTagsProperty: ObjectProperty<(Record<T>, List<ItemTag>) -> Unit> = SimpleObjectProperty { _, _ -> }
    val onActionProperty: ObjectProperty<(List<Record<T>>) -> Unit> = SimpleObjectProperty { _ -> }

    private val thumbnailCache = MutableValueCache(::createThumbnail)
    private val thumbnails = TransformedList(records, thumbnailCache::getOrPut)
    init {
        children.add(ThumbnailFlowPane().apply {
            thumbnails.bindContent(this@RecordThumbnailViewer.thumbnails)
        })
    }

    private fun createThumbnail(record: Record<T>): ThumbnailFlowPane.Thumbnail =
            OverlayThumbnailDecorator(baseThumbnailFactory(record), false).apply {
                tags.setAll(record.itemTags)
                onEditEndedProperty.set { new -> updateTagsProperty.value?.invoke(record, new) }
                setOnMouseClicked {
                    onActionProperty.get()?.invoke(listOf(record))
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