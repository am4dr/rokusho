package com.github.am4dr.rokusho.library

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlySetProperty
import kotlin.reflect.KClass

interface Library<T : Any> {

    val type: KClass<T>
    val name: String
    val shortName: String

    fun getItems(): ReadOnlyListProperty<LibraryItem<out T>>
    fun getTags(): ReadOnlySetProperty<LibraryItemTagTemplate>
    fun contains(item: LibraryItem<*>): Boolean
    fun parseItemTag(text: String): LibraryItemTag?
}