package com.github.am4dr.rokusho.core.library

import kotlin.reflect.KClass

sealed class StandardLibraryProviderDescriptors : LibraryProviderDescriptor {

    companion object {
        fun of(string: String): StandardLibraryProviderDescriptors = StringDescriptor(string)
        fun of(clazz: KClass<*>): StandardLibraryProviderDescriptors = FQCNDescriptor(clazz)
    }

    data class StringDescriptor(override val value: String) : StandardLibraryProviderDescriptors()
    data class FQCNDescriptor(override val value: String) : StandardLibraryProviderDescriptors() {
        constructor(clazz: KClass<*>) : this(clazz.qualifiedName ?: throw IllegalArgumentException())
    }
}
