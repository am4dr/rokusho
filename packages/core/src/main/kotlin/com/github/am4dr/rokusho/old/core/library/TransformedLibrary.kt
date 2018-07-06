package com.github.am4dr.rokusho.old.core.library

import com.github.am4dr.rokusho.javafx.collection.TransformedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty

class TransformedLibrary<B, T>(private val base: Library<B>,
                               private val transform: (B) -> T) : Library<T> {

    private val transformedRecords = TransformedList(base.records) {
        Record(transform(it.key), it.itemTags)
    }

    private fun reverseKey(key: T): B? {
        val tIndex = transformedRecords.indexOfFirst { it.key == key }.takeIf { it >= 0 } ?: return null
        return transformedRecords.source[tIndex]?.key
    }

    override val tags: ReadOnlyMapProperty<String, Tag> = base.tags
    override val records: ReadOnlyListProperty<Record<T>> = ReadOnlyListWrapper(transformedRecords).readOnlyProperty

    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        reverseKey(key)?.let {
            base.updateItemTags(it, tags)
        }
    }
}

fun <B, T> Library<B>.transform(transform: (B) -> T): Library<T> = TransformedLibrary(this, transform)