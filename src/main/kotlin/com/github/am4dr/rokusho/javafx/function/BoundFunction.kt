package com.github.am4dr.rokusho.javafx.function

import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableObjectValue

class BoundFunction<T, R>(t: ObservableObjectValue<T>, function: ObservableObjectValue<(T) -> R>): ObservableObjectValue<R> {

    constructor(t: ObservableObjectValue<T>, function: (T) -> R) : this(t, SimpleObjectProperty(function))
    constructor(t: T, function: ObservableObjectValue<(T) -> R>) : this(SimpleObjectProperty(t), function)

    private val binding = Bindings.createObjectBinding({ function.value.invoke(t.value) }, arrayOf(t, function))

    override fun removeListener(listener: InvalidationListener?) = binding.removeListener(listener)
    override fun removeListener(listener: ChangeListener<in R>?) = binding.removeListener(listener)
    override fun get(): R = binding.get()
    override fun addListener(listener: InvalidationListener?) = binding.addListener(listener)
    override fun addListener(listener: ChangeListener<in R>?) = binding.addListener(listener)
    override fun getValue(): R = binding.value
}
