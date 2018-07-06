package com.github.am4dr.rokusho.core

class DefaultLibraryProvidersImpl(val providers: Set<LibraryProvider>) : LibraryProviders {

    override fun getLibrary(descriptor: LibraryDescriptor): Library? {
        for (provider in providers) {
            if (provider.isAcceptable(descriptor)) {
                return provider.get(descriptor) ?: continue
            }
        }
        return null
    }
}
