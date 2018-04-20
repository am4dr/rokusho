package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.core.library.Record
import javafx.scene.Node
import java.lang.ref.SoftReference
import java.util.*

class ThumbnailNodeCache {

    private val cache = WeakHashMap(mutableMapOf<Record<*>, SoftReference<Node>>())

    fun set(record: Record<*>, thumbnail: Node) = cache.set(record, SoftReference(thumbnail))

    fun get(record: Record<*>): Node? = cache[record]?.get()

    fun getOrPut(record: Record<*>, thumbnailSupplier: () -> Node): Node = get(record) ?: thumbnailSupplier().also { set(record, it) }
}