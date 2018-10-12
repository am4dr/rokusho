package com.github.am4dr.rokusho.old.core.library

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyMapProperty
import kotlin.reflect.KClass

interface Library<T : Any> {

    val type: KClass<T>
    val name: String
    val shortName: String get() = name
    val tags: ReadOnlyMapProperty<String, Tag>
    val records: ReadOnlyListProperty<Record<T>>

    fun updateItemTags(key: T, tags: Iterable<ItemTag>)
}
