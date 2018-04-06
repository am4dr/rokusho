package com.github.am4dr.rokusho.javafx.function

import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableObjectValue

fun <T, R> ObservableObjectValue<(T)->R>.bindLeft(t: ObservableObjectValue<out T>): ObservableObjectValue<R> = Bindings.createObjectBinding({ value.invoke(t.value) }, arrayOf(this, t))
fun <T, R> ObservableObjectValue<(T)->R>.bindLeft(t: T): ObservableObjectValue<R> = Bindings.createObjectBinding({ get().invoke(t) }, arrayOf(this))
fun <T, R> ((T)->R).bindLeft(t: ObservableObjectValue<out T>): ObservableObjectValue<R> = Bindings.createObjectBinding({ invoke(t.value) }, arrayOf(t))
