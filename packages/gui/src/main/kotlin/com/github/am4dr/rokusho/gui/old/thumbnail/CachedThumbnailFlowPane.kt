package com.github.am4dr.rokusho.gui.old.thumbnail

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.scene.layout.StackPane
import java.lang.ref.SoftReference
import java.util.*

// TODO キャッシュはファクトリがやるということにしてこのクラスを消す
class CachedThumbnailFlowPane<T>(thumbnailFactory: (T) -> ThumbnailFlowPane.Thumbnail) : StackPane() {

    val records: ReadOnlyListProperty<T> = SimpleListProperty(observableArrayList())

    private val thumbnailCache = MutableValueCache(thumbnailFactory)
    private val thumbnails = TransformedList(records, thumbnailCache::getOrPut)
    init {
        children.add(ThumbnailFlowPane().apply {
            thumbnails.bindContent(this@CachedThumbnailFlowPane.thumbnails)
        })
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