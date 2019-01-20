package com.github.am4dr.rokusho.library

import com.github.am4dr.rokusho.util.event.EventPublisher
import kotlin.reflect.KClass

interface Library<T : Any> : EventPublisher<Library.Event> {

    val type: KClass<T>
    val name: String
    val shortName: String

    fun getItems(): List<LibraryItem<out T>>
    fun getTags(): Set<LibraryItemTagTemplate>
    fun contains(item: LibraryItem<*>): Boolean

    fun parseItemTag(text: String): LibraryItemTag?

    sealed class Event {
        class AddItem<T : Any>(val item: LibraryItem<T>) : Event()
        class RemoveItem<T : Any>(val item: LibraryItem<T>) : Event()
        class UpdateItem<T : Any>(val item: LibraryItem<T>) : Event()

        class AddTag(val tag: LibraryItemTagTemplate) : Event()
        class RemoveTag(val tag: LibraryItemTagTemplate) : Event()
        class UpdateTag(val tag: LibraryItemTagTemplate) : Event()
    }
}