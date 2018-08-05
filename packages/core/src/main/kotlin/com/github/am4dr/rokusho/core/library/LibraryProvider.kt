package com.github.am4dr.rokusho.core.library

interface LibraryProvider<T : Any> {

    val name: String
    val description: String

    fun isAcceptable(descriptor: LibraryDescriptor): Boolean
    fun get(descriptor: LibraryDescriptor): Library<out T>?
}
