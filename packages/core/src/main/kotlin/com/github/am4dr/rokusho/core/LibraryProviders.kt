package com.github.am4dr.rokusho.core

interface LibraryProviders {

    fun getLibrary(descriptor: LibraryDescriptor): Library?
}