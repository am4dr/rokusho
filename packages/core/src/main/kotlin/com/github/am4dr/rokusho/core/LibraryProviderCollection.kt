package com.github.am4dr.rokusho.core

class LibraryProviderCollection(providers: Set<LibraryProvider>,
                                override val name: String = "LibraryProviderCollection",
                                override val description: String = "aggregated LibraryProviders") : LibraryProvider {

    val providers: Set<LibraryProvider> = providers.toSet()

    override fun isAcceptable(descriptor: LibraryDescriptor): Boolean = providers.any { it.isAcceptable(descriptor) }
    override fun get(descriptor: LibraryDescriptor): Library? = providers.find { isAcceptable(descriptor) }?.get(descriptor)
}
