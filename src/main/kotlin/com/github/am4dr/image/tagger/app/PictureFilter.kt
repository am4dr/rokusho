package com.github.am4dr.image.tagger.app

import com.github.am4dr.image.tagger.core.Picture
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

interface PictureFilter<T> {
    val filterProperty: ReadOnlyObjectProperty<(Picture) -> Boolean>
    val inputProperty: ObjectProperty<T>
}

class StringPictureFilter : PictureFilter<String?> {
    override val inputProperty: ObjectProperty<String?> = SimpleObjectProperty()
    override val filterProperty: ReadOnlyObjectProperty<(Picture) -> Boolean>
    init {
        filterProperty = SimpleObjectProperty()
        filterProperty.bind(object : ObjectBinding<(Picture) -> Boolean>() {
            init { super.bind(inputProperty) }
            override fun computeValue(): (Picture) -> Boolean = { pic ->
                val text = inputProperty.get()
                if (text == null || text == "") { true }
                else { pic.metaData.tags.find { it.name.contains(text) } != null }
            }
        })
    }
}
