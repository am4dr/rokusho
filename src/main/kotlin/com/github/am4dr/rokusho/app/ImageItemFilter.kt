package com.github.am4dr.rokusho.app

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

interface ImageItemFilter<T> {
    val filterProperty: ReadOnlyObjectProperty<(ImageItem) -> Boolean>
    val inputProperty: ObjectProperty<T>
}

class StringImageItemFilter : ImageItemFilter<String?> {
    override val inputProperty: ObjectProperty<String?> = SimpleObjectProperty()
    override val filterProperty: ReadOnlyObjectProperty<(ImageItem) -> Boolean>

    init {
        filterProperty = SimpleObjectProperty()
        filterProperty.bind(object : ObjectBinding<(ImageItem) -> Boolean>() {
            init { super.bind(inputProperty) }
            override fun computeValue(): (ImageItem) -> Boolean = { item ->
                val text = inputProperty.get()
                if (text == null || text == "") { true }
                else {item.tags.find { it.id.contains(text) } != null }
            }
        })
    }
}
