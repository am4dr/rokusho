package com.github.am4dr.rokusho.core.provider

interface NamedProvider<T, R> {

    val name: String
    val description: String

    fun isAcceptable(descriptor: T): Boolean
    fun get(descriptor: T): R?
}