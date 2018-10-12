package com.github.am4dr.rokusho.core.library.provider

import com.github.am4dr.rokusho.core.library.Library

interface LibraryProvider<T : Any> {

    val name: String
    val description: String

    fun isAcceptable(descriptor: LibraryDescriptor): Boolean
    fun get(descriptor: LibraryDescriptor): Library<out T>?
}
