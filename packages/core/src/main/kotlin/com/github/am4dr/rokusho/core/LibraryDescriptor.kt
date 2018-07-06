package com.github.am4dr.rokusho.core

import com.github.am4dr.rokusho.core.provider.ProviderDescriptor

data class LibraryDescriptor(val providerDescriptor: ProviderDescriptor, val libraryCoordinates: String)
