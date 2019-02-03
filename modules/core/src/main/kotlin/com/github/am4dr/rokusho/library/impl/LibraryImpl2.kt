package com.github.am4dr.rokusho.library.impl

import com.github.am4dr.rokusho.library.Library
import com.github.am4dr.rokusho.library.LibraryItem
import com.github.am4dr.rokusho.library.LibraryItemTag
import com.github.am4dr.rokusho.library.LibraryItemTagTemplate
import com.github.am4dr.rokusho.util.event.EventPublisher
import com.github.am4dr.rokusho.util.event.EventPublisherSupport
import kotlin.reflect.KClass

class LibraryImpl2<T : Any>(
    override val type: KClass<T>,
    override val name: String,
    override val shortName: String,
    private val eventSupport: EventPublisherSupport<Library.Event>
) : Library<T>, EventPublisher<Library.Event> by eventSupport {

    private val items = mutableListOf<LibraryItem<out T>>()
    private val tags = mutableSetOf<LibraryItemTagTemplate>()

    override fun getItems(): List<LibraryItem<out T>> = items.toList()
    override fun getTags(): Set<LibraryItemTagTemplate> = tags.toSet()
    override fun contains(item: LibraryItem<*>) = items.contains(item)

    override fun parseItemTag(text: String): LibraryItemTag? {
        return parseAsTextTag(text)
    }
    private fun parseAsTextTag(text: String): LibraryItemTag? {
        val trimmedText = text.trim()
        return tags.find { it.name == trimmedText }
            ?.let { LibraryItemTagByTemplate(it) }
            ?: SimpleItemTag(trimmedText, mapOf())
    }

    private class SimpleItemTag(
        override val name: String,
        data: Map<String, String>
    ) : LibraryItemTag {
        private val data = data.toMutableMap()
        override val entries: Set<Pair<String, String>> get() = data.toList().toSet()
        override val entryNames: Set<String> get() = data.keys
        override fun get(value: String): String? = data[value]
    }

    private class LibraryItemTagByTemplate(val template: LibraryItemTagTemplate) : LibraryItemTag {
        override val name: String
            get() = template.name
        override val entries: Set<Pair<String, String>>
            get() = template.entries
        override val entryNames: Set<String>
            get() = template.entryNames

        override fun get(value: String): String? = template.get(value)
    }
}