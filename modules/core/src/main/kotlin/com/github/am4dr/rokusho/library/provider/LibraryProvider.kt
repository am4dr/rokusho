package com.github.am4dr.rokusho.library.provider

import com.github.am4dr.rokusho.library.Library

interface LibraryProvider<T : Any> {

    val name: String
    val description: String

    fun isAcceptable(descriptor: LibraryDescriptor): Boolean
    fun get(descriptor: LibraryDescriptor): Library<out T>?
}
