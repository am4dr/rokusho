package com.github.am4dr.rokusho.core.library;

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.collections.transformation.FilteredList

class FilteredLibrary<T>(private val base: Library<T>,
                         private val filter: (T) -> Boolean) : Library<T> {

    private val filteredRecords = FilteredList(base.records) { filter(it.key) }

    override val tags: ReadOnlyMapProperty<String, Tag> = base.tags
    override val records: ReadOnlyListProperty<Record<T>> = ReadOnlyListWrapper(filteredRecords).readOnlyProperty

    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) = base.updateItemTags(key, tags)
}

fun <T> Library<T>.filter(filter: (T) -> Boolean): Library<T> = FilteredLibrary(this, filter)