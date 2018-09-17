package com.github.am4dr.rokusho.gui.thumbnail

import java.lang.ref.SoftReference
import java.util.*

class MemoizedThumbnailFactory<T>(thumbnailFactory: (T)->ThumbnailFlowPane.Thumbnail) : (T)->ThumbnailFlowPane.Thumbnail {

    private val thumbnailCache = MutableValueCache(thumbnailFactory)

    override fun invoke(p1: T): ThumbnailFlowPane.Thumbnail = thumbnailCache.getOrPut(p1)

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