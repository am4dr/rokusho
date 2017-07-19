package com.github.am4dr.rokusho.core.library

import javafx.beans.property.ReadOnlySetProperty
import javafx.beans.property.ReadOnlySetWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableSet

class DefaultTagRegistry(observableSet: ObservableSet<Tag> = FXCollections.observableSet()) : TagRegistry {
    private val _tags = ReadOnlySetWrapper(observableSet)
    override val tags: ReadOnlySetProperty<Tag> = _tags.readOnlyProperty
    override fun put(tag: Tag) { _tags.add(tag) }
    override fun get(id: String): Tag? = _tags.find { it.id == id }
    override fun remove(id: String) { _tags.removeIf { it.id == id } }
}