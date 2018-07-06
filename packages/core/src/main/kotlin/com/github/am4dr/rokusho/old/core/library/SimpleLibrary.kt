package com.github.am4dr.rokusho.old.core.library

import com.github.am4dr.rokusho.old.core.library.helper.LibrarySupport
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper

class SimpleLibrary<T>(private val support: LibrarySupport<T> = LibrarySupport()) : Library<T> {

    override val tags: ReadOnlyMapProperty<String, Tag> = ReadOnlyMapWrapper(support.tags).readOnlyProperty
    override val records: ReadOnlyListProperty<Record<T>> = ReadOnlyListWrapper(support.records).readOnlyProperty

    override fun updateItemTags(key: T, tags: Iterable<ItemTag>) {
        support.updateItemTags(key, tags)
    }
}