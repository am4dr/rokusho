package com.github.am4dr.rokusho.core.library.provider

import com.github.am4dr.rokusho.core.library.Library

class LibraryProviderCollection(providers: Set<LibraryProvider<*>>,
                                override val name: String = "LibraryProviderCollection",
                                override val description: String = "aggregated LibraryProviders") : LibraryProvider<Any> {

    val providers: Set<LibraryProvider<*>> = providers.toSet()

    override fun isAcceptable(descriptor: LibraryDescriptor): Boolean = providers.any { it.isAcceptable(descriptor) }
    override fun get(descriptor: LibraryDescriptor): Library<out Any>? = providers.find { isAcceptable(descriptor) }?.get(descriptor)
}
