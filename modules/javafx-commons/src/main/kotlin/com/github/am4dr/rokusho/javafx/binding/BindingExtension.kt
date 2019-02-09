package com.github.am4dr.rokusho.javafx.binding


import javafx.beans.Observable
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

/**
 * [createObjectBinding]を省略できる[Property.bind]
 */
fun <T> Property<T>.bind(supplier: () -> T, vararg dependencies: Observable) {
    bind(createObjectBinding(supplier, dependencies))
}

/**
 * 明示的な[java.util.concurrent.Callable]や[arrayOf]を省略できる[createObjectBinding]
 */
fun <T> createBinding(supplier: () -> T, vararg dependencies: Observable): ObjectBinding<T> =
    createObjectBinding(supplier, dependencies)



fun <T, R> ObservableValue<T>.map(function: (T) -> R): Binding<R> =
    createBinding({ function(value) }, this)



operator fun <T, R, F : (T) -> R> ObservableValue<F?>.invoke(param: T): R? =
    value?.invoke(param)

operator fun <T, R, F : (T) -> R> ObservableValue<F?>.invoke(param: T, default: () -> R): R =
    value?.invoke(param) ?: default()

operator fun <R, F : () -> R> ObservableValue<F?>.invoke(): R? =
    value?.invoke()

operator fun <R, F : () -> R> ObservableValue<F?>.invoke(default: () -> R): R =
    value?.invoke() ?: default()
