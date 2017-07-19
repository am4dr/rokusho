package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlySetProperty

interface TagRegistry {
    fun put(tag: Tag)
    fun get(id: String): Tag?
    fun remove(id: String)
    val tags: ReadOnlySetProperty<Tag>
}