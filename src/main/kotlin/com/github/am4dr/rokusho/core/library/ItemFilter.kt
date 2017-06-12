package com.github.am4dr.rokusho.core.library

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

class ItemFilter<T> {
    val inputProperty: ObjectProperty<String?> = SimpleObjectProperty()
    val filterProperty: ReadOnlyObjectProperty<(Item<T>) -> Boolean>

    init {
        filterProperty = SimpleObjectProperty()
        filterProperty.bind(object : ObjectBinding<(Item<T>) -> Boolean>() {
            init { super.bind(inputProperty) }
            override fun computeValue(): (Item<T>) -> Boolean = { item ->
                val text = inputProperty.get()
                if (text == null || text == "") { true }
                else { item.itemTags.find { it.name.contains(text) } != null }
            }
        })
    }
}