package com.github.am4dr.rokusho.gui

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

interface ObservableFilter<InputType, ElementType> {
    val inputProperty: ObjectProperty<InputType?>
    val filterProperty: ReadOnlyObjectProperty<(ElementType) -> Boolean>

}
class SimpleObservableFilter<InputType, ElementType>(filterFactory: (InputType?) -> (ElementType) -> Boolean) : ObservableFilter<InputType, ElementType> {
    override val inputProperty: ObjectProperty<InputType?> = SimpleObjectProperty()
    override val filterProperty: ReadOnlyObjectProperty<(ElementType) -> Boolean>

    init {
        filterProperty = SimpleObjectProperty()
        filterProperty.bind(object : ObjectBinding<(ElementType) -> Boolean>() {
            init { super.bind(inputProperty) }
            override fun computeValue(): (ElementType) -> Boolean = filterFactory.invoke(inputProperty.get())
        })
    }
}