package com.github.am4dr.rokusho.core.library

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

class ItemTagFilter {
    val inputProperty: ObjectProperty<String?> = SimpleObjectProperty()
    val filterProperty: ReadOnlyObjectProperty<(List<ItemTag>) -> Boolean>

    init {
        filterProperty = SimpleObjectProperty()
        filterProperty.bind(object : ObjectBinding<(List<ItemTag>) -> Boolean>() {
            init { super.bind(inputProperty) }
            override fun computeValue(): (List<ItemTag>) -> Boolean = { tags ->
                val text = inputProperty.get()
                if (text == null || text == "") { true }
                else { tags.find { it.name.contains(text) } != null }
            }
        })
    }
}